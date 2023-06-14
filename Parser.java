// Yusuf Sallam and Matthew Lerman - ATiCS 22-23 Period 1

import java.text.ParseException;
import java.util.ArrayList;

public class Parser {
	private VariableMap variableMap = new VariableMap();

	public VariableMap getVariableMap() {
		return variableMap;
	}

	//find index of ( that matches with ) **RETURNS INDEX OF ( **
	private int findPairedParenthesis(ArrayList<String> tokens, int idxOfParen) {
		int count = 1; 
		int idx = idxOfParen;
		while(count > 0 && idx > 0) {
			idx--;
			if(tokens.get(idx).equals("(")) {
				count--;
			}
			else if(tokens.get(idx).equals(")")) {
				count++;
			}
		}
		return idx;
	}

	// Checks if two tokens are equal to one another, just a wrapper function
	private boolean checkTokenEqual(ArrayList<String> tokens, int idx, String check) {
		return tokens.get(idx).equals(check);
	}

	// Adds missing parens to lambda expressions so that they can be parsed correctly
	private void addLambdaParenthesis(ArrayList<String> tokens, int idx) {
		int i = idx;
		int count = 0;

		while(idx < tokens.size()) {
			if(checkTokenEqual(tokens, idx, ")") && (--count == -1)) {
				tokens.add(i, "(");
				tokens.add(idx + 1, ")");
				return;
			}
			if(checkTokenEqual(tokens, idx, "(")) count++;
			idx++;
		}

		if(count != 0) { System.out.println("Something is wrong with parsing parens."); return; }
		
		tokens.add(i, "(");
		tokens.add(")");
	}
	
	// Largest function and handles the bulk processing of tokens
	// Conditional outputs/checks depending on what user inputs, so whether it be "run", "populate", or "variable = expression"
	// Outputs the correct alpha reduction of the expression, however if the expression is a variable, it will return the variable and what it was initially defined as
	public Expression handleTokens(ArrayList<String> preparsed_tokens) throws ParseException {
		Executor executor = new Executor();
		Expression exp;

		if(preparsed_tokens.get(0).equals("populate")) {
			int num1 = Integer.parseInt(preparsed_tokens.get(1));
			int num2 = Integer.parseInt(preparsed_tokens.get(2));

			// this is kind of hacky, apologies to whoever reads this in the future
			for(int i = num1; i <= num2; i++) {
				if(variableMap.getVariable(Integer.toString(i)) == null) {
					String parsethis = "(\\f.(\\x." + "(f ".repeat(i) + "x" + ")".repeat(i) + "))";
					ArrayList<String> tokens = Lexer.tokenize(parsethis);
					Expression number_exp =  executor.execute_expression(parse(tokens));
					variableMap.addVariable(new Variable(Integer.toString(i)), number_exp);
				} else {
					System.out.println("[WARN] " + Integer.toString(i) + " is already defined.");
				}
			}

			return new Variable(Integer.toString(num2));
		} else if(preparsed_tokens.get(0).equals("run")) {
			exp = parse(new ArrayList<String>(preparsed_tokens.subList(1, preparsed_tokens.size())));

			Expression result = executor.execute_expression(exp);
			Variable corresponding_var = variableMap.getVarByExp(result);
			
			if(corresponding_var != null) {
				return corresponding_var;
			} else {
				return GetDisplayNames(result);
			}
		} else if(preparsed_tokens.contains("=")) {
			try {
				ArrayList<String> expression_to_parse = new ArrayList<String>(preparsed_tokens.subList(preparsed_tokens.indexOf("=")+1, preparsed_tokens.size()));
				exp = parse(expression_to_parse);

				int assignmentLocation = preparsed_tokens.indexOf("=");
				// TODO - Add check for multiple = signs in the preparsed_tokens --> this was not done, but honestly multiple = signs just breaks anyways so it's fine
				if(assignmentLocation != 1) {
					throw new AssignmentError("Invalid variable assignment. Multiple tokens found prior to `=` sign. Input Tokens: \"" + preparsed_tokens + "\"", assignmentLocation);
				}
				if(preparsed_tokens.contains("run") && (preparsed_tokens.indexOf("run") == preparsed_tokens.indexOf("=") + 1)) {
					Variable newAssignment = new Variable(preparsed_tokens.get(0));
					exp = parse(new ArrayList<String>(preparsed_tokens.subList(preparsed_tokens.indexOf("run")+1, preparsed_tokens.size())));
					variableMap.addVariable(newAssignment, executor.execute_expression(exp));
					System.out.println("[EVALUATED] Added " + executor.execute_expression(exp) + " as " + newAssignment);
					return executor.execute_expression(exp);
				} else {
					Variable newAssignment = new Variable(preparsed_tokens.get(0));
					variableMap.addVariable(newAssignment, exp);
					System.out.println("Added " + exp + " as " + newAssignment);
					return exp;
				}
			} catch (Exception e) {
				throw e;
			}
		} else {
			// in the event we basically just want to echo back stuff to the user, we just return the parsed version of what they gave
			exp = parse(preparsed_tokens);
			if(exp instanceof Variable) {
				Variable definedVar = (Variable) exp;

				if(variableMap.getVariable(definedVar) != null) {
					Expression associated_exp = variableMap.getVariable(definedVar);
					return associated_exp;
				} else {
					return GetDisplayNames(definedVar);
				}
			}

			return GetDisplayNames(exp);
		}
	}

	// necessary since this just makes it easier to parse stuff, adds any missing parens in wherever necessary
	public ArrayList<String> preparse(ArrayList<String> inputtokens) {
		for(int i = 0; i < inputtokens.size(); i++) {
			// checks to see if we should have a paren wrapping a lambda
			if(inputtokens.get(i).equals("\\") && (i == 0 || (!inputtokens.get(i-1).equals("(") || i == 0))) {
				addLambdaParenthesis(inputtokens, i);
			}
		}
		
		return inputtokens;
	}
	
	/*
	 * Turns a set of tokens into an expression.  Comment this back in when you're ready. (we are ready and this is commented back)
	 */

	//  basically just handles combining the tokens together into a big expression that we will later process and handle
	public Expression parse(ArrayList<String> tokens) throws ParseException {
		String curtoken = tokens.get(tokens.size()-1);

		if(tokens.size() == 1) { //only one element, return variable
			if(variableMap.getVariable(curtoken) != null) {
				return variableMap.getVariable(curtoken);
			}
		
			return new Variable(curtoken); 
		}  else if(tokens.size() == 2) { //only two elements, return application of both
			Expression left;
			Expression right;

			// checks to see if given token is already defined as an expression, if so, we just return that expression
			if(variableMap.getVariable(tokens.get(0)) != null) {
				left = variableMap.getVariable(tokens.get(0));
			} else {
				left = new Variable(tokens.get(0));
			}

			if(variableMap.getVariable(tokens.get(1)) != null) {
				right = variableMap.getVariable(tokens.get(1));
			} else {
				right = new Variable(tokens.get(1));
			}

			return new Application(left, right);
		} else if(curtoken.equals(")")) { 
			int pairedparenidx = findPairedParenthesis(tokens, tokens.size()-1);
			if(tokens.get(pairedparenidx + 1).equals("\\")) { //detect and return lambda function
				if(tokens.indexOf("\\") == 1 && (pairedparenidx == 0)) { //just lambda left
					return new Function(new Variable(tokens.get(pairedparenidx + 2)), parse(new ArrayList<String>(tokens.subList(pairedparenidx + 4, tokens.size()-1))));
				}
				return new Application(parse(new ArrayList<String>(tokens.subList(0, pairedparenidx))), parse(new ArrayList<String>(tokens.subList(pairedparenidx, tokens.size()))));
				
			} else if(pairedparenidx == 0) { //simplify unnecessary end parenthesis 
				return parse(new ArrayList<String>(tokens.subList(1, tokens.size()-1)));
			} else {
				return new Application(parse(new ArrayList<String>(tokens.subList(0, pairedparenidx))), parse(new ArrayList<String>(tokens.subList(pairedparenidx + 1, tokens.size()-1))));
			}
		}
		else {
			Expression right;
			if(variableMap.getVariable(curtoken) != null) {
				right = variableMap.getVariable(curtoken);
			} else {
				right = new Variable(curtoken);
			}

			return new Application(parse(new ArrayList<String>(tokens.subList(0, tokens.size()-1))), right);
		}
	}

	// "hacks" in alpha reductions, since code was originally structured with ids instead of alpha reductions, basically just goes in and subs things for their displaynames (alphareduced names)
	public Expression GetDisplayNames(Expression exp) {
		if(exp instanceof Application a) {
			return new Application(GetDisplayNames(a.left), GetDisplayNames(a.right));
		} else if(exp instanceof Function f) {
			return new Function(new Variable(f.var.getDisplayName()), GetDisplayNames(f.exp));
		} else if(exp instanceof Variable v) {
			return new Variable(v.getDisplayName());
		}

		return null;
	}
}
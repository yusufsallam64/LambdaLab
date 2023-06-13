
import java.text.ParseException;
import java.util.ArrayList;

public class Parser {
	VariableMap variableMap = new VariableMap();

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

	private void addLambdaParenthesis(ArrayList<String> tokens, int idx) {
		int i = idx;
		int count = 0;

		while(idx < tokens.size()) {
			if(tokens.get(idx).equals("(")) {
				count++;
			} else if(tokens.get(idx).equals(")")) {
				count--;

				if(count == -1) {
					tokens.add(i, "(");
					tokens.add(idx + 1, ")");
					return;
				}
			}
			idx++;
		}

		if(count == 0) {
			tokens.add(i, "(");
			tokens.add(")");
			return;
		}
		
		System.out.println("ERROR IN PARENTHESIS");
		return;
		
	}
	
	public Expression handleTokens(ArrayList<String> preparsed_tokens) throws ParseException {
		Executor executor = new Executor();
		Expression exp;

		if(preparsed_tokens.get(0).equals("populate")) {
			int num1 = Integer.parseInt(preparsed_tokens.get(1));
			int num2 = Integer.parseInt(preparsed_tokens.get(2));

			for(int i = num1; i <= num2; i++) {
				String parsethis = "(\\f.(\\x." + "(f ".repeat(i) + "x" + ")".repeat(i) + "))";
					
				ArrayList<String> tokens = Lexer.tokenize(parsethis);

				variableMap.addVariable(new Variable(Integer.toString(i)), executor.execute_expression(parse(tokens)));
			}

			return new Variable(Integer.toString(num2));
		} else if(preparsed_tokens.get(0).equals("run")) {
			exp = parse(new ArrayList<String>(preparsed_tokens.subList(1, preparsed_tokens.size()))); // TODO --> add expression stuff in here

			// just so that if running something gives a defined var, you return the name of that var 
			// Expression result = exp.run();

			Expression result = executor.execute_expression(exp);
			Variable corresponding_var = variableMap.getVarByExp(result);
			
			if(corresponding_var != null) {
				return corresponding_var;
			} else {
				return result;
			}
			// return exp.run();
		} else if(preparsed_tokens.contains("=")) {
			try {
				ArrayList<String> expression_to_parse = new ArrayList<String>(preparsed_tokens.subList(preparsed_tokens.indexOf("=")+1, preparsed_tokens.size()));
				exp = parse(expression_to_parse);

				int assignmentLocation = preparsed_tokens.indexOf("=");
				// TODO --> Add check for multiple = signs in the preparsed_tokens
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
			exp = parse(preparsed_tokens);
			if(exp instanceof Variable) {
				Variable definedVar = (Variable) exp;

				if(variableMap.getVariable(definedVar) != null) {
					Expression associated_exp = variableMap.getVariable(definedVar);
					return associated_exp;
				} else {
					return definedVar;
				}
			}

			return exp;
		}
	}

	public ArrayList<String> preparse(ArrayList<String> inputtokens) {
		for(int i = 0; i < inputtokens.size(); i++) {
			if(inputtokens.get(i).equals("\\") && (i == 0 || (!inputtokens.get(i-1).equals("(") || i == 0))) {
				addLambdaParenthesis(inputtokens, i);
			}
		}
		
		return inputtokens;
	}
	
	/*
	 * Turns a set of tokens into an expression.  Comment this back in when you're ready.
	 */
	public Expression parse(ArrayList<String> tokens) throws ParseException {
		String curtoken = tokens.get(tokens.size()-1);

		if(tokens.size() == 1) { //only one element, return variable
			if(variableMap.getVariable(tokens.get(0)) != null) {
				return variableMap.getVariable(tokens.get(0));
			}
		
			return new Variable(curtoken); 
		}  else if(tokens.size() == 2) { //only two elements, return application of both
			Expression left;
			Expression right;

			if(variableMap.getVariable(tokens.get(0)) != null) {
				// left = getVariable(new Variable(tokens.get(0)));
				left = variableMap.getVariable(tokens.get(0));
			} else {
				left = new Variable(tokens.get(0));
			}

			if(variableMap.getVariable(tokens.get(1)) != null) {
				// right = getVariable(new Variable(tokens.get(1)));
				right = variableMap.getVariable(tokens.get(1));
			} else {
				right = new Variable(tokens.get(1));
			}

			return new Application(left, right);
		} else if(curtoken.equals(")")) { 
			int pairedparenidx = findPairedParenthesis(tokens, tokens.size()-1);
			if(tokens.get(pairedparenidx + 1).equals("\\")) { //detect and return lambda function
				if(tokens.indexOf("\\") == 1 && (pairedparenidx == 0)) { //just lambda left
					// if there is gonna be an error, it is most likely with this function variable although not sure
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
}

import java.text.ParseException;
import java.util.ArrayList;

public class Parser {
	VariableMap variableMap = new VariableMap();

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

	private int findPairedParenthesisClosing(ArrayList<String> tokens, int idxOfParen) {
		int count = 1; 
		int idx = idxOfParen;
		while(count > 0 && idx < tokens.size()-1) {
			idx++;
			if(tokens.get(idx).equals("(")) {
				count++;
			}
			else if(tokens.get(idx).equals(")")) {
				count--;
			}
		}
		return idx;
	}
	
	public Expression handleTokens(ArrayList<String> preparsed_tokens) throws ParseException {
		Expression exp;

		if(preparsed_tokens.get(0).equals("run")) {
			exp = parse(new ArrayList<String>(preparsed_tokens.subList(1, preparsed_tokens.size())));
			System.out.println("Expression given: " + exp);

			// just so that if running something gives a defined var, you return the name of that var 
			Expression result = exp.run();
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
					variableMap.addVariable(newAssignment, exp.run());
					System.out.println("[EVALUATED] Added " + exp.run() + " as " + newAssignment);
					return exp.run();
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
		for(int i = inputtokens.size(); i --> 0;) {
			if(i >= 1) {
				if(inputtokens.get(i).equals("\\") && !(inputtokens.get(i-1).equals("("))) {
					inputtokens.add(i, "(");

					int indexofnextparen = inputtokens.subList(i, inputtokens.size()).indexOf(")") + i + 1;

					if(inputtokens.subList(i, inputtokens.size()).indexOf(")") == -1) {
						inputtokens.add(")");
					} else {
						// iffy about this branch, might have a bug?
						inputtokens.add(indexofnextparen-1, ")");
					}
				}
			} else {
				if(inputtokens.get(0).equals("\\")) {
					inputtokens.add(0, "(");

					int indexofnextparen = findPairedParenthesisClosing(new ArrayList<String>(inputtokens.subList(i, inputtokens.size())), 0);

					if(indexofnextparen == inputtokens.size()-1) {
						inputtokens.add(")");
					} else {
						inputtokens.add(indexofnextparen-1, ")");
					}		
				}
			}
		}

		System.out.println("PREPARSER: " + inputtokens);
		return inputtokens;
	}

	/*
	 * Turns a set of tokens into an expression.  Comment this back in when you're ready.
	 */
	// TODO --> Made it so that this fetches the variables for stuff, and if it isn't defined already then it just returns a new variable?
	public Expression parse(ArrayList<String> tokens) throws ParseException {
		String curtoken = tokens.get(tokens.size()-1);

		if(tokens.size() == 1) { //only one element, return variable
			return new Variable(curtoken); 
		} else if(tokens.size() == 2) { //only two elements, return application of both
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
				// right = getVariable(new Variable(curtoken));
				right = variableMap.getVariable(curtoken);
			} else {
				right = new Variable(curtoken);
			}

			return new Application(parse(new ArrayList<String>(tokens.subList(0, tokens.size()-1))), right);
		}
	}
}
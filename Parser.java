
import java.text.ParseException;
import java.util.ArrayList;
public class Parser {
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

					int indexofnextparen = inputtokens.subList(i, inputtokens.size()).indexOf(")") + i + 1;
					if(indexofnextparen == 0) {
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
	public Expression parse(ArrayList<String> tokens) throws ParseException {
		String curtoken = tokens.get(tokens.size()-1);

		if(tokens.size() == 1) { //only one element, return variable
			return new Variable(curtoken); 
		} else if(tokens.size() == 2) { //only two elements, return application of both
			return new Application(new Variable(tokens.get(0)), new Variable(tokens.get(1)));
		} else if(curtoken.equals(")")) { 
			int pairedparenidx = findPairedParenthesis(tokens, tokens.size()-1);
			if(tokens.get(pairedparenidx + 1).equals("\\")) { //detect and return lambda function
				if(tokens.indexOf("\\") == 1) { //just lambda left
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
			return new Application(parse(new ArrayList<String>(tokens.subList(0, tokens.size()-1))), new Variable(curtoken));
		}
		// Variable var = new Variable(parse());
		// This is nonsense code, just to show you how to thrown an Exception.
		// To throw it, type "error" at the console.
		// if (var.toString().equals("error")) {
		// 	throw new ParseException("User typed \"Error\" as the input!", 0);
		// }
		// (/a.b c)
		// return var;
	}
}

// (((a)) c)
// 
// (a b) (c d)
// (e f) (a (b c) d)

// a b (c d) (e (f g) h)
// (((a b) (c d)) ((e (f g)) h)))

// (a b c) --> [a b]
// ((a b)  c) --> [[a b] c]
// (a (b c))
// ( ( a b ) ( c d ) )
// a b (c d) (e (f g) h) --> ANSWER: (((a b) (c d)) ((e (f g)) h))

// BROKEN ONES:
// \f.(f x)
// λf.(f x)
//    (λf.(f x))
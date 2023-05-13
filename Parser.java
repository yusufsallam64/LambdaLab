
import java.text.ParseException;
import java.util.ArrayList;

public class Parser {
	public ArrayList<String> preparse(ArrayList<String> inputtokens) {
		for(int i = inputtokens.size(); i --> 0;) {
			if(i >= 1) {
				if(inputtokens.get(i).equals("\\") && !(inputtokens.get(i-1).equals("("))) {
					inputtokens.add(i, "(");

					int indexofnextparen = inputtokens.subList(i, inputtokens.size()).indexOf(")") + i + 1;
					if(indexofnextparen == -1) {
						inputtokens.add(")");
					} else {
						inputtokens.add(indexofnextparen-1, ")");
					}
				}
			} else {
				if(inputtokens.get(0).equals("\\")) {
					inputtokens.add(0, "(");

					int indexofnextparen = inputtokens.subList(i, inputtokens.size()).indexOf(")");
					if(indexofnextparen == -1) {
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
		Variable var = new Variable(tokens.get(0));
		
		// This is nonsense code, just to show you how to thrown an Exception.
		// To throw it, type "error" at the console.
		if (var.toString().equals("error")) {
			throw new ParseException("User typed \"Error\" as the input!", 0);
		}
		
		return var;
	}
}

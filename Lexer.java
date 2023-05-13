
import java.util.ArrayList;
import java.util.Arrays;
// import java.util.regex.Matcher;
// import java.util.regex.Pattern;
import java.util.List;

public class Lexer {
	
	/*
	 * A lexer (or "tokenizer") converts an input into tokens that
	 * eventually need to be interpreted.
	 * 
	 * Given the input 
	 *    (\bat  .bat flies)cat  λg.joy! )
	 * you should output the ArrayList of strings
	 *    [(, \, bat, ., bat, flies, ), cat, \, g, ., joy!, )]
	 *
	 */

	// semi-colon excluded because we no longer treat it as a special token in the lexer
	private final String regex = "()\\λ.=";

	public ArrayList<String> tokenize(String input) {
		ArrayList<String> tokens = new ArrayList<String>();
		StringBuilder currentstr = new StringBuilder();

		for(int x = 0; x < input.length(); x++) {
			String symbol = Character.toString(input.charAt(x));

			if(symbol.equals(";")) return tokens;
			
			if(regex.contains(symbol)) {
				if(!currentstr.isEmpty()) {
					tokens.add(currentstr.toString());
					currentstr = new StringBuilder();
				}

				tokens.add(symbol);
			} else if(symbol.equals(" ") || symbol.equals("\n") || symbol.equals("\t")) {
				if(!currentstr.isEmpty()) {
					tokens.add(currentstr.toString());
					currentstr = new StringBuilder();
				}
			} else {
				currentstr.append(symbol);
			}
		}

		if(!currentstr.isEmpty()) {
			tokens.add(currentstr.toString());
		}

		return tokens;
	}
}

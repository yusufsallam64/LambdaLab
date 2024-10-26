// Yusuf Sallam and Matthew Lerman - ATiCS 22-23 Period 1

import java.util.ArrayList;
// import java.util.regex.Matcher;
// import java.util.regex.Pattern;

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
	private final static String regex = "()\\λ.=";


	public static ArrayList<String> tokenize(String input) {
		ArrayList<String> tokens = new ArrayList<String>();
		StringBuilder currentstr = new StringBuilder();

		for(int x = 0; x < input.length(); x++) {
			String symbol = Character.toString(input.charAt(x));
			
			if(symbol.equals(";")) {
				if(!currentstr.isEmpty()) {
					tokens.add(currentstr.toString());
				}
				return tokens;
			}
			
			if(regex.contains(symbol)) {
				if(!currentstr.isEmpty()) {
					tokens.add(currentstr.toString());
					currentstr = new StringBuilder();
				}

				tokens.add(symbol);
			// incredibly long just because macOS made it annoying to copy since ascii has 2 different values for space??
			} else if( Integer.toHexString((int) symbol.charAt(0)).equals("a0") || Integer.toHexString((int) symbol.charAt(0)).equals("20") || symbol.equals("\n") || symbol.equals("\t")) {
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

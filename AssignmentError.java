public class AssignmentError extends java.text.ParseException {
    public AssignmentError(String message, int assignmentLocation) {
        super(message, assignmentLocation);
    }
}


// ArrayList<String> preparsed_tokens = parser.preparse(tokens);

// Expression exp;
// if(preparsed_tokens.contains("=")) {
//     try {
//         exp = parser.parse(new ArrayList<String>(preparsed_tokens.subList(preparsed_tokens.indexOf("=")+1, preparsed_tokens.size())));
//         // parser.addVariable(new Variable(preparsed_tokens.get(preparsed_tokens.indexOf("=")-1)), exp);
//         if(preparsed_tokens.indexOf("=") != 1) {
//             throw new AssignmentError("Invalid variable assignment. Multiple tokens found prior to = sign. Input: \"" + input + "\"");
//         }
//         parser.addVariable(new Variable(preparsed_tokens.get(0)), exp);

//     } catch (Exception e) {
//         System.out.println("Error assigning variable, input was: \"" + input + "\"");
//     }
// } else {
//     exp = parser.parse(preparsed_tokens);
//     output = exp.toString();
//     System.out.println(output);
// }
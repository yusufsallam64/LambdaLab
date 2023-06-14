// Yusuf Sallam and Matthew Lerman - ATiCS 22-23 Period 1

import java.util.Set;

/*
 * Interface designed to be implemented by all expression sub-types (Functions, Applications, Variables)
 */
public interface Expression extends Runner {
    public String toString();
    public Expression run();
    public Expression substitute(Variable varToReplace, Expression replace_exp);
    public boolean checkForVariableName(Variable varToReplace, Expression exp);
    public Set<String> LoopTillVariable(Expression e, Set<String> variables);
    public void printExpression(Expression exp);
}


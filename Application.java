// Yusuf Sallam and Matthew Lerman - ATiCS 22-23 Period 1

import java.util.HashSet;
import java.util.Set;

public class Application implements Expression {
    // marked as public because we need reference not value
    public Expression left;
    public Expression right;

    public Application(Expression left, Expression right) {
        assert left != null;
        assert right != null;
        
        this.left = left;
        this.right = right;
    }

    public String toString() {
        return "(" + left + " " + right + ")";
    }

    public Expression getLeft() {
        return this.left;
    }

    public Expression getRight() {
        return this.right;
    }

    public Expression run() {
        if(this.left instanceof Function) {
            Function leftSide = (Function) this.left;
            
            leftSide.fixVariableIdentifiers(); // TODO --> I believe this is the bottleneck for everything

            return leftSide.substitute(((Function) left).getVar(), this.getRight()).run();
        } else {
            Application returnApp = new Application(this.left.run(), this.right.run());
            
            // detects a redex after running both sides of app
            if(returnApp.getLeft() instanceof Function lhsFn) {
                return lhsFn.substitute(lhsFn.getVar(), returnApp.getRight()).run();
            } else {
                return returnApp;
            }
        }
    }

    public Expression substitute(Variable varToReplace, Expression replaceExp) {
        return new Application(left.substitute(varToReplace, replaceExp), right.substitute(varToReplace, replaceExp));
    }

    // debug function meant for QOL purposes
    public void printExpression(Expression exp) {
        if(exp instanceof Variable) {
            System.out.println("[Variable : " + exp + " : " + ((Variable) exp).getID() + "]");
        } else if (exp instanceof Function) {
            System.out.println("Function: " + exp);
            System.out.println("[Function Variable : " + ((Function) exp).getVar() + " : " + ((Function) exp).getVar().getID() + "]");
            printExpression(((Function) exp).getExp());
        } else if (exp instanceof Application) {
            System.out.println("Application: " + exp);
            printExpression(((Application) exp).getLeft());
            printExpression(((Application) exp).getRight());
        }
    }

    // returns whether a given variable name is already in use for alpha reductions
    public boolean checkForVariableName(Variable v, Expression e) {
        Set<String> var_names = new HashSet<String>();
        
        var_names = LoopTillVariable(e, var_names);
        // if(var_names.contains(v.toString())) { // not sure if this should be toString?
        if(var_names.contains(v.getDisplayName())) {
            return true;
        }
        return false;
    }

    // fetches the variables for a given expression
    public Set<String> LoopTillVariable(Expression e, Set<String> variables) {
        if(e instanceof Function f) {
            LoopTillVariable(f.getVar(), variables);
            LoopTillVariable(f.getExp(), variables);
        }

        if(e instanceof Application a) {
            LoopTillVariable(a.getLeft(), variables);
            LoopTillVariable(a.getRight(), variables);
        }

        // if(!variables.contains(e.toString())) { // not sure if this should be toString?
        if(!variables.contains(((Variable) e).getDisplayName())) {
            variables.add(((Variable) e).getDisplayName());
        };
        
        return variables;
    }
}
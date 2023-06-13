public class Application implements Expression {
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
        System.out.println("[APPLICATION]: " + this);
        if(this.left instanceof Function) {
            //System.out.println("HITS THIS BRANCH");
            //System.out.println("[APPLICATION] CURRENT: " + this);

            Function leftSide = (Function) this.left;
            
            leftSide.fixVariableIdentifiers(); // TODO --> i believe this is the bottleneck for everything

            //System.out.println("[LHS]: " + leftSide);
            //System.out.println("[RHS]: " + this.right);

            //printExpression(this);

            Expression substituted = leftSide.substitute(((Function) left).getVar(), this.getRight());
            //System.out.println("[SUBSTITUTED]: " + substituted);

            Expression evalled = substituted.run();

            //System.out.println("[EVALLED]: " + evalled);

            // System.out.println("[EXPRESSION] PreSub: " + leftSide);
            // System.out.println("[EVALUATED] EvalledSub: " + evalled);
            Expression whatwegotback = evalled;
            // System.out.println("[EXPRESSION] PostSub: " + whatwegotback);
            // if(whatwegotback instanceof Function funcreturned) {
            //     return recurseUntilApp(whatwegotback);
            // }
            // System.out.println("[APPLICARETU]: " + whatwegotback);
            return whatwegotback;
        }
        else {
            Application returnApp = new Application(this.left.run(), this.right.run());
            // System.out.println("[APPLICATION] ReturningApp: " + returnApp);
            if(returnApp.getLeft() instanceof Function) {
                Expression back = returnApp.getLeft().substitute(((Function) returnApp.getLeft()).getVar(), returnApp.getRight()).run();
                // System.out.println("[REDEX] Returning: " + back);
                // System.out.println("[APPLICARETU]: " + back);
                return back;
            } else {
                // System.out.println("[RECURSE] Returning: " + returnApp);
                // System.out.println("[APPLICARETU]: " + returnApp);
                return returnApp;
            }
        }
    }

    public Expression substitute(Variable varToReplace, Expression replaceExp) {
        return new Application(left.substitute(varToReplace, replaceExp), right.substitute(varToReplace, replaceExp));
    }

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
}
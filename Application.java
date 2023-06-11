public class Application implements Expression {
    private Expression left;
    private Expression right;

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
            leftSide.fixVariableIdentifiers();
            Expression whatwegotback = leftSide.substitute(((Function) left).getVar(), this.getRight()).run();
            
            return whatwegotback;
        }
        else {
            Application returnApp = new Application(this.left.run(), this.right.run());
            if(returnApp.getLeft() instanceof Function) {
                return returnApp.getLeft().substitute(((Function) returnApp.getLeft()).getVar(), returnApp.getRight()).run();
            } else {
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
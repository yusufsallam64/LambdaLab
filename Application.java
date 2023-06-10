
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
        System.out.println("APPLICATION: " + this);
        if(left instanceof Function) {
            Function func = (Function) left;
            Expression returned = func.run(right);
            return returned.run();
        } else {
            Expression leftexp = this.left.run();

            if(leftexp instanceof Function) {
                Function func = (Function) leftexp;
                return func.run(this.right.run());
            } else {
                Expression left_run_returned = leftexp.run();
                if(left_run_returned instanceof Function) {
                    return ((Function) leftexp.run()).run(this.right.run());
                } // TODO --> something is funky with this

                // else if (left_run_returned instanceof Application) { // this wrong fo sho
                //     Application app = (Application) left_run_returned; 

                //     return new Application(app.run(), this.right.run());
                // }
                
                // TODO -- We need cases to check to see if we have an application then run the left and right i think
                // else if ()

                return new Application(leftexp.run(), this.right.run());
            }
        }       
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
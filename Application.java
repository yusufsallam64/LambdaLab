
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
        System.out.println("Expression left: " + left + " Expression right: " + right);

        if(left instanceof Function) {
            // System.out.println("Detecting function and running it");
            // printExpression(left);
            Function func = (Function) left;
            // return func.run(right);
            Expression returned = func.run(right);
            // System.out.println("Returned: " + returned);
            // System.out.println("Function: " + func);
            // System.out.println("Input: " + right);

            return returned;
        } else {
            // System.out.println("Detecting something not a function");

            // return (this.left.run()).run(this.right.run());
            Expression leftexp = this.left.run();
            if(leftexp instanceof Function) {
                Function func = (Function) leftexp;
                return func.run(this.right.run());
            } else {
                return new Application(leftexp, this.right.run()); // idk if this is right
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


// 0 = \f.\x.x
// Added (λf.(λx.x)) as 0
// > succ = \n.\f.\x.f (n f x)
// Added (λn.(λf.(λx.(f ((n f) x))))) as succ
// > 1 = run succ 0
// Added (λf.(λx.(f x))) as 1

// 0 = \f.\x.x
// succ = \n.\f.\x.f (n f x)
// 1 = run succ 0
// Added (λf.(λx.(f x))) as 1

// run (\n.\f.\x.f (n f x) \f.\x.x))

// ((λn.(λf.(λx.(f ((n f) x))))) (λf.(λx.x)))

// run (\a.\b.(a b)) e
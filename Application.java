
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
        System.out.println("calls this run function");
        if(left instanceof Function) {
            System.out.println("a");
            Function func = (Function) left;
            return func.run(right);
        } else {
            System.out.println("this stuff");
            return new Application(this.left.run(), this.right.run());
        }       

        // if(!(left instanceof Function) && !(right instanceof Function)) {
        //     return this;
        // } else if ( (left instanceof Function) ) {
        //     // this is the only thing that can actually "run"
        //     Function func = (Function) left;
        //     return func.run(right);

        // } else if ( (right instanceof Function) ) {

        //     Function func = (Function) right;
        //     return func.run(left);


        // }

        // throw new Error("Not implemented");
    }
}

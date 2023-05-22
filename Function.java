
public class Function implements Expression {
    private Variable var;
    private Expression exp;

    public Function(Variable var, Expression exp) {
        this.var = var;
        this.exp = exp;
    }

    public String toString() {
        return "(λ" + var + "." + exp + ")";
    }

    public Expression run() {
        return this;
    }
 
    private Expression substitute(Expression current, Expression replace_exp, Variable var_to_replace) {
        System.out.println("--------------------");
        System.out.println(replace_exp);
        System.out.println(replace_exp instanceof Variable);
        System.out.println(replace_exp instanceof Function);
        System.out.println(replace_exp instanceof Application);
       
        if(current instanceof Variable) {
            if(current.toString().equals(var_to_replace.toString())) {
                return replace_exp;
            } else {
                return current;
            }
        } else if (current instanceof Application) { // (\x.(x x)) y
            Application app = (Application) current;           
            Expression leftside = app.getLeft();
            Expression rightside = app.getRight();
            return new Application(substitute(leftside, replace_exp, var_to_replace), substitute(rightside, replace_exp, var_to_replace));
        } else {
            Function func = (Function) current;
            return substitute(func.exp, replace_exp, var_to_replace);
        }
    }

    public Expression run(Expression exp) {
        // if application, recurse left and run all of those then recurse right and run all of those
        // Expression returned = substitute(this, exp, this.var);
        // return returned.run();
        return substitute(this, exp, this.var);
    }
}

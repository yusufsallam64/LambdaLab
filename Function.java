import java.util.UUID;

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
        System.out.println(current);

        if(current instanceof Variable) {
            // if(current.toString().equals(var_to_replace.toString())) {
            System.out.println("path a");
            if(((Variable) current).getID().equals(var_to_replace.getID())) {
                return replace_exp;
            } else {
                return current;
            }
        } else if (current instanceof Application) { // (\x.(x x)) y
            System.out.println("path b");
            Application app = (Application) current;           
            Expression leftside = app.getLeft();
            Expression rightside = app.getRight();
            return new Application(substitute(leftside, replace_exp, var_to_replace), substitute(rightside, replace_exp, var_to_replace));
        } else {
            System.out.println("path c");
            Function func = (Function) current;
            System.out.println("Function Given: " + func);
            Expression returned = substitute(func.exp, replace_exp, var_to_replace);
            System.out.println("Post-Substitution Expression: " + returned);
            System.out.println("Var?: " + (returned instanceof Variable));
            System.out.println("Fn?: " + (returned instanceof Function));
            System.out.println("App?: " + (returned instanceof Application));

            System.out.println("returning: " + returned);

            // TODO --> not gonna lie, if there is a bug its in here i have no idea what this does
            if(returned instanceof Variable) {
                return new Function(func.var, returned);
            } else {
                return returned;
            }
        }
    }

    private void syncVariableIDs(Expression f, UUID id_to_set, String name_to_set_against) {
        System.out.println("Syncing Expression: " + f);
        if(f instanceof Function) {
            Function function = (Function) f;
            id_to_set = function.var.getID();
            name_to_set_against = function.var.toString();
            System.out.println("instance of function");
            if(function.exp instanceof Variable) {
                if(((Variable) function.exp).toString().equals(function.var.toString())) {
                    System.out.println("Updated a variable ID");
                    ((Variable) function.exp).setID(id_to_set);
                }
            } else if (function.exp instanceof Application) {
                Application app = (Application) function.exp;
                System.out.println("found application to set");
                syncVariableIDs(app.getLeft(), id_to_set, name_to_set_against);
                syncVariableIDs(app.getRight(), id_to_set, name_to_set_against);
            } else if (function.exp instanceof Function) {
                Function func = (Function) function.exp;
                syncVariableIDs(func.exp, func.var.getID(), name_to_set_against);
            }
        } else {
            if(f instanceof Variable) {
                System.out.println("instance of var");
                System.out.println(f);
                System.out.println("----");
                if(f.toString().equals(name_to_set_against)) {
                    ((Variable) f).setID(id_to_set);
                }
            } else {
                System.out.println("instance of app");
                Application app = (Application) f;
                syncVariableIDs(app.getLeft(), id_to_set, name_to_set_against);
                syncVariableIDs(app.getRight(), id_to_set, name_to_set_against);
            }
        }
    }

    private void fixVariableIdentifiers(Expression exp) {
        // syncVariableIDs(exp, null, null);
        System.out.println("fixing");
        syncVariableIDs(exp, this.var.getID(),this.var.toString());
    }

    public Expression run(Expression exp) {
        // if application, recurse left and run all of those then recurse right and run all of those
        // Expression returned = substitute(this, exp, this.var);
        // return returned.run();

        // printExpression(this);
        fixVariableIdentifiers(this);
        // printExpression(this);

        Expression evaluated = substitute(this, exp, this.var);
        return evaluated;
    }

    public void printExpression(Expression exp) {
        if(exp instanceof Variable) {
            System.out.println("[Variable : " + exp + " : " + ((Variable) exp).getID() + "]");
        } else if (exp instanceof Function) {
            System.out.println("Function: " + exp);
            System.out.println("[Function Variable : " + ((Function) exp).var + " : " + ((Function) exp).var.getID() + "]");
            printExpression(((Function) exp).exp);
        } else if (exp instanceof Application) {
            System.out.println("Application: " + exp);
            printExpression(((Application) exp).getLeft());
            printExpression(((Application) exp).getRight());
        }
    }
}



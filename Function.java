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

    public Variable getVar() {
        return this.var;
    }

    public Expression getExp() {
        return this.exp;
    }

    public Expression run() {
        return this;
    }
 
    private Expression substitute(Expression current, Expression replace_exp, Variable var_to_replace) {
        // System.out.println("Checking a substitute: " + var_to_replace + " [ " + var_to_replace.getID() + " ] for " + replace_exp + " in " + current);
        if(current instanceof Variable) {
            if(((Variable) current).getID().equals(var_to_replace.getID())) {
                System.out.println("Substituted a var and got: " + replace_exp);
                return replace_exp;
            } else {
                // System.out.println("no match found");
                // System.out.println("current [ " + ((Variable) current).getID() + " ]: " + current + " var_to_replace [ " + var_to_replace.getID() + " ]: " + var_to_replace);

                return current;
            }
        } else if (current instanceof Application) { // (\x.(x x)) y
            Application app = (Application) current;           
            Expression leftside = app.getLeft();
            Expression rightside = app.getRight();
            Application substituted = new Application(substitute(leftside, replace_exp, var_to_replace), substitute(rightside, replace_exp, var_to_replace));
            System.out.println("Substituted an application and got: " + substituted);
            return substituted;
        } else {
            Function func = (Function) current;
            // if(!func.var.equals(var_to_replace)) {
            //     System.out.println("Branch C.1");
            //     return func;
            // }
            Expression current_function_expression = func.exp;

            if(current_function_expression instanceof Variable) {
                if( ((Variable) current_function_expression).getID().equals(var_to_replace.getID()) ) {
                    System.out.println("Substituting a variable in a function: " + current_function_expression + " for " + replace_exp);
                    return replace_exp;
                } else {
                    // this might be wrong
                    return current;
                }
            } else if (current_function_expression instanceof Application) {
                Application app = (Application) current_function_expression;
                Expression leftside = app.getLeft();
                Expression rightside = app.getRight();

                Expression leftside_subbed = substitute(leftside, replace_exp, var_to_replace);
                Expression rightside_subbed;
                
                if(rightside instanceof Function) {
                    Function rs_function = (Function) rightside;
                    rightside_subbed = new Function(rs_function.var, substitute(((Function) rightside).exp, replace_exp, var_to_replace));
                } else {
                    rightside_subbed = substitute(rightside, replace_exp, var_to_replace);
                }
                
                // System.out.println("LHS: " + leftside);
                // System.out.println("LHS SUBBED: " + leftside_subbed);

                // System.out.println("RHS: " + rightside);
                // System.out.println("RHS SUBBED: " + rightside_subbed);

                Expression substituted = new Application(leftside_subbed, rightside_subbed).run();
                // System.out.println("[RAN] Substituted an application in a function and got: " + substituted);
                return substituted; // run might be wrong here
            } else if (current_function_expression instanceof Function) {
                // decent chance this is also wrong
                Function f = (Function) current_function_expression;
                // return substitute(f.exp, replace_exp, var_to_replace);
                System.out.println("CURRENT: " + func);

                Expression returned = new Function(f.var, substitute(f.exp, replace_exp, var_to_replace)).run();

                // System.out.println("VAR TO REPLACE: " + var_to_replace);

                if(var_to_replace.getID().equals(func.getVar().getID())) {
                    return returned;
                } else {
                    return new Function(func.var, returned).run();
                }
                // System.out.println("[RAN] Substituted a function in a function and got: " + returned);
                // return returned;
            } else {
                throw new Error("Not implemented");
            }
        }
    }

    private void syncVariableIDs(Expression f, UUID id_to_set, String name_to_set_against) {
        // System.out.println("-------");
        // System.out.println("Syncing Expression: " + f);
        // System.out.println(f instanceof Variable);
        // System.out.println(f instanceof Function);
        // System.out.println(f instanceof Application);
        // System.out.println("-------");

        if(f instanceof Function) {
            Function function = (Function) f;

            syncVariableIDs(function.exp, id_to_set, name_to_set_against);

            id_to_set = function.var.getID();
            name_to_set_against = function.var.toString();
            
            // if(function.var.toString().equals(name_to_set_against)) {
            //     id_to_set = function.var.getID();
            // }

            // UUID current_fn_var_id = function.var.getID();
            // String current_fn_name_to_set_against = function.var.toString();

            if(function.exp instanceof Variable) {
                // System.out.println("Variable found: " + function.exp + " [ " + ((Variable) function.exp).getID() + " ]");

                if(((Variable) function.exp).toString().equals(function.var.toString())) {
                    // System.out.println("Updating variable id for " + function.exp + " to " + current_fn_var_id);
                    // ((Variable) function.exp).setID(id_to_set);
                    ((Variable) function.exp).setID(id_to_set);
                }
            } else if (function.exp instanceof Application) {
                // System.out.println("Application found: " + function.exp + " [ " + ((Application) function.exp).getLeft() + " || " + ((Application) function.exp).getRight() + " ]");
                Application app = (Application) function.exp;
                syncVariableIDs(app.getLeft(), id_to_set, name_to_set_against);
                // System.out.println("Syncing RHS with " + current_fn_name_to_set_against + " [ " + current_fn_var_id + " ]");
                syncVariableIDs(app.getRight(), id_to_set, name_to_set_against);
                // System.out.println("RHS is synced");
            } else if (function.exp instanceof Function) {
                // System.out.println("Function found: " + function.exp + " [ " + ((Function) function.exp).var + " ] [ " + ((Function) function.exp).exp + " ]"); 
                
                Function func = (Function) function.exp;
                // System.out.println("TAKING A LOOK AT THIS FN VAR: " + func.var.toString());
                if(func.var.toString().equals(name_to_set_against)) {
                    // System.out.println("-----");
                    // System.out.println("[NORMAL?] Syncing variable ids for function: " + func.exp + " [ " + id_to_set + " ] [ " + name_to_set_against + " ]");
                    // System.out.println("[CURRENT] Syncing variable ids for function: " + func.exp + " [ " + current_fn_var_id + " ] [ " + current_fn_name_to_set_against + " ]");
                    // System.out.println("-----");
                    syncVariableIDs(func.exp, func.var.getID(), name_to_set_against);
                } else {
                    // System.out.println("[NORMAL?] Syncing variable ids for function: " + func.exp + " [ " + id_to_set + " ] [ " + name_to_set_against + " ]");
                    syncVariableIDs(func.exp, id_to_set, name_to_set_against);
                    // System.out.println("[CURRENT] Syncing variable ids for function: " + func.exp + " [ " + current_fn_var_id + " ] [ " + current_fn_name_to_set_against + " ]");
                    // syncVariableIDs(func.exp, id_to_set, current_fn_name_to_set_against);
                }

                // syncVariableIDs(func.exp, func.var.getID(), name_to_set_against);
            }
        } else {
            if(f instanceof Variable) {
                if(f.toString().equals(name_to_set_against)) {
                    ((Variable) f).setID(id_to_set);
                }
            } else {
                Application app = (Application) f;
                syncVariableIDs(app.getLeft(), id_to_set, name_to_set_against);
                syncVariableIDs(app.getRight(), id_to_set, name_to_set_against);
            }
        }
    }

    private void fixVariableIdentifiers(Expression exp) {
        syncVariableIDs(exp, this.var.getID(),this.var.toString());
        // printExpression(exp);
    }

    public Expression run(Expression exp) {
        // if application, recurse left and run all of those then recurse right and run all of those
        // Expression returned = substitute(this, exp, this.var);
        // return returned.run();

        // printExpression(this);
        fixVariableIdentifiers(this);
        System.out.println("----------");
        // printExpression(this);

        Expression evaluated = substitute(this, exp, this.var);
        System.out.println("Evaluated: " + evaluated);
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

// false = \f.\x.x
// true = λx.λy.x
// and = λp.λq.p q p
// run and false false
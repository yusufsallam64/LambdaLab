import java.util.UUID;

public class Function implements Expression {
    private Variable var;
    private Expression exp;
    private boolean innerFunction;
    
    public Function(Variable var, Expression exp) {
        this.var = var;
        this.exp = exp;
        this.innerFunction = false;
    }

    public String toString() {
        return "(λ" + var + "." + exp + ")";
    }

    public void setInnerFunction(boolean innerFunction) {
        this.innerFunction = innerFunction;
    }

    public Variable getVar() {
        return this.var;
    }

    public Expression getExp() {
        return this.exp;
    }

    public void setExp(Expression exp) {
        this.exp = exp;
    }

    public Expression run() {
        return this;
    }
    // run ((\x.\y.x) p)
    // run ((\x.\p.\t.\h.\y.x) p)
    // run ((\z.\y.x) p)
  /**
    * Carries out a singular substitution when running a function
    * @param  current  Current Expression -- We are substituting the things in this
    * @param  replaceExp Expression to replace the variable with
    * @param  varToReplace The given variable to be replaced
    * @return      Returns the final substituted expression
    */
    public Expression substitute(Variable varToReplace, Expression replaceExp) {
        // System.out.println("----------------------------");
        // System.out.println("VAR TO REPLACE: " + varToReplace);
        // System.out.println("REPLACE EXP: " + replaceExp);
        // System.out.println("THIS FUNCTION: " + this);
        if(this.exp instanceof Function innerFunc) {
            innerFunc.setInnerFunction(true);
        }
        
        if(varToReplace.getID().equals(this.var.getID())) {
            if(this.exp instanceof Function func_exp) {
                if(func_exp.exp instanceof Variable functionVariable) {
                    if(functionVariable.getID().equals(varToReplace.getID())) {
                        // System.out.println("1");
                        return new Function(func_exp.var, replaceExp);
                    } else {
                        // System.out.println("2");
                        return new Function(func_exp.var, func_exp.exp);
                    }
                }
                // System.out.println("3");
                if(func_exp.exp instanceof Function innerInnerFunc) {
                    innerInnerFunc.setInnerFunction(true);
                }
                return new Function(func_exp.var, func_exp.exp.substitute(varToReplace, replaceExp));
            }
            if((this.exp instanceof Variable) && this.innerFunction) {
                return this;
            }
            Expression returned = (this.exp).substitute(varToReplace, replaceExp);
            
            // System.out.println("4");
            return returned;
        } else {
            // System.out.println("5");      

            // Function wegotthis = new Function(this.var, recurseUntilApp(this.exp).substitute(varToReplace, replaceExp));
            return new Function(this.var, this.exp.substitute(varToReplace, replaceExp));
            
            // return wegotthis;
        } 
    }

    public Expression recurseUntilApp(Expression a) {
        if(a instanceof Function func) {
            return new Function(func.getVar(), recurseUntilApp(func.getExp()));
        } else if(a instanceof Variable) {
            return a; // this might be wrong
        } else {
            Application app = (Application) a;
            System.out.println("our application is: " + app);
            return app.run();
        }
    }

    private void syncVariableIDs(Expression exp, UUID id_to_set, String name_to_set_against) {
        if(exp instanceof Function) {
            // if have a fn, we want to set the id of inner vars with the same name to the id of the fn var
            Function function = (Function) exp;

            // sync things inside the fn expression
            syncVariableIDs(function.exp, id_to_set, name_to_set_against);

            id_to_set = function.var.getID();
            name_to_set_against = function.var.toString();

            // if exp is var, check ids
            if(function.exp instanceof Variable) {
                if(((Variable) function.exp).toString().equals(function.var.toString())) {
                    ((Variable) function.exp).setID(id_to_set);
                }
            } else if (function.exp instanceof Application) { // if app, sync left and right
                Application app = (Application) function.exp;
                syncVariableIDs(app.getLeft(), id_to_set, name_to_set_against);
                syncVariableIDs(app.getRight(), id_to_set, name_to_set_against);
            } else if (function.exp instanceof Function) { // if fn inside fn, sync outer fn var but if name conflict, then inner fn's var takes precedence               
                Function func = (Function) function.exp;
                if(func.var.toString().equals(name_to_set_against)) {
                    syncVariableIDs(func.exp, func.var.getID(), name_to_set_against);
                } else {
                    syncVariableIDs(func.exp, id_to_set, name_to_set_against);
                }
            }
        } else if(exp instanceof Variable) { // if we've recursed and hit a var, check if we need to set the id
            if(exp.toString().equals(name_to_set_against)) {
                ((Variable) exp).setID(id_to_set);
            }
        } else { // with app, just recurse left & right
            Application app = (Application) exp;
            syncVariableIDs(app.getLeft(), id_to_set, name_to_set_against);
            syncVariableIDs(app.getRight(), id_to_set, name_to_set_against);
        }
    }

    public void fixVariableIdentifiers() {
        syncVariableIDs(this, this.var.getID(),this.var.toString());
    }

    
}

// false = \f.\x.x
// true = λx.λy.x
// and = λp.λq.p q p
// run and false false

// cons = λx.λy.λf.f x y
// run (cons A B)
// should give > (λf.((f A) B))
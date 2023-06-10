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
 
    public Expression run(Expression exp) {
        // before we run anything, we want to fix the identifiers of the variables
        fixVariableIdentifiers(this);

        Expression evaluated = substitute(this, exp, this.var, false);
        return evaluated;
    }
  /**
    * Carries out a singular substitution when running a function
    * @param  current  Current Expression -- We are substituting the things in this
    * @param  replaceExp Expression to replace the variable with
    * @param  varToReplace The given variable to be replaced
    * @return      Returns the final substituted expression
    */
    private Expression substitute(Expression current, Expression replaceExp, Variable varToReplace, boolean insideFunction) {
        System.out.println("FUNCTION: " + this);
        // System.out.println("EXPRESSION: " + current);
        // System.out.println("REPLACE EXPRESSION: " + replaceExp);
        // System.out.println("VARIABLE TO REPLACE: " + varToReplace);
        // System.out.println("----------");
        if(current instanceof Variable) {
            // if id matches, we know we have the right variable
            if(((Variable) current).getID().equals(varToReplace.getID())) {
                return replaceExp;
            } else {
                // if id doesn't match, leave current var alone
                return current;
            }
        } else if (current instanceof Application) {
            Application currentApp = (Application) current;           
            Expression leftside = currentApp.getLeft();
            Expression rightside = currentApp.getRight();

            // sub both sides of the app and return the result
            Expression subbedApp = new Application(substitute(leftside, replaceExp, varToReplace, false), substitute(rightside, replaceExp, varToReplace, false)).run();

            return subbedApp;
        } else {
            Function currentFn = (Function) current;
            Expression currentFnExp = currentFn.exp;
            if(currentFnExp instanceof Variable) { // somehow combine these two because that would solve basically all the problems we have
                // when our function's exp is a var, follow rules for variables
                // System.out.println("here");
                // if( ((Variable) currentFnExp).getID().equals(varToReplace.getID()) ) {
                //     return replaceExp;
                // } else {
                //     return currentFn;
                // }

                if( ((Variable) currentFnExp).getID().equals(varToReplace.getID()) ) {
                    return replaceExp;
                } else if ( currentFn.var.getID().equals(varToReplace.getID()) && (insideFunction == false) ){
                    // System.out.println("CURRENT FN: " + currentFn);
                    // System.out.println("VARTOREPLACE: " + varToReplace);
                    return currentFnExp;
                } else {
                    return currentFn;
                }

                // if( currentFn.var.getID().equals(varToReplace.getID()) && ((Variable) currentFn.exp).getID().equals(currentFn.var.getID()) ){
                //     return replaceExp;
                // } else if (currentFn.var.getID().equals(varToReplace.getID()) && !((Variable) currentFn.exp).getID().equals(currentFn.var.getID())) {
                //     return currentFnExp;
                // } else {
                //     return currentFn;
                // }

            } else if (currentFnExp instanceof Application) {
                // when we have an application, sub left first, then deal with right
                Application fnApp = (Application) currentFnExp;
                Expression fnAppLeftside = fnApp.getLeft();
                Expression fnAppRightside = fnApp.getRight();

                Expression leftsideSubbed = substitute(fnAppLeftside, replaceExp, varToReplace, true);
                Expression rightsideSubbed;
                
                if(fnAppRightside instanceof Function) {
                    Function rightsideFn = (Function) fnAppRightside;
                    rightsideSubbed = new Function(rightsideFn.var, substitute(rightsideFn.exp, replaceExp, varToReplace, true));
                } else {
                    rightsideSubbed = substitute(fnAppRightside, replaceExp, varToReplace, false);
                }
                
                Expression substituted = new Application(leftsideSubbed, rightsideSubbed); // build a new application with the substituted sides

                if(!currentFn.var.getID().equals(varToReplace.getID())) {
                    return new Function(currentFn.var, substituted);
                }
                
                return substituted;
            } else if (currentFnExp instanceof Function) {
                // case of fn inside fn
                Function innerFn = (Function) currentFnExp; // get the inner function  --> \y.(\x.(x y))
                // ignore the param to the inner function and continue with substituting the varToReplace
                Expression evalInnerFn = new Function(innerFn.var, substitute(innerFn.exp, replaceExp, varToReplace, true)); 
                
                // if the id of the variable we want to replace is the same as the current function's var,
                // we know this is the function we want to replace 
                if(varToReplace.getID().equals(currentFn.getVar().getID())) {
                    return evalInnerFn;
                } else {
                    return new Function(currentFn.var, evalInnerFn);
                }
            } else {
                throw new RuntimeException("Something went wrong with substitution");
            }
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

    private void fixVariableIdentifiers(Expression exp) {
        syncVariableIDs(exp, this.var.getID(),this.var.toString());
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

// cons = λx.λy.λf.f x y
// run (cons A B)
// should give > (λf.((f A) B))
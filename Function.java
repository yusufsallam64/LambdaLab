import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Function implements Expression {
    public static int counter_alpha_redux_postfix = 0;
    public Variable var;
    public Expression exp;
    public boolean innerFunction;
    
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

    //Updates names of variables in an expression, given a variable
    private Expression updateVariableNames(Variable varToReplace, Expression e) {
        if(e instanceof Application a) {
			return new Application(updateVariableNames(varToReplace, a.left), updateVariableNames(varToReplace, a.right));
        }
        if(e instanceof Function f) {
			return new Function((Variable) updateVariableNames(varToReplace, f.var), updateVariableNames(varToReplace, f.exp));
        }
        // e must be variable 

		if(e instanceof Variable v) {
            if(v.getID().equals(varToReplace.getID())) {
                v.displayname = v.displayname + Integer.toString(counter_alpha_redux_postfix);
            }
        }
        
        return ((Variable) e);
    }

    //Function to Run Alphareductions 
    public Expression AlphaReduction(Expression e, Expression rightSide) {
        if(e instanceof Function f) {
            if(checkForVariableName(f.var, rightSide)) {
                counter_alpha_redux_postfix++;
                Function return_f = (Function) updateVariableNames(f.var, f);
                return return_f;
            }
            return new Function(f.var, AlphaReduction(f.exp, rightSide));
        }
        if(e instanceof Application a) {
            return new Application(AlphaReduction(a.getLeft(), rightSide), AlphaReduction(a.getRight(), rightSide));
        }

        return (Variable) e;
    
    }


    /**
    * Carries out a singular substitution when running a function
    * @param  current  Current Expression -- We are substituting the things in this
    * @param  replaceExp Expression to replace the variable with
    * @param  varToReplace The given variable to be replaced
    * @return      Returns the final substituted expression
    * 
    */
    public Expression substitute(Variable varToReplace, Expression replaceExp) {
        AlphaReduction(this, replaceExp);

        if(this.exp instanceof Function innerFunc && this.innerFunction) {
            innerFunc.setInnerFunction(true);
        }
        
        if(varToReplace.getID().equals(this.var.getID())) {
            if(this.exp instanceof Function func_exp) {
                if(func_exp.exp instanceof Variable functionVariable) {
                    if(functionVariable.getID().equals(varToReplace.getID())) {
                        return new Function(func_exp.var, replaceExp);
                    } else {
                        return new Function(func_exp.var, func_exp.exp);
                    }
                }
                if(func_exp.exp instanceof Function innerInnerFunc) {
                    innerInnerFunc.setInnerFunction(true);
                }
                return new Function(func_exp.var, func_exp.exp.substitute(varToReplace, replaceExp));
            }
            if((this.exp instanceof Variable) && this.innerFunction) {
                return this;
            }

            if(this.exp instanceof Function f) {
                f.setInnerFunction(false);
            }

            Expression returned = (this.exp).substitute(varToReplace, replaceExp);
            return returned;
        } else {
            Expression subbed_exp = this.exp.substitute(varToReplace, replaceExp);

            if(subbed_exp instanceof Variable v && v.toString().equals(this.var.toString())) {
                return new Function(this.var, this.var);
            }
            
            return new Function(this.var, subbed_exp);
        } 
    }

    //Set corresponding variable IDs in functions to the ID of the function's variable
    private void syncVariableIDs(Expression exp, UUID id_to_set, String name_to_set_against) {
        if(exp instanceof Function fnexp) {
            // if have a fn, we want to set the id of inner vars with the same name to the id of the fn var
            Function function = (Function) exp;

            // sync things inside the fn expression
            UUID temp_id = function.var.getID();
            String temp_name = function.var.toString();

            syncVariableIDs(function.exp, id_to_set, name_to_set_against);

            if(function.var.toString().equals(name_to_set_against)) {
                syncVariableIDs(function.exp, temp_id, temp_name);
            }

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


    //Check if a given variable name exists in an expression
    public boolean checkForVariableName(Variable v, Expression e) {
        Set<String> var_names = new HashSet<String>();
        
        var_names = LoopTillVariable(e, var_names);

        if(var_names.contains(v.toString())) {
            return true;
        }
        return false;
    }

    //Loop to create list of variables in given expression
    public Set<String> LoopTillVariable(Expression e, Set<String> variables) {
        if(e instanceof Function f) {
            Set<String> temp = new HashSet<String>();
            temp.addAll(LoopTillVariable(f.getVar(), variables));
            temp.addAll(LoopTillVariable(f.getExp(), variables));
            // LoopTillVariable(f.getVar(), variables);
            // LoopTillVariable(f.getExp(), variables);
            return temp;

        }

        if(e instanceof Application a) {
            Set<String> temp = new HashSet<String>();
            temp.addAll(LoopTillVariable(a.getLeft(), variables));
            temp.addAll(LoopTillVariable(a.getRight(), variables));
            return temp;
        }

        if(!variables.contains(e.toString())) {
            variables.add(((Variable) e).toString());
        };
        
        return variables;
    }


    // Deep copy given function and Sync variable IDs
    public void fixVariableIdentifiers() {
        Function deepcopied = ((Function) DeepCopyExpression(this));
        this.exp = deepcopied.exp;
        this.var = deepcopied.var;
        syncVariableIDs(this, this.var.getID(),this.var.toString());
    }

    //Create a copy of a function with all IDs for variable changed
    public Expression DeepCopyExpression(Expression e) {
        if(e instanceof Application a) {
			return new Application(DeepCopyExpression(a.left), DeepCopyExpression(a.right));
        }
        if(e instanceof Function f) {
			return new Function(new Variable(f.var.toString()), DeepCopyExpression(f.exp));
        }
        // e must be variable 
		return new Variable(((Variable) e).toString());
    }


    //Nicer way to print Functions
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

// false = \f.\x.x
// true = λx.λy.x
// and = λp.λq.p q p
// run and false false

// cons = λx.λy.λf.f x y
// run (cons A B)
// should give > (λf.((f A) B))
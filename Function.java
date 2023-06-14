// Yusuf Sallam and Matthew Lerman - ATiCS 22-23 Period 1

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Function implements Expression {
    public static int counter_alpha_redux_postfix = 0; // allows us to relatively easily assign unique alpha reduction names
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

    // helper function that allows us to properly detect when variable IDs should change and when function variables should be discarded
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

    // Updates displaynames of variables in an expression, given a variable to replace and an expression to replace in
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
                v.displayname = v.displayname + Integer.toString(counter_alpha_redux_postfix); // displayname is used as a consequence of how we structured everything with IDs earlier
            }
        }
        
        return ((Variable) e);
    }

    //Function to carry out alpha reductions on a given expression
    public Expression AlphaReduction(Expression e, Expression rightSide) {
        if(e instanceof Function f) {
            if(checkForVariableName(f.var, rightSide)) {
                counter_alpha_redux_postfix++; // we know that we need to assign a unique name if this is already in use by the right side
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
        AlphaReduction(this, replaceExp); // before we do anything, just alpha reduce!

        if(this.exp instanceof Function innerFunc && this.innerFunction) innerFunc.setInnerFunction(true);
        
        if(varToReplace.getID().equals(this.var.getID())) { // we know we need to substitute this function's body
            if(this.exp instanceof Function func_exp) {
                if(func_exp.exp instanceof Variable functionVariable) {
                    if(functionVariable.getID().equals(varToReplace.getID())) return new Function(func_exp.var, replaceExp);

                    return new Function(func_exp.var, func_exp.exp);
                }

                if(func_exp.exp instanceof Function innerInnerFunc) innerInnerFunc.setInnerFunction(true);
                
                return new Function(func_exp.var, func_exp.exp.substitute(varToReplace, replaceExp));
            }

            if((this.exp instanceof Variable) && this.innerFunction) return this;

            if(this.exp instanceof Function f) f.setInnerFunction(false);

            return this.exp.substitute(varToReplace, replaceExp);
        } else {
            Expression subbed_exp = this.exp.substitute(varToReplace, replaceExp);
            if((subbed_exp instanceof Variable v) && v.toString().equals(this.var.toString())) return new Function(this.var, this.var);
            
            return new Function(this.var, subbed_exp);
        } 
    }

    // Set corresponding variable IDs in functions to the ID of the function's variable
    // Essentially sorts out all of the variables and connects everything together
    private void syncVariableIDs(Expression exp, UUID id_to_set, String name_to_set_against) {
        if(exp instanceof Function function) {
            UUID temp_id = function.var.getID();
            String temp_name = function.var.toString();

            syncVariableIDs(function.exp, id_to_set, name_to_set_against);

            // if name conflict, then inner fn's var takes precedence
            if(function.var.toString().equals(name_to_set_against)) syncVariableIDs(function.exp, temp_id, temp_name);

            id_to_set = function.var.getID();
            name_to_set_against = function.var.toString();

            // if exp is var, check ids
            if(function.exp instanceof Variable var_exp) {
                if(var_exp.toString().equals(function.var.toString())) var_exp.setID(id_to_set);
            } else if (function.exp instanceof Application app) { // if app, sync left and right
                syncVariableIDs(app.getLeft(), id_to_set, name_to_set_against);
                syncVariableIDs(app.getRight(), id_to_set, name_to_set_against);
            } else if (function.exp instanceof Function func) { // if fn inside fn, sync outer fn var but if name conflict, then inner fn's var takes precedence like before            
                if(func.var.toString().equals(name_to_set_against)) {
                    syncVariableIDs(func.exp, func.var.getID(), name_to_set_against);
                } else {
                    syncVariableIDs(func.exp, id_to_set, name_to_set_against);
                }
            }
        } else if(exp instanceof Variable var) { // if we've recursed and hit a var, check if we need to set the id
            if(var.toString().equals(name_to_set_against)) var.setID(id_to_set);
        } else {                                 // with app, just recurse left & right
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

    // Loop to create list of variables in given expression
    public Set<String> LoopTillVariable(Expression e, Set<String> variables) {
        if(e instanceof Function f) {
            Set<String> temp = new HashSet<String>();
            temp.addAll(LoopTillVariable(f.getVar(), variables));
            temp.addAll(LoopTillVariable(f.getExp(), variables));
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


    // Nicer way to print Functions
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
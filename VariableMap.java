// Yusuf Sallam and Matthew Lerman - ATiCS 22-23 Period 1

import java.util.HashMap;
import java.util.Map.Entry;
/*
 * 
 * This class is responsible for maintaining and managing the defined variables as the lab is running
 * 
 */
public class VariableMap {
    private HashMap<Variable, Expression> variableMap = new HashMap<Variable, Expression>();

	public void addVariable(Variable var, Expression exp) throws AssignmentError {
		if(getVariable(var) != null) {
			throw new AssignmentError("Variable " + var + " is already defined!", -1);
		}
		variableMap.put(var, exp);
	}

	public HashMap<Variable, Expression> getSetVariables() {
		return variableMap;
	}

	public Expression getVariable(Variable v) {
		return getVariable(v.toString());
	}

	// this is kinda hacky, but it makes it easier to get stuff from varmap
	public Expression getVariable(String v) {
		for(Entry<Variable, Expression> entry : variableMap.entrySet()) {
			if(entry.getKey().toString().equals(v)) {
				return DeepCopyExpression(entry.getValue()); // whenever we get a variable from here, we want the deepcopied version of it
			}
		}

		return null;
	}

	public Expression getValue(Variable v) {
		return DeepCopyExpression(variableMap.get(v)); // deep copies should always be returned
	}

	// this works but like not too important for the lab, just makes it easier in Parser.java
	public Variable getVarByExp(Expression exp) {
		for(Entry<Variable, Expression> entry : variableMap.entrySet()) {
			if(entry.getValue().toString().equals(exp.toString())) {
				return entry.getKey();
			}
		}

		return null;
	}

	// returns a new copy of the expression with all the variables given refreshed IDs
	public Expression DeepCopyExpression(Expression e) {
        if(e instanceof Application a) {
			return new Application(DeepCopyExpression(a.left), DeepCopyExpression(a.right));
        }
        if(e instanceof Function f) {
			return new Function(new Variable(f.var.toString()), DeepCopyExpression(f.exp));
        }

		return new Variable(((Variable) e).toString());
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

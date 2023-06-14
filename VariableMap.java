import java.util.HashMap;
import java.util.Map.Entry;

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
		Expression var_value = getVariable(v.toString());
		//printExpression(var_value);
		//System.out.println("----------------------------------------------");
		return var_value;
	}

	// this is kinda hacky, but it makes it easier to get stuff from varmap
	public Expression getVariable(String v) {
		for(Entry<Variable, Expression> entry : variableMap.entrySet()) {
			if(entry.getKey().toString().equals(v)) {
				// System.out.println("getting var: " + v);
				// System.out.println("value: " + entry.getValue());
				// System.out.println("PRE-----------------------------");
				// printExpression(entry.getValue());

				Expression deep_copied = DeepCopyExpression(entry.getValue());
				// System.out.println("post");
				// printExpression(deep_copied);
				// System.out.println();
				return deep_copied;
			}
		}

		return null;
	}

	public Expression getValue(Variable v) {
		return DeepCopyExpression(variableMap.get(v));
	}

	// this works but like not too important
	public Variable getVarByExp(Expression exp) {
		for(Entry<Variable, Expression> entry : variableMap.entrySet()) {
			if(entry.getValue().toString().equals(exp.toString())) {
				return entry.getKey();
			}
		}

		return null;
	}


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
	
    // private Variable ChangeID(Variable v) {
    //     v.setID(UUID.randomUUID());
	// 	return v;
    // }

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
    } //(115/1.3)
}

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
		return getVariable(v.toString());
	}

	// this is kinda hacky, but it makes it easier to get stuff from varmap
	public Expression getVariable(String v) {
		for(Entry<Variable, Expression> entry : variableMap.entrySet()) {
			if(entry.getKey().toString().equals(v)) {
				System.out.println("returning value: " + entry.getValue());
				return entry.getValue();
			}
		}

		return null;
	}

	public Expression getValue(Variable v) {
		return variableMap.get(v);
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


}

// Yusuf Sallam and Matthew Lerman - ATiCS 22-23 Period 1

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Variable implements Expression {
	public String name;
    public String displayname; // added to support true alpha reductions
	public UUID id;
	
	public Variable(String name) {
		this.name = name;
        this.displayname = name;
		this.id = UUID.randomUUID();
	}

	public Variable(String name, UUID id) {
		this.name = name;
        this.displayname = name;
		this.id = id;
	}

	public UUID getID() {
		return id;
	}
	
	public void setID(UUID id) {
		this.id = id;
	}

	public Expression substitute(Variable compare, Expression exp) {
		if(compare.getID().equals(this.id)) return exp;
        return this;
	}
	
	public String toString() {
		return name;
	}

    public String getDisplayName() {
        return displayname;
    }

	public Expression run() { // vars just return themselves
		return this;
	}
	
    // checks if a variable name is within a given expression
	public boolean checkForVariableName(Variable v, Expression e) {
        Set<String> var_names = new HashSet<String>();
        
        var_names = LoopTillVariable(e, var_names);

        if(var_names.contains(v.toString())) {
            return true;
        }
        return false;
    }

    // same function as in Function.java
    public Set<String> LoopTillVariable(Expression e, Set<String> variables) {
        if(e instanceof Function f) {
            LoopTillVariable(f.getVar(), variables);
            LoopTillVariable(f.getExp(), variables);
        }

        if(e instanceof Application a) {
            LoopTillVariable(a.getLeft(), variables);
            LoopTillVariable(a.getRight(), variables);
        }

        if(!variables.contains(e.toString())) {
            variables.add(((Variable) e).toString());
        };
        
        return variables;
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

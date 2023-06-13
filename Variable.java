import java.util.UUID;

public class Variable implements Expression {
	public String name;
	public UUID id;
	
	public Variable(String name) {
		this.name = name;
		this.id = UUID.randomUUID();
	}

	public Variable(String name, UUID id) {
		this.name = name;
		this.id = id;
	}

	public UUID getID() {
		return id;
	}
	
	public void setID(UUID id) {
		this.id = id;
	}

	public Expression substitute(Variable compare, Expression exp) {
		if(compare.getID().equals(this.id)) {
			return exp;
		} else {
			return this;
		}
	}
	
	public String toString() {
		return name;
	}

	public Expression run() {
		return this;
	}

	// public 
}

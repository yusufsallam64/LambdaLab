import java.util.UUID;

public class Variable implements Expression {
	private String name;
	private UUID id;
	
	public Variable(String name) {
		this.name = name;
		this.id = UUID.randomUUID();
	}

	public UUID getID() {
		return id;
	}
	
	public void setID(UUID id) {
		this.id = id;
	}

	public String toString() {
		return name;
	}

	public Expression run() {
		return this;
	}

	// public 
}

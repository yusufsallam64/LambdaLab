
public class Function implements Expression {
    private Variable var;
    private Expression exp;

    public Function(Variable var, Expression exp) {
        this.var = var;
        this.exp = exp;
    }

    // TODO --> double check this toString
    public String toString() {
        return "(λ" + var + "." + exp + ")";
    }

}

public interface  Expression extends Runner {
    public String toString();
    public Expression run();
    public Expression substitute(Variable varToReplace, Expression replace_exp);
}


public class Executor {

    //Execute a given expression
    public Expression execute_expression(Expression exp) {
        while(containsRedex(exp)) {
            exp = LoopThroughExpression(exp.run());
        }

        return exp;
    }

    //Return whether a given expression contains a REDEX
    public Boolean containsRedex(Expression e) {
        if(e instanceof Variable) {
            return false;
        }

        if(e instanceof Function f) {
            return containsRedex(f.getExp());
        }

        if(e instanceof Application a) {
            if(a.getLeft() instanceof Function) {
                return true;
            }
            return containsRedex(a.getLeft()) || containsRedex(a.getRight());
        }

        return false;
    }


    public Expression LoopThroughExpression(Expression e) {
        if(e instanceof Variable v) {
            return v;
        }
        if(e instanceof Application a) {
            if(containsRedex(a)) {
                Expression aleft = LoopThroughExpression(a.getLeft());
                Expression aright = LoopThroughExpression(a.getRight());
                a = new Application(aleft, aright);
                Expression ran = a.run();
                return ran;
            } 

            Expression reta =  LoopThroughApplications(a);
            return reta;
        }
        return LoopThroughFunction((Function) e);
    }


    // Loop Through function 
    public Expression LoopThroughFunction(Function f) {
        if(f.getExp() instanceof Function fexp) {
            return new Function(f.getVar(), LoopThroughFunction(fexp));
        }
        if(f.getExp() instanceof Variable) {
            return f;
        }

        return new Function(f.getVar(), LoopThroughExpression(f.getExp()));
    }
 
    //Loop throgugh Application until a REDEX is found, substitute when it is
    public Expression LoopThroughApplications(Application a) {
        if(a.getLeft() instanceof Function f) {
            Expression e = f.substitute(f.getVar(), a.getRight());
            return LoopThroughExpression(e);
        }
        
        return new Application(LoopThroughExpression(a.getLeft()), a.getRight()).run();
    }
}
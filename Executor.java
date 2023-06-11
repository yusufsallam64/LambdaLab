public class Executor {
    
    public Expression execute_expression(Expression exp) {
        Expression fully_executed = exp.run();
        System.out.println("we have gotten back: " + fully_executed);
        
        // printExpression(returned);
        // Expression fully_executed = runAllRemainingRedexes(returned);
        
        System.out.println("after fully executing: " + fully_executed);

        // Expression loopedthroughtething = LoopThroughExpression(fully_executed);

        // System.out.println("matt's thing: " + loopedthroughtething);
        return fully_executed;
    }


    public Expression LoopThroughExpression(Expression e) {
        if(e instanceof Variable v) {
            return v;
        }
        if(e instanceof Application a) {
            return LoopThroughApplications(a);
        }
        return LoopThroughFunction((Function) e);
    }

    public Expression LoopThroughFunction(Function f) {
        if(f.getExp() instanceof Function fexp) {
            return new Function(f.getVar(), LoopThroughFunction(fexp));
        }
        if(f.getExp() instanceof Variable) {
            return f;
        }
        return new Function(f.getVar(), LoopThroughApplications((Application) f.getExp()));
    }

    public Expression LoopThroughApplications(Application a) {
        if(a.getLeft() instanceof Variable && a.getRight() instanceof Variable) {
            return a;
        }
        if(a.getLeft() instanceof Function f) {
            Expression e = a.getLeft().substitute(f.getVar(), a.getRight());
            if(e instanceof Application ea) {
                return LoopThroughApplications(ea);
            }
            if(e instanceof Function fe) {
                return LoopThroughFunction(fe);
            }
            return (Variable) e;
        }
        return new Application(LoopThroughExpression(a.getLeft()), LoopThroughExpression(a.getRight())).run();
    }
    
    // public Expression runAllRemainingRedexes(Expression exp) {
    //     if(exp instanceof Variable var) {
    //         return var;
    //     } else if (exp instanceof Application app) {
    //         if(app.getLeft() instanceof Function) {
    //             Function leftSide = (Function) app.getLeft();
    //             System.out.println("AHSJDALSHD");
    //             return app.getLeft().substitute(leftSide.getVar(), app.getRight());
    //         } else {
    //             System.out.println("KAKAKAKAK");
    //             Application test = new Application( runAllRemainingRedexes(app.getLeft()), runAllRemainingRedexes(app.getRight()));
    //             return test;
    //         }
    //     } else {
    //         Function func = (Function) exp;
    //         System.out.println("fnexp: " + func.getExp());
    //         System.out.println("run all remainred in fnexp: " + runAllRemainingRedexes(func.getExp()));
    //         return new Function(func.getVar(), func.getExp().run());
    //     }
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
    }
}
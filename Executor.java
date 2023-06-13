public class Executor {
    
    public Expression execute_expression(Expression exp) {
        // Expression returned_initial = exp.run();

        // TODO -> fjkdsafnh uiosagfudinfgheuiowqnrh438qnyr7843nhr834nhfiuoew
        while(containsRedex(exp)) {
            System.out.println("redex: " + exp);
            exp = LoopThroughExpression(exp);
            System.out.println("redexAFTER: " + exp);
        }

        return exp;
    }


    public Expression LoopThroughExpression(Expression e) {
        if(e instanceof Variable v) {
            return v;
        }
        if(e instanceof Application a) {
            // TODO --> THSI FIXES IT KINDA JUST NEED TO FIGURE OUT WHY THE riGHT SIDE STILL ADDS RANDOM SHIT
            while(containsRedex(a)) {
                Expression aleft = LoopThroughExpression(a.getLeft());
                Expression aright = LoopThroughExpression(a.getRight());
                a = new Application(aleft, aright);
                return a.run();
            } // to go back comment out this if statement

            Expression reta =  LoopThroughApplications(a);
            // System.out.println("loop thru app returning: " + reta);
            return reta;
        }
        return LoopThroughFunction((Function) e);
    }

    public Expression LoopThroughFunction(Function f) {
        System.out.println("CALLED LTF: " + f);
        // System.out.println("LOOPING THROUGH FUNCTION: " + f);
        if(f.getExp() instanceof Function fexp) {
            return new Function(f.getVar(), LoopThroughFunction(fexp));
        }
        if(f.getExp() instanceof Variable) {
            return f;
        }
        return new Function(f.getVar(), LoopThroughApplications((Application) f.getExp()));
    }

    public Expression LoopThroughApplications(Application a) {
        System.out.println("CALLED LTA: " + a);
        if(a.getLeft() instanceof Function f) {
            Expression e = f.substitute(f.getVar(), a.getRight());
            // System.out.println("[SUBSTITU] Returning: " + e);
            return LoopThroughExpression(e);
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

        // TODO --> i firmly believe that this is a future soln/where the problem is
        // if(a.getRight() instanceof Application rhsapp) {
        //     Expression left = LoopThroughExpression(a.getLeft());
        //     Expression right = LoopThroughExpression(a.getRight());
        //     return new Application(left, right).run();
        // } else {
        //     Expression left = LoopThroughExpression(a.getLeft());
        //     return new Application(left, a.getRight()).run();
        // }
        // System.out.println("LEFT: " + a.getLeft());
        // System.out.println("RIGHT: " + a.getRight());
        Expression ap = new Application(LoopThroughExpression(a.getLeft()), LoopThroughExpression(a.getRight())).run();
 
        // Expression ap = new Application(LoopThroughExpression(a.getLeft()), a.getRight()).run();

        System.out.println("[APPLICA] Returning: " + ap);
        return ap;
    }
}
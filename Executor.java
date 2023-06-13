public class Executor {
    public Expression execute_expression(Expression exp) {
        //System.out.println("EXECUTING EXPRESSION: " + exp);
        // Expression returned_initial = exp.run();

        // TODO -> fjkdsafnh uiosagfudinfgheuiowqnrh438qnyr7843nhr834nhfiuoew
        while(containsRedex(exp)) {
            //System.out.println("redex: " + exp);
            exp = LoopThroughExpression(exp.run());
            //System.out.println("redexAFTER: " + exp);
        }

        return exp;
    }

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
        //System.out.println("LOOPSTHROUGHEXPCOUNT: " + count++);
        if(e instanceof Variable v) {
            //System.out.print(count++ + " ");
            //System.out.println("VAREXPRET: " + v);
            return v;
        }
        if(e instanceof Application a) {
            // TODO --> THSI FIXES IT KINDA JUST NEED TO FIGURE OUT WHY THE riGHT SIDE STILL ADDS RANDOM SHIT
            if(containsRedex(a)) {
                Expression aleft = LoopThroughExpression(a.getLeft());
                Expression aright = LoopThroughExpression(a.getRight());
                a = new Application(aleft, aright);

                //System.out.println("CNTAINRDXAPP WITHOUT RUNNING: " + a);

                // print out the type of aleft and a right
                //System.out.println("CLASSLEFT: " + aleft.getClass());
                //System.out.println("CLASSRIGHT: " + aright.getClass());

                Expression ran = a.run();
                //System.out.print(count++ + " ");
                //System.out.println("CNTAINRDXAPP: " + ran);
                return ran;
            } // to go back comment out this if statement

            Expression reta =  LoopThroughApplications(a);
            //System.out.print(count++ + " ");
            //System.out.println("loop thru app returning: " + reta);
            return reta;
        }
        return LoopThroughFunction((Function) e);
    }

    public Expression LoopThroughFunction(Function f) {
        // System.out.println("LOOPING THROUGH FUNCTION: " + f);
        // System.out.println("LOOPSTHROUGHFNCOUNT: " + count++);
        //System.out.println("CALLS LTF ON: " + f);
        if(f.getExp() instanceof Function fexp) {
            Expression idk = new Function(f.getVar(), LoopThroughFunction(fexp));
            //System.out.print(count++ + " ");
            //System.out.println("RETURNING: " + idk);
            return idk;
        }
        if(f.getExp() instanceof Variable) {
            // System.out.println("we have a variable expression of a fn which is: " + f);
            //System.out.print(count++ + " ");
            //System.out.println("VARRETURNING: " + f);
            return f;
        }

        // if(containsRedex(f.getExp())) {
        //     Function fn = new Function(f.getVar(), LoopThroughExpression( ((Application) f.getExp()).run() ));
        //     return fn;
        // }
        Function fn = new Function(f.getVar(), LoopThroughExpression(f.getExp()));
        // System.out.println("[FUNCTION] Returning: " + fn);
        //System.out.print(count++ + " ");
        //System.out.println("FNRETURN: " + fn);
        return fn;
    }
 
    // 99% sure the bug is in here
    public Expression LoopThroughApplications(Application a) {
        //System.out.println("CALLS LTA ON: " + a);
        if(a.getLeft() instanceof Function f) {
            Expression e = f.substitute(f.getVar(), a.getRight());
            //System.out.print(count++ + " ");
            //System.out.println("[SUBSTITU] Returning: " + e);
            return LoopThroughExpression(e);
        }
        
        // System.out.println("looping through application: " + a);

        // TODO --> i firmly believe that this is a future soln/where the problem is
        // if(a.getRight() instanceof Application rhsapp) {
        //     Expression left = LoopThroughExpression(a.getLeft());
        //     Expression right = LoopThroughExpression(a.getRight());
        //     return new Application(left, right).run();
        // } else {
        //     Expression left = LoopThroughExpression(a.getLeft());
        //     return new Application(left, a.getRight()).run();
        // }
        
        // Application newapp = new Application(LoopThroughExpression(a.getLeft()), LoopThroughExpression(a.getRight()));
        // if(containsRedex(newapp)){
        //     return newapp.run();
        // } else {
        //     return newapp;
        // }
 
        Expression ap = new Application(LoopThroughExpression(a.getLeft()), a.getRight()).run();

        //System.out.print(count++ + " ");
        //System.out.println("RETURNS AP: " + ap);
        return ap;
    }
}
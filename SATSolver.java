package sat;

import immutable.ImList;
import sat.env.Environment;
import sat.formula.Clause;
import sat.formula.Formula;
import sat.formula.Literal;
import sat.formula.NegLiteral;

/**
 * A simple DPLL SAT solver. See http://en.wikipedia.org/wiki/DPLL_algorithm
 */
public class SATSolver {
    /**
     * Solve the problem using a simple version of DPLL with backtracking and
     * unit propagation. The returned environment binds literals of class
     * bool.Variable rather than the special literals used in clausification of
     * class clausal.Literal, so that clients can more readily use it.
     *
     * @return an environment for which the problem evaluates to Bool.TRUE, or
     * null if no such environment exists.
     */
    public static Environment solve(Formula formula) {

        Environment env = new Environment();
        ImList<Clause> clauses = formula.getClauses();
        return solve(clauses, env);
    }

    /**
     * Takes a partial assignment of variables to values, and recursively
     * searches for a complete satisfying assignment.
     *
     * @param clauses formula in conjunctive normal form
     * @param env     assignment of some or all variables in clauses to true or
     *                false values.
     * @return an environment for which all the clauses evaluate to Bool.TRUE,
     * or null if no such environment exists.
     */
    private static Environment solve(ImList<Clause> clauses, Environment env) {
        if (clauses.isEmpty()) {
            //trivially satisfiable
            return env;
        }

        for (Clause c : clauses) {
            if (c.isEmpty()) {
                //unsatisfiable
                return null;
            }
        }

        Clause smallest = clauses.first();
        for (Clause cl : clauses) {
            if (cl.size() < smallest.size()) {
                smallest = cl;
            }
        }

        Literal l = smallest.chooseLiteral();
        ImList<Clause> sub = null;

        if (l instanceof NegLiteral) {
            env = env.putFalse(l.getVariable());
        } else {
            env = env.putTrue(l.getVariable());
        }

        if (smallest.isUnit()) {
            sub = substitute(clauses, l);
            if (sub == null){
                return null;
            }
            return solve(sub, env);

        } else {
            //assume true
            sub = substitute(clauses, l);
            if (sub == null) {
                return null;
            }

            Environment ans = solve(sub, env);
            if (ans != null) {
                return ans;

            } else {
                //assume false
                sub = substitute(clauses, l.getNegation());

                if (sub == null) {
                    return null;
                }

                if (l instanceof NegLiteral) {
                    env = env.putFalse(l.getVariable());
                } else {
                    env = env.putTrue(l.getVariable());
                }

                return solve(sub, env);
            }
        }
    }

    /**
     * given a clause list and literal, produce a new list resulting from
     * setting that literal to true
     *
     * @param clauses , a list of clauses
     * @param l       , a literal to set to true
     * @return a new list of clauses resulting from setting l to true
     */
    private static ImList<Clause> substitute(ImList<Clause> clauses,
                                             Literal l) {

        //1. If clause is false, formula is unsatisfiable, return null
        //2. If clause contains one true literal, remove clause from list
        //3. If clause contains one false literal, remove literal from clause

        if (clauses.isEmpty()){
            return clauses;
        }

        for (Clause c : clauses) {
            if (c.contains(l)) {
                //l is true, remove c from clauses
                clauses = clauses.remove(c);

            } else if (c.contains(l.getNegation())) {
                //false literal, remove literal from clause
                Clause newC = c.reduce(l);
                clauses = clauses.remove(c);
                clauses = clauses.add(newC);

            } else if (c.isEmpty()) {
                //unsatisfiable
                return null;
            }
        }
        return clauses;
    }
}
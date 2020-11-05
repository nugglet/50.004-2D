package sat;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import sat.env.*;
import sat.formula.*;

public class SATSolverTest {
    Literal a = PosLiteral.make("a");
    Literal b = PosLiteral.make("b");
    Literal c = PosLiteral.make("c");
    Literal na = a.getNegation();
    Literal nb = b.getNegation();
    Literal nc = c.getNegation();
    private Environment e;


    // TODO: add the main method that reads the .cnf file and calls SATSolver.solve to determine the satisfiability

    public static void main(String[] args) throws IOException {
        String name = args[0];
        File file = new File("2d-demo\\" + name);
        Scanner scanner = new Scanner(file);
        FileWriter writer = new FileWriter("BoolAssignment.txt");
        Formula formula = new Formula();
        ArrayList<Clause> clauseArr = new ArrayList<Clause>() {
        };

        while (scanner.hasNextLine()) {

            String s = scanner.nextLine().trim();

            String[] splitVariables = s.split("\\s+");
            ArrayList<Literal> litArray = new ArrayList<Literal>() {
            };


            //ignore c and p lines
            if (splitVariables[0].equals("c") || splitVariables[0].equals("p") || splitVariables[0].equals("")) {
                continue;
            }

            for (String i : splitVariables) {
                Literal literal;

                //ignore whitespace
                if (i.equals(" ")) {
                    continue;
                }

                //parse positive value
                if (Integer.parseInt(i) > 0) {
                    literal = PosLiteral.make(i);
                    litArray.add(literal);
                }

                //parse negative value
                else if (Integer.parseInt(i) < 0) {
                    String sub = i.substring(1);
                    literal = NegLiteral.make(sub);
                    litArray.add(literal);
                }
            }
            Literal[] lit = litArray.toArray(new Literal[litArray.size()]);
            Clause clause = makeCl(lit);
            clauseArr.add(clause);
        }
        Clause[] clauses = clauseArr.toArray(new Clause[clauseArr.size()]);
        formula = makeFm(clauses);

        System.out.println("SAT solver starts!!!");
        long started = System.nanoTime();

        Environment e = SATSolver.solve(formula);

        long time = System.nanoTime();
        long timeTaken = time - started;
        System.out.println("Time:" + timeTaken / 1000000.0 + "ms");

        if (e == null) {
            System.out.println("not satisfiable");

        } else {
            System.out.println("satisfiable");
            //Write out to file
            for (Clause c : formula.getClauses()) {
                for (Literal l : c) {
                    writer.write(l.getVariable() + ":" + e.get(l.getVariable()) + '\n');
                }
            }
            writer.close();
        }
    }


    public void testSATSolver1() {
        // (a v b)
        Environment e = SATSolver.solve(makeFm(makeCl(a, b)));
/*
    	assertTrue( "one of the literals should be set to true",
    			Bool.TRUE == e.get(a.getVariable())  
    			|| Bool.TRUE == e.get(b.getVariable())	);
*/
    }


    public void testSATSolver2() {
        // (~a)
        Environment e = SATSolver.solve(makeFm(makeCl(na)));
/*
    	assertEquals( Bool.FALSE, e.get(na.getVariable()));
*/
    }

    private static Formula makeFm(Clause... e) {
        Formula f = new Formula();
        for (Clause c : e) {
            f = f.addClause(c);
        }
        return f;
    }

    private static Clause makeCl(Literal... e) {
        Clause c = new Clause();
        for (Literal l : e) {
            c = c.add(l);
        }
        return c;
    }
}
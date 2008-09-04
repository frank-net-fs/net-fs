package net.fs.algo.test;

import junit.framework.TestCase;
import net.fs.algo.IntegerSimplex;

/**
 * Global Sourcing Tool test cases starts with testTPS4.
 */
public class IntegerSimplexTest extends TestCase {
	
	private final static double DELTA = 1e-5;
	
	private IntegerSimplex is;
	private boolean minimize; 
	private double[] targetCoefficients;
	private Constraint[] constraints;
	private double[] targetCoefficientValuesExpected;
	private double expected;
	private int status;
	
	private void initIntegerSimplex() {
		
		this.expected = 0;
		for(int i=0; i<targetCoefficients.length; ++i) {
			expected += targetCoefficients[i] * targetCoefficientValuesExpected[i];
		}
		
		is = new IntegerSimplex();
		
		is.setObjective(targetCoefficients, minimize);
		
		double[][] constraintArray = new double[constraints.length][targetCoefficients.length];
		int[] equations = new int[constraints.length];
		double[] rhs = new double[constraints.length];
		
		for(int i=0; i<constraints.length; ++i) {
			
			constraintArray[i] = constraints[i].getCoefficients();
			equations[i] = constraints[i].getEquations();
			rhs[i] = constraints[i].getRHS();
		}
		
		is.setConstraints(constraintArray, equations, rhs);
		
		is.init();
	}
	
	private void assertResult() {
		
		assertEquals(
				true,
				status == IntegerSimplex.OPTIMAL);
		
		assertEquals(
				expected,
				is.getObjectiveResult(),
				DELTA);
		
		double[] targetCoefficientValues = is.getCoefficients();
		
		for(int i=0; i<targetCoefficients.length; ++i) {
			assertEquals(
					targetCoefficientValuesExpected[i],
					targetCoefficientValues[i],
					DELTA);
		}
	}
	
	public void solve() {
		
		while((status = is.iterate()) == IntegerSimplex.CONTINUE) {
			//System.out.println(tps.toString());
		}
	}
	
	public void testTPS1() {

		/*
		 * OK, this is like the test testTPS4 from the TwoPhaseSimlexTest, but we wan't to remove the fix costs constraints
		 * x5 = 1000 and x = 1000:
		 * 
		 * P a producer vector and
		 * S a supplier vector
		 * 
		 * Then the costs are:
		 * x1: P(1) to S(1) = 4		    // targetCoefficients(0)
		 * x2: P(1) to S(2) = 8	+	    // targetCoefficients(1)
		 * x3: P(2) to S(1) = 8	+	    // targetCoefficients(2)
		 * x4: P(2) to S(2) = 4	+    	// targetCoefficients(3)
		 * x5: Fixcosts P1  = 10000 +	// targetCoefficients(4)
		 * x6: Fixcosts P2  = 10001 +	// targetCoefficients(5)
		 * 
		 * With the constraints:
		 *   x1 + x2                       =  200	// The requested amount of P1 is 200
		 *  		   x3 + x4             =  200   // The requested amount of P2 is 200
		 *   x1      + x3                  <= 300   // S1 can not deliver more than 300
		 *        x2 +      x4             <= 300   // S2 can not deliver more than 300
		 * - x1      - x3      + 300       >= 0     // Add fixed costs for S1 if S1 is used
		 *      - x2 -      x4       + 300 >= 0     // Add fixed costs for S2 if S2 is used
		 * 
		 * Solving this with the simplex algorithm the result is:
		 * (200, 0, 0, 200, 0.5, 0.5) -> 11600.5
		 * 
		 * But this is not a suitable solution because of the 0.5 for x5 and x6. So we use the Branch and Bound
		 * Method to solve this as a Linear Integer optimization problem. This method adds additional constraints.
		 * E.g.: x5 <=1 or x5 >=0. So the result will be
		 * 200, 0, 200, 0, 1, 0) -> 12400
		 * 
		 */
		this.minimize = true;
		this.targetCoefficients = new double[] { 4, 8, 8, 4, 10000, 10001 };
		this.constraints = new Constraint[] {
				new Constraint(new double[] { 1, 1, 0, 0, 0, 0 }, IntegerSimplex.EQUAL_TO, 200),
				new Constraint(new double[] { 0, 0, 1, 1, 0, 0 }, IntegerSimplex.EQUAL_TO, 200),
				new Constraint(new double[] { 1, 0, 1, 0, 0, 0 }, IntegerSimplex.LESS_THAN, 400),
				new Constraint(new double[] { 0, 1, 0, 1, 0, 0 }, IntegerSimplex.LESS_THAN, 400),
				new Constraint(new double[] { -1, 0, -1, 0, 400, 0 }, IntegerSimplex.GREATER_THAN, 0),
				new Constraint(new double[] { 0, -1, 0, -1, 0, 400 }, IntegerSimplex.GREATER_THAN, 0)

		};
		this.targetCoefficientValuesExpected = new double[] { 200, 0, 200, 0, 1, 0 };

		initIntegerSimplex();

		solve();

		assertResult();
	}

	public void testTPS2() {

		this.minimize = true;
		this.targetCoefficients = new double[] { 4, 8, 8, 8, 4, 8, 8, 8, 4, 1000, 1001, 1000 };
		this.constraints = new Constraint[] { new Constraint(new double[] { 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, IntegerSimplex.EQUAL_TO, 200),
				new Constraint(new double[] { 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0 }, IntegerSimplex.EQUAL_TO, 200),
				new Constraint(new double[] { 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0 }, IntegerSimplex.EQUAL_TO, 200),
				new Constraint(new double[] { 1, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0 }, IntegerSimplex.LESS_THAN, 300),
				new Constraint(new double[] { 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0 }, IntegerSimplex.LESS_THAN, 300),
				new Constraint(new double[] { 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 0 }, IntegerSimplex.LESS_THAN, 300),

				new Constraint(new double[] { -1, 0, 0, -1, 0, 0, -1, 0, 0, 300, 0, 0 }, IntegerSimplex.GREATER_THAN, 0),
				new Constraint(new double[] { 0, -1, 0, 0, -1, 0, 0, -1, 0, 0, 300, 0 }, IntegerSimplex.GREATER_THAN, 0),
				new Constraint(new double[] { 0, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0, 300 }, IntegerSimplex.GREATER_THAN, 0), };
		this.targetCoefficientValuesExpected = new double[] { 200, 0, 0, 100, 0, 100, 0, 0, 200, 1, 0, 1 };

		initIntegerSimplex();

		solve();

		assertResult();
	}
	
	public void testTPS3() {
		
		this.minimize = true;
		this.targetCoefficients = new double[] {20, 15, 40, 70, 10, 20, 10, 20, 20, 25, 15, 25, 50, 30, 30, 30, 20, 20, 40, 50, 80000, 60000, 90000, 50000};
		this.constraints = new Constraint[] {
				new Constraint(new double[] {1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, IntegerSimplex.EQUAL_TO,     600),
				new Constraint(new double[] {0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, IntegerSimplex.EQUAL_TO,     750),
				new Constraint(new double[] {0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, IntegerSimplex.EQUAL_TO,     800),
				new Constraint(new double[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0}, IntegerSimplex.EQUAL_TO,     900),
				new Constraint(new double[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0}, IntegerSimplex.EQUAL_TO,     700),
				new Constraint(new double[] {1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0}, IntegerSimplex.LESS_THAN,    1800),
				new Constraint(new double[] {0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0}, IntegerSimplex.LESS_THAN,    2400),
				new Constraint(new double[] {0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0}, IntegerSimplex.LESS_THAN,    3200),
				new Constraint(new double[] {0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0}, IntegerSimplex.LESS_THAN,    1200),
				
				new Constraint(new double[] {-1, 0, 0, 0, -1, 0, 0, 0, -1, 0, 0, 0, -1, 0, 0, 0, -1, 0, 0, 0, 80000, 0, 0, 0}, IntegerSimplex.GREATER_THAN,    0),
				new Constraint(new double[] {0, -1, 0, 0, 0, -1, 0, 0, 0, -1, 0, 0, 0, -1, 0, 0, 0, -1, 0, 0, 0, 60000, 0, 0}, IntegerSimplex.GREATER_THAN,    0),
				new Constraint(new double[] {0, 0, -1, 0, 0, 0, -1, 0, 0, 0, -1, 0, 0, 0, -1, 0, 0, 0, -1, 0, 0, 0, 90000, 0}, IntegerSimplex.GREATER_THAN,    0),
				new Constraint(new double[] {0, 0, 0, -1, 0, 0, 0, -1, 0, 0, 0, -1, 0, 0, 0, -1, 0, 0, 0, -1, 0, 0, 0, 50000}, IntegerSimplex.GREATER_THAN,    0)
				
		};
		this.targetCoefficientValuesExpected = new double[] {
				0, 600, 0, 0,
				750, 0, 0, 0,
				800, 0, 0, 0,
				0, 900, 0, 0,
				250, 450, 0, 0,
				1, 1, 0, 0
		};
		
		initIntegerSimplex();
		
		solve();
		
		assertResult();
	}
	
	public void testTPS4() {
		
		this.minimize = false;
		this.targetCoefficients = new double[] {8, 11, 6, 4};
		this.constraints = new Constraint[] {
				new Constraint(new double[] {5, 7, 4, 3}, IntegerSimplex.LESS_THAN, 14)
		};
		this.targetCoefficientValuesExpected = new double[] {0, 2, 0, 0};
		
		initIntegerSimplex();
		
		solve();
		
		assertResult();
	}

	public void testTPS5() {
		
		int dq = 200; // discount quantity
		int d = 2; // discount
		
		this.minimize = true;
		this.targetCoefficients = new double[] { 4, 8, 8, 4, 4-d, 8-d, 8-d, 4-d, 10001, 10000, 10001, 10000, dq*(4-d), dq*(4-d) };
		this.constraints = new Constraint[] {
				new Constraint(new double[] { 1, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0 }, IntegerSimplex.EQUAL_TO, 200),
				new Constraint(new double[] { 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0 }, IntegerSimplex.EQUAL_TO, 200),
				
				new Constraint(new double[] { 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, IntegerSimplex.LESS_THAN, 400),
				new Constraint(new double[] { 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, IntegerSimplex.LESS_THAN, 400),
				new Constraint(new double[] { 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0 }, IntegerSimplex.LESS_THAN, 400),
				new Constraint(new double[] { 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0 }, IntegerSimplex.LESS_THAN, 400),
				
				new Constraint(new double[] { -1, 0, -1, 0, 0, 0, 0, 0, 400, 0, 0, 0, 0, 0 }, IntegerSimplex.GREATER_THAN, 0),
				new Constraint(new double[] { 0, -1, 0, -1, 0, 0, 0, 0, 0, 400, 0, 0, 0, 0 }, IntegerSimplex.GREATER_THAN, 0),
				new Constraint(new double[] { 0, 0, 0, 0, -1, 0, -1, 0, 0, 0, 400, 0, 0, 0 }, IntegerSimplex.GREATER_THAN, 0),
				new Constraint(new double[] { 0, 0, 0, 0, 0, -1, 0, -1, 0, 0, 0, 400, 0, 0 }, IntegerSimplex.GREATER_THAN, 0),
				
				new Constraint(new double[] { 0, 0, 0, 0, -1, 0, -1, 0, 0, 0, 0, 0, 400, 0 }, IntegerSimplex.GREATER_THAN, 0),
				new Constraint(new double[] { 0, 0, 0, 0, 0, -1, 0, -1, 0, 0, 0, 0, 0, 400 }, IntegerSimplex.GREATER_THAN, 0),
		};
		this.targetCoefficientValuesExpected = new double[] { 0, 0, 0, 0, 0, 200, 0, 200, 0, 0, 0, 1, 0, 1 };
		
		initIntegerSimplex();
		
		solve();
		
		assertResult();
	}
	
	private class Constraint {
		private double[] coefficients;
		private int equations;
		private double rhs;
		
		public Constraint(double[] coefficients, int equations, double rhs) {
			this.coefficients = coefficients;
			this.equations = equations;
			this.rhs = rhs;
		}

		public double[] getCoefficients() {
			return coefficients;
		}

		public int getEquations() {
			return equations;
		}

		public double getRHS() {
			return rhs;
		}
	}
}

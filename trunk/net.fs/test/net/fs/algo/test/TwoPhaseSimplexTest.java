package net.fs.algo.test;

import junit.framework.TestCase;
import net.fs.algo.TwoPhaseSimplex;

/**
 * Global Sourcing Tool test cases starts with testTPS4.
 */
public class TwoPhaseSimplexTest extends TestCase {
	
	private final static double DELTA = 1e-5;
	
	private TwoPhaseSimplex tps;
	private boolean minimize; 
	private double[] targetCoefficients;
	private Constraint[] constraints;
	private double[] targetCoefficientValuesExpected;
	private double expected;
	private int status;
	
	private void initTwoPhaseSimplex() {
		
		this.expected = 0;
		for(int i=0; i<targetCoefficients.length; ++i) {
			expected += targetCoefficients[i] * targetCoefficientValuesExpected[i];
		}
		
		tps = new TwoPhaseSimplex();
		
		tps.setObjective(targetCoefficients, minimize);
		
		double[][] constraintArray = new double[constraints.length][targetCoefficients.length];
		int[] equations = new int[constraints.length];
		double[] rhs = new double[constraints.length];
		
		for(int i=0; i<constraints.length; ++i) {
			
			constraintArray[i] = constraints[i].getCoefficients();
			equations[i] = constraints[i].getEquations();
			rhs[i] = constraints[i].getRHS();
		}
		
		tps.setConstraints(constraintArray, equations, rhs);
		
		tps.init();
	}
	
	private void assertResult() {
		
		assertEquals(
				true,
				status == TwoPhaseSimplex.OPTIMAL);
		
		assertEquals(
				expected,
				tps.getObjectiveResult(),
				DELTA);
		
		double[] targetCoefficientValues = tps.getCoefficients();
		
		for(int i=0; i<targetCoefficients.length; ++i) {
			assertEquals(
					targetCoefficientValuesExpected[i],
					targetCoefficientValues[i],
					DELTA);
		}
	}
	
	public void solve() {
		
		while((status = tps.iterate()) == TwoPhaseSimplex.CONTINUE) {
			//System.out.println(tps.toString());
		}
	}
	
//	public void testTPS1() {
//		
//		this.minimize = false;
//		this.targetCoefficients = new double[] {3, 2};
//		this.constraints = new Constraint[] {
//				new Constraint(new double[] {2, 1}, PrimalSimplex.LESS_THAN, 100),
//				new Constraint(new double[] {1, 1}, PrimalSimplex.LESS_THAN, 80),
//				new Constraint(new double[] {1, 0}, PrimalSimplex.LESS_THAN, 40)
//		};
//		this.targetCoefficientValuesExpected = new double[] {20, 60};
//		
//		initTwoPhaseSimplex();
//		
//		solve();
//		
//		assertResult();
//	}
//	
//	public void testTPS2() {
//		
//		this.minimize = false;
//		this.targetCoefficients = new double[] {2, 5};
//		this.constraints = new Constraint[] {
//				new Constraint(new double[] {1, 0}, PrimalSimplex.LESS_THAN, 5),
//				new Constraint(new double[] {0, 1}, PrimalSimplex.LESS_THAN, 4),
//				new Constraint(new double[] {2, 3}, PrimalSimplex.LESS_THAN, 16)
//		};
//		this.targetCoefficientValuesExpected = new double[] {2, 4};
//		
//		initTwoPhaseSimplex();
//		
//		solve();
//		
//		assertResult();
//	}
//	
//	public void testTPS3() {
//		
//		this.minimize = false;
//		this.targetCoefficients = new double[] {4, 8, 8, 4, 1, 1};
//		this.constraints = new Constraint[] {
//				new Constraint(new double[] {1, 1, 0, 0, 0, 0}, PrimalSimplex.EQUAL_TO,     200),
//				new Constraint(new double[] {0, 0, 1, 1, 0, 0}, PrimalSimplex.EQUAL_TO,     200),
//				new Constraint(new double[] {1, 0, 1, 0, 0, 0}, PrimalSimplex.LESS_THAN,    300),
//				new Constraint(new double[] {0, 1, 0, 1, 0, 0}, PrimalSimplex.LESS_THAN,    300),
//				new Constraint(new double[] {1, 0, 1, 0, 0, 0}, PrimalSimplex.GREATER_THAN, 1),
//				new Constraint(new double[] {0, 1, 0, 1, 0, 0}, PrimalSimplex.GREATER_THAN, 1),
//				new Constraint(new double[] {0, 0, 0, 0, 1, 0}, PrimalSimplex.EQUAL_TO,     1000),
//				new Constraint(new double[] {0, 0, 0, 0, 0, 1}, PrimalSimplex.EQUAL_TO,     1000)
//		};
//		this.targetCoefficientValuesExpected = new double[] {0, 200, 200, 0, 1000, 1000};
//		
//		initTwoPhaseSimplex();
//		
//		solve();
//		
//		assertResult();
//	}
//	
//	// Start DualPrimalTest
//	public void testTPS4() {
//		
//		this.minimize = false;
//		this.targetCoefficients = new double[] {2, 5};
//		this.constraints = new Constraint[] {
//				new Constraint(new double[] {1, 0}, PrimalSimplex.LESS_THAN, 5),
//				new Constraint(new double[] {0, 1}, PrimalSimplex.LESS_THAN, 4),
//				new Constraint(new double[] {-2, -3}, PrimalSimplex.LESS_THAN, -16)
//		};
//		this.targetCoefficientValuesExpected = new double[] {5, 4};
//		
//		initTwoPhaseSimplex();
//		
//		solve();
//		
//		assertResult();
//	}
//	
//	public void testTPS5() {
//		
//		this.minimize = true;
//		this.targetCoefficients = new double[] {11, 8};
//		this.constraints = new Constraint[] {
//				new Constraint(new double[] {2, 1}, PrimalSimplex.GREATER_THAN, 12),
//				new Constraint(new double[] {1, 2}, PrimalSimplex.GREATER_THAN, 12),
//				new Constraint(new double[] {1, 1}, PrimalSimplex.GREATER_THAN, 10),
//				new Constraint(new double[] {3, 4}, PrimalSimplex.LESS_THAN,    60)
//		};
//		this.targetCoefficientValuesExpected = new double[] { 2, 8 };
//		
//		initTwoPhaseSimplex();
//		
//		solve();
//		
//		assertResult();
//	}
//	
//	// Start TwoPhaseTest
//	public void testTPS6() {
//		
//		this.minimize = true;
//		this.targetCoefficients = new double[] {3, -2, 2, 4, 1};
//		this.constraints = new Constraint[] {
//				new Constraint(new double[] {1,  1, -1, 1, 0}, PrimalSimplex.LESS_THAN,    3),
//				new Constraint(new double[] {-1, 0, 0,  2, 0}, PrimalSimplex.GREATER_THAN, -1),
//				new Constraint(new double[] {0,  0, 0,  0, 1}, PrimalSimplex.EQUAL_TO,     10)
//		};
//		this.targetCoefficientValuesExpected = new double[] { 0, 3, 0, 0, 10 };
//		
//		initTwoPhaseSimplex();
//		
//		solve();
//		
//		assertResult();
//	}
//
//	public void testTPS7() {
//		
//		/*
//		 * OK, let:
//		 * P a producer vector and
//		 * S a supplier vector
//		 * 
//		 * Then the costs are:
//		 * x1: P(1) to S(1) = 4		// targetCoefficients(0)
//		 * x2: P(1) to S(2) = 8	+	// targetCoefficients(1)
//		 * x3: P(2) to S(1) = 8	+	// targetCoefficients(2)
//		 * x4: P(2) to S(2) = 4	+	// targetCoefficients(3)
//		 * x5: Fixcosts P1  = 1 +	// targetCoefficients(4)
//		 * x6: Fixcosts P2  = 1 +	// targetCoefficients(5)
//		 * 
//		 * With the constraints:
//		 * x1 + x2                     =  200	// The requested amount of P1 is 200
//		 * 		     x3 + x4           =  200   // The requested amount of P2 is 200
//		 * x1      + x3                <= 300   // S1 can not deliver more than 300
//		 *      x2 +      x4           <= 300   // S2 can not deliver more than 300
//		 * x1      + x3                >= 1     // S1 have to be used (Fixed costs constraint)
//		 *      x2 +      x4           >= 1     // S2 have to be used (Fixed costs constraint)
//		 *                     x5      = 1000   // Fix costs for S1 are 1000
//		 *                          x6 = 1000   // Fix costs for S2 are 1000   
//		 * 
//		 */
//		
//		this.minimize = true;
//		this.targetCoefficients = new double[] {4, 8, 8, 4, 1, 1};
//		this.constraints = new Constraint[] {
//				new Constraint(new double[] {1, 1, 0, 0, 0, 0}, PrimalSimplex.EQUAL_TO,     200),
//				new Constraint(new double[] {0, 0, 1, 1, 0, 0}, PrimalSimplex.EQUAL_TO,     200),
//				new Constraint(new double[] {1, 0, 1, 0, 0, 0}, PrimalSimplex.LESS_THAN,    300),
//				new Constraint(new double[] {0, 1, 0, 1, 0, 0}, PrimalSimplex.LESS_THAN,    300),
//				new Constraint(new double[] {1, 0, 1, 0, 0, 0}, PrimalSimplex.GREATER_THAN, 1),
//				new Constraint(new double[] {0, 1, 0, 1, 0, 0}, PrimalSimplex.GREATER_THAN, 1),
//				new Constraint(new double[] {0, 0, 0, 0, 1, 0}, PrimalSimplex.EQUAL_TO,     1000),
//				new Constraint(new double[] {0, 0, 0, 0, 0, 1}, PrimalSimplex.EQUAL_TO,     1000)
//		};
//		this.targetCoefficientValuesExpected = new double[] {200, 0, 0, 200, 1000, 1000};
//		
//		initTwoPhaseSimplex();
//		
//		solve();
//		
//		assertResult();
//	}
//	
//	public void testTPS8() {
//		
//		this.minimize = true;
//		this.targetCoefficients = new double[] {4, 8, 8, 8, 4, 8, 8, 8, 4, 1, 1, 1};
//		this.constraints = new Constraint[] {
//				new Constraint(new double[] {1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0}, PrimalSimplex.EQUAL_TO,     200),
//				new Constraint(new double[] {0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0}, PrimalSimplex.EQUAL_TO,     200),
//				new Constraint(new double[] {0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0}, PrimalSimplex.EQUAL_TO,     200),
//				new Constraint(new double[] {1, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0}, PrimalSimplex.LESS_THAN,    300),
//				new Constraint(new double[] {0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0}, PrimalSimplex.LESS_THAN,    300),
//				new Constraint(new double[] {0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 0}, PrimalSimplex.LESS_THAN,    300),
//				new Constraint(new double[] {1, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0}, PrimalSimplex.GREATER_THAN, 1),
//				new Constraint(new double[] {0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0}, PrimalSimplex.GREATER_THAN, 1),
//				new Constraint(new double[] {0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 0}, PrimalSimplex.GREATER_THAN, 1),
//				new Constraint(new double[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0}, PrimalSimplex.EQUAL_TO,     1000),
//				new Constraint(new double[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0}, PrimalSimplex.EQUAL_TO,     1000),
//				new Constraint(new double[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}, PrimalSimplex.EQUAL_TO,     1000)
//		};
//		this.targetCoefficientValuesExpected = new double[] {200, 0, 0, 0, 200, 0, 0, 0, 200, 1000, 1000, 1000};
//		
//		initTwoPhaseSimplex();
//		
//		solve();
//		
//		assertResult();
//	}
//	
//	public void testTPS9() {
//		
//		this.minimize = true;
//		this.targetCoefficients = new double[] {4, 6, 8, 8, 7, 8, 8, 8, 4, 1, 1, 1};
//		this.constraints = new Constraint[] {
//				new Constraint(new double[] {1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0}, PrimalSimplex.EQUAL_TO,     200),
//				new Constraint(new double[] {0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0}, PrimalSimplex.EQUAL_TO,     200),
//				new Constraint(new double[] {0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0}, PrimalSimplex.EQUAL_TO,     200),
//				new Constraint(new double[] {1, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0}, PrimalSimplex.LESS_THAN,    50),
//				new Constraint(new double[] {0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0}, PrimalSimplex.LESS_THAN,    300),
//				new Constraint(new double[] {0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 0}, PrimalSimplex.LESS_THAN,    350),
//				new Constraint(new double[] {1, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0}, PrimalSimplex.GREATER_THAN, 1),
//				new Constraint(new double[] {0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0}, PrimalSimplex.GREATER_THAN, 1),
//				new Constraint(new double[] {0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 0}, PrimalSimplex.GREATER_THAN, 1),
//				new Constraint(new double[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0}, PrimalSimplex.EQUAL_TO,     1000),
//				new Constraint(new double[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0}, PrimalSimplex.EQUAL_TO,     1000),
//				new Constraint(new double[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}, PrimalSimplex.EQUAL_TO,     1000)
//		};
//		this.targetCoefficientValuesExpected = new double[] {50, 150, 0, 0, 150, 50, 0, 0, 200, 1000, 1000, 1000};
//		
//		initTwoPhaseSimplex();
//		
//		solve();
//		
//		assertResult();
//	}
//	
//	public void testTPS10() {
//		
//		this.minimize = true;
//		this.targetCoefficients = new double[] { 4, 8, 8, 4, 10000, 10000 };
//		this.constraints = new Constraint[] {
//				new Constraint(new double[] { 1, 1, 0, 0, 0, 0 }, IntegerSimplex.EQUAL_TO, 200),
//				new Constraint(new double[] { 0, 0, 1, 1, 0, 0 }, IntegerSimplex.EQUAL_TO, 200),
//				new Constraint(new double[] { 1, 0, 1, 0, 0, 0 }, IntegerSimplex.LESS_THAN, 400),
//				new Constraint(new double[] { 0, 1, 0, 1, 0, 0 }, IntegerSimplex.LESS_THAN, 400),
//				new Constraint(new double[] { -1, 0, -1, 0, 400, 0 }, IntegerSimplex.GREATER_THAN, 0),
//				new Constraint(new double[] { 0, -1, 0, -1, 0, 400 }, IntegerSimplex.GREATER_THAN, 0)
//
//		};
//		this.targetCoefficientValuesExpected = new double[] { 200, 0, 0, 200, 0.5f, 0.5f };
//		
//		initTwoPhaseSimplex();
//		
//		solve();
//		
//		assertResult();
//	}
	
	public void testTPS11() {
		
		int dq = 200; // discount quantity
		int d = 2; // discount
		
		this.minimize = true;
		this.targetCoefficients = new double[] { 4, 8, 8, 4, 4-d, 8-d, 8-d, 4-d, 10001, 10000, 10001, 10000, dq*(4-d), dq*(4-d) };
		this.constraints = new Constraint[] {
				new Constraint(new double[] { 1, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0 }, TwoPhaseSimplex.EQUAL_TO, 200),
				new Constraint(new double[] { 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0 }, TwoPhaseSimplex.EQUAL_TO, 200),
				
				new Constraint(new double[] { 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, TwoPhaseSimplex.LESS_THAN, 400),
				new Constraint(new double[] { 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, TwoPhaseSimplex.LESS_THAN, 400),
				new Constraint(new double[] { 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0 }, TwoPhaseSimplex.LESS_THAN, 400),
				new Constraint(new double[] { 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0 }, TwoPhaseSimplex.LESS_THAN, 400),
				
				new Constraint(new double[] { -1, 0, -1, 0, 0, 0, 0, 0, 400, 0, 0, 0, 0, 0 }, TwoPhaseSimplex.GREATER_THAN, 0),
				new Constraint(new double[] { 0, -1, 0, -1, 0, 0, 0, 0, 0, 400, 0, 0, 0, 0 }, TwoPhaseSimplex.GREATER_THAN, 0),
				new Constraint(new double[] { 0, 0, 0, 0, -1, 0, -1, 0, 0, 0, 400, 0, 0, 0 }, TwoPhaseSimplex.GREATER_THAN, 0),
				new Constraint(new double[] { 0, 0, 0, 0, 0, -1, 0, -1, 0, 0, 0, 400, 0, 0 }, TwoPhaseSimplex.GREATER_THAN, 0),
				
				new Constraint(new double[] { 0, 0, 0, 0, -1, 0, -1, 0, 0, 0, 0, 0, 400, 0 }, TwoPhaseSimplex.GREATER_THAN, 0),
				new Constraint(new double[] { 0, 0, 0, 0, 0, -1, 0, -1, 0, 0, 0, 0, 0, 400 }, TwoPhaseSimplex.GREATER_THAN, 0),
		};
		this.targetCoefficientValuesExpected = new double[] { 0, 0, 0, 0, 200, 0, 0, 200, 0, 0, 0.5f, 0.5f, 0.5f, 0.5f };
		
		initTwoPhaseSimplex();
		
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

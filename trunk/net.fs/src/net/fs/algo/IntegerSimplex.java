/* 
Copyright (C) 2004 Free Software Foundation, Inc.

This file is part of GNU Classpath.

net.fs is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2, or (at your option)
any later version.
 
net.fs is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
General Public License for more details.

You should have received a copy of the GNU General Public License
along with net.fs; see the file COPYING.  If not, write to the
Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
02110-1301 USA.

Linking this library statically or dynamically with other modules is
making a combined work based on this library.  Thus, the terms and
conditions of the GNU General Public License cover the whole
combination.

As a special exception, the copyright holders of this library give you
permission to link this library with independent modules to produce an
executable, regardless of the license terms of these independent
modules, and to copy and distribute the resulting executable under
terms of your choice, provided that you also meet, for each linked
independent module, the terms and conditions of the license of that
module.  An independent module is a module which is not derived from
or based on this library.  If you modify this library, you may extend
this exception to your version of the library, but you are not
obligated to do so.  If you do not wish to do so, delete this
exception statement from your version. */
package net.fs.algo;

/**
 * Based on Branch and Bound
 */
public class IntegerSimplex extends TwoPhaseSimplex {
	
	public final static int MAX_ITERATIONS = 100;
	public final static double DELTA = 1e-5;
	
	private boolean eliminateReal;
	private IntegerSimplex upperBound;
	private IntegerSimplex lowerBound;
	private boolean solvedUpper;
	private boolean solvedLower;
	private double objectiveResult;
	private double[] coeffizients;
	//private int iterations;
	
	
	public void init() {
		super.init();
		this.eliminateReal = false;
		this.upperBound = lowerBound = null;
		this.solvedUpper = this.solvedLower = false;
		this.objectiveResult = minimize ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY;
		this.coeffizients = null;
		//this.iterations = 0;
	}
	
	public int iterate() {
		
		int status;
		while((status = super.iterate()) == CONTINUE) {
		}
		if(status == UNBOUNDED) {
			return UNBOUNDED;
		}
		
		
		System.out.println("Eliminate real values");
		int ix = -1;
		double[] coeffizients = super.getCoefficients();
		for(int i=0;i<coeffizients.length;++i) {
			float f = (float) Math.round(coeffizients[i]);
			
			//System.out.println("("+ Math.abs(coeffizients[i]) +" - "+ Math.abs(f) +") > "+ DELTA);
			
			if(Math.abs((Math.abs(coeffizients[i]) - Math.abs(f))) > DELTA) {
				ix = i;
				System.out.println("Non int value at: "+ (i+1) + " "+ coeffizients[i] + " "+ f);
				break;
			}
		}
		
		if(ix > -1) {
			System.out.println("Create branch for index: "+ (ix+1));
			
			// Branch
			upperBound = new IntegerSimplex();
			lowerBound = new IntegerSimplex();
		
			upperBound.minimize = lowerBound.minimize = this.minimize;
			upperBound.objective = lowerBound.objective = this.objective;
		
			upperBound.constraints = new double[constraints.length+1][objective.length];
			upperBound.equations = new int[constraints.length+1];
			upperBound.rhs = new double[constraints.length+1];
		
			lowerBound.constraints = new double[constraints.length+1][objective.length];
			lowerBound.equations = new int[constraints.length+1];
			lowerBound.rhs = new double[constraints.length+1];
		
			for(int i=0;i<constraints.length;++i) {
				System.arraycopy(constraints[i], 0, upperBound.constraints[i], 0, constraints[i].length);
				System.arraycopy(constraints[i], 0, lowerBound.constraints[i], 0, constraints[i].length);
				
				upperBound.equations[i] = lowerBound.equations[i] = equations[i];
				upperBound.rhs[i] = lowerBound.rhs[i] = rhs[i];
			}
		
			upperBound.constraints[constraints.length][ix] = lowerBound.constraints[constraints.length][ix] = 1;
		
			upperBound.equations[constraints.length] = GREATER_THAN;
			lowerBound.equations[constraints.length] = LESS_THAN;
		
			upperBound.rhs[constraints.length] = Math.ceil(coeffizients[ix]);
			lowerBound.rhs[constraints.length] = Math.floor(coeffizients[ix]);
		
			upperBound.init();
			lowerBound.init();
			
			while((status = upperBound.iterate()) == CONTINUE) {
			}
			if(status == OPTIMAL) {
				this.objectiveResult = upperBound.getObjectiveResult();
				this.coeffizients = upperBound.getCoefficients();
			}
			
			while((status = lowerBound.iterate()) == CONTINUE) {
			}
			if(status == OPTIMAL) {
				if(this.coeffizients != null) {
					if(this.minimize && lowerBound.getObjectiveResult() < objectiveResult) {
						this.objectiveResult = lowerBound.getObjectiveResult();
						this.coeffizients = lowerBound.getCoefficients();
					} else if(!this.minimize && lowerBound.getObjectiveResult() > objectiveResult) {
						this.objectiveResult = lowerBound.getObjectiveResult();
						this.coeffizients = lowerBound.getCoefficients();
					}
				} else {
					this.objectiveResult = lowerBound.getObjectiveResult();
					this.coeffizients = lowerBound.getCoefficients();
				}
			}
			
		} else  {
			this.objectiveResult = super.getObjectiveResult();
			this.coeffizients = super.getCoefficients();
		}
		
		return OPTIMAL;
	}
	
	public double getObjectiveResult() {
		return objectiveResult;
	}
	
	public double[] getCoefficients() {
		return coeffizients;
	}
	
	
//	public int iterate() {
//		
//		if(!eliminateReal) {
//			int status = super.iterate();
//			if(status == OPTIMAL) {
//				eliminateReal = true;
//				return CONTINUE;
//			} 
//			return status;
//		}
//		
//		System.out.println("Eliminate real values");
//		int ix = -1;
//		double[] coeffizients = super.getCoefficients();
//		for(int i=0;i<coeffizients.length;++i) {
//			float f = (float) Math.round(coeffizients[i]);
//			
//			//System.out.println("("+ Math.abs(coeffizients[i]) +" - "+ Math.abs(f) +") > "+ DELTA);
//			
//			if(Math.abs((Math.abs(coeffizients[i]) - Math.abs(f))) > DELTA) {
//				ix = i;
//				System.out.println("Non int value at: "+ (i+1) + " "+ coeffizients[i] + " "+ f);
//				break;
//			}
//		}
//		
//		if(ix > -1) {
//			
//			iterations++;
//			if(iterations > MAX_ITERATIONS) {
//				return UNBOUNDED;
//			}
//			
//			if(upperBound == null || lowerBound == null) {
//				System.out.println("Create branch for index: "+ ix);
//				
//				// Branch
//				upperBound = new IntegerSimplex();
//				lowerBound = new IntegerSimplex();
//			
//				upperBound.minimize = lowerBound.minimize = this.minimize;
//				upperBound.objective = lowerBound.objective = this.objective;
//			
//				upperBound.constraints = new double[constraints.length+1][objective.length];
//				upperBound.equations = new int[constraints.length+1];
//				upperBound.rhs = new double[constraints.length+1];
//			
//				lowerBound.constraints = new double[constraints.length+1][objective.length];
//				lowerBound.equations = new int[constraints.length+1];
//				lowerBound.rhs = new double[constraints.length+1];
//			
//				for(int i=0;i<constraints.length;++i) {
//					System.arraycopy(constraints[i], 0, upperBound.constraints[i], 0, constraints[i].length);
//					System.arraycopy(constraints[i], 0, lowerBound.constraints[i], 0, constraints[i].length);
//					
//					upperBound.equations[i] = lowerBound.equations[i] = equations[i];
//					upperBound.rhs[i] = lowerBound.rhs[i] = rhs[i];
//				}
//			
//				upperBound.constraints[constraints.length][ix] = lowerBound.constraints[constraints.length][ix] = 1;
//			
//				upperBound.equations[constraints.length] = GREATER_THAN;
//				lowerBound.equations[constraints.length] = LESS_THAN;
//			
//				upperBound.rhs[constraints.length] = Math.ceil(coeffizients[ix]);
//				lowerBound.rhs[constraints.length] = Math.floor(coeffizients[ix]);
//			
//				upperBound.init();
//				lowerBound.init();
//				
//				//System.out.println("upper: ");
//				//System.out.println(upperBound.toString());
//			}
//			
//			if(!solvedUpper) {
//				System.out.println("Solve upperBound branch");
//				int status = upperBound.iterate();
//				if(status == CONTINUE) {
//					return CONTINUE;
//				} else if(status == OPTIMAL) {
//					System.out.println("upperBound branch solved");
//					this.objectiveResult = upperBound.getObjectiveResult();
//					this.coeffizients = upperBound.getCoefficients();
//				}
//				solvedUpper = true;
//				return CONTINUE;
//			}
//			
//			if(!solvedLower) {
//				int status = lowerBound.iterate();
//				if(status == CONTINUE) {
//					return CONTINUE;
//				} else if(status == OPTIMAL) {
//					/*
//					if(this.coeffizients != null) {
//						if(this.minimize && lowerBound.getObjectiveResult() < objectiveResult) {
//							this.objectiveResult = lowerBound.getObjectiveResult();
//							this.coeffizients = lowerBound.getCoefficients();
//						} else if(!this.minimize && lowerBound.getObjectiveResult() > objectiveResult) {
//							this.objectiveResult = lowerBound.getObjectiveResult();
//							this.coeffizients = lowerBound.getCoefficients();
//						}
//					} else {
//						this.objectiveResult = lowerBound.getObjectiveResult();
//						this.coeffizients = lowerBound.getCoefficients();
//					}
//					*/
//					this.objectiveResult = lowerBound.getObjectiveResult();
//					this.coeffizients = lowerBound.getCoefficients();
//				}
//				solvedLower = true;
//				return CONTINUE;
//			}
//		} else {
//			
//			System.out.println("+SOLUTION");
//			System.out.println(super.toString());
//			System.out.println("-SOLUTION");
//			
//			this.objectiveResult = super.getObjectiveResult();
//			this.coeffizients = super.getCoefficients();
//		}
//		return OPTIMAL;
//	}
	
	public String toString() {
		if(!eliminateReal) {
			return super.toString();
		} else  {
			if(!solvedUpper && upperBound != null) {
				return upperBound.toString();
			} else if(!solvedLower && lowerBound != null) {
				return lowerBound.toString();
			} else {
				return "--";
			}
		}
		
	}
	

	
	public static void main(String[] args) {
		IntegerSimplex ps = new IntegerSimplex();
		
		ps.minimize = true;
		ps.objective = new double[] {4, 8, 8, 4, 1000, 1000};
		ps.constraints = new double[][] {
				{ 1, 1, 0, 0, 0, 0 },
				{ 0, 0, 1, 1, 0, 0 },
				{ 1, 0, 1, 0, 0, 0 },
				{ 0, 1, 0, 1, 0, 0 },
				{ 1, 0, 1, 0, -400, 0 },
				{ 0, 1, 0, 1, 0, -400 }
		};
		ps.equations = new int[] { EQUAL_TO, EQUAL_TO, LESS_THAN, LESS_THAN, LESS_THAN, LESS_THAN };
		ps.rhs = new double[] {200, 200, 400, 400, 0, 0};
		ps.init();
		
		System.out.println(ps.toString());
		
		while(ps.iterate() == 0) {
			System.out.println(ps.toString());
		}
		System.out.println(ps.toString());

		System.out.println("objectiveResult: "+ ps.getObjectiveResult());
		double[] result = ps.getCoefficients();
		for(int i=0;i<result.length;++i) {
			System.out.println("x["+ (i+1) +"]: "+ result[i]);
		}
	}
}

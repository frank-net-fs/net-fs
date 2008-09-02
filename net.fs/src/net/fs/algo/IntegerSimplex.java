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
	
	public void init() {
		super.init();
		this.eliminateReal = false;
		this.upperBound = lowerBound = null;
		this.solvedUpper = this.solvedLower = false;
		this.objectiveResult = minimize ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY;
		this.coeffizients = null;
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
}

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

public class PrimalSimplex {
	
	public final static int LESS_THAN = 0;
	public final static int GREATER_THAN = 1;
	public final static int EQUAL_TO = 2;
	
	public final static int CONTINUE = 0;
	public final static int OPTIMAL = 1;
	public final static int UNBOUNDED = 2;
	
	protected boolean minimize;
	protected double[] objective;
	protected double[][] constraints;
	protected int[] equations;
	protected double[] rhs;
	
	protected double [][] m;
	protected int[] basisVariable;
	protected int[] nonBasisVariable;
	protected int[] slackVariable;
	protected boolean[] locked;
	
	public void init() {
		this.m = new double[constraints.length+1][objective.length+constraints.length+1];
		for(int i=0;i<constraints.length;++i) {
			for(int j=0;j<constraints[i].length;++j) {
				m[i][j] = constraints[i][j] * (equations[i] == GREATER_THAN ? -1 : 1);
			}
			m[i][objective.length+i] = 1;
			m[i][m[i].length-1] = rhs[i] * (equations[i] == GREATER_THAN ? -1 : 1);
		}
		for(int i=0;i<objective.length;++i) {
			m[m.length-1][i] = objective[i] * (minimize ? 1 : -1);
		}
		this.nonBasisVariable = new int[objective.length+constraints.length];
		this.slackVariable = new int[constraints.length];
		for(int i=0;i<this.nonBasisVariable.length;++i) {
			this.nonBasisVariable[i] = i;
			if(i>=objective.length) {
				slackVariable[i-objective.length] = i;
			}
		}
		this.basisVariable = new int[constraints.length];
		for(int i=0;i<this.basisVariable.length;++i) {
			basisVariable[i] = slackVariable[i];
		}
		this.locked = new boolean[basisVariable.length];
	}
	
	public void setObjective(double[] objective, boolean minimize) {
		this.objective = objective;
		this.minimize = minimize;
	}
	
	public void setConstraints(double[][] constraints, int[] equations, double[] rhs) {
		this.constraints = constraints;
		this.equations = equations;
		this.rhs = rhs;
	}
	
	public int iterate() {
		
		double quotient;
		
		// Select column
		int pc = -1;
		double min = Double.POSITIVE_INFINITY;
		for(int i=0;i<m[m.length-1].length-1;++i) {
			//	System.out.println(m[m.length-1][i] +" < "+ 0  + " && "+ m[m.length-1][i] + " < "+ min + " && ("+ i +" < "+ target.length +" || !locked["+ (i-target.length) +"])");
			if(m[m.length-1][i] < 0 && m[m.length-1][i] < min && (i < objective.length || !locked[i-objective.length])) {
				pc = i;
				min = m[m.length-1][i];
			}
		}
		if(pc < 0) {
			// Solved;
			return OPTIMAL;
		}
		
		// Select row
		int pr = -1;
		min = Double.POSITIVE_INFINITY;
		if(pc > -1) {
			for(int i=0;i<m.length-1;++i) {
				if(m[i][pc] > 0) {
					quotient = m[i][m[i].length-1] / m[i][pc];
					//System.out.println("("+ m[i][m[i].length-1] +" / "+ m[i][pc] +") <"+ min);
					if(quotient < min) {
						min = quotient;
						pr = i;
					}
				}
			}
			if(pr < 0) {
				// UNBOUNDED
				return UNBOUNDED;
			}
			
		}
		
		// pivot
		System.out.println("Pivo: row="+ (pr+1) +", column="+ (pc+1));
		pivot(pr, pc);
		
		return CONTINUE;
	}
	
	protected void pivot(int pivotRow, int pivotColumn) {
		double quotient = m[pivotRow][pivotColumn];
		for(int i=0;i<m[pivotRow].length;++i) {
			m[pivotRow][i] = m[pivotRow][i] / quotient;
		}
		for(int i=0;i<m.length;++i) {
			if(m[i][pivotColumn] != 0 && i != pivotRow) {
				
				quotient = m[i][pivotColumn] / m[pivotRow][pivotColumn];
				
				for(int j=0;j<m[i].length;++j) {
					m[i][j] = m[i][j] - quotient * m[pivotRow][j];
				}
			}
		}
		basisVariable[pivotRow] = nonBasisVariable[pivotColumn];
	}
	
	public double getObjectiveResult() {
		return m[m.length-1][m[m.length-1].length-1] * (minimize ? -1 : 1);
	}
	
	public double[] getCoefficients() {
		double[] result = new double[objective.length];
		
		for(int i=0;i<result.length;++i) {
			for(int j=0;j<basisVariable.length;++j) {
				if(i == basisVariable[j]) {
					result[i] = m[j][m[j].length-1];
				}
			}
		}
		return result;
	}
	
	public String toString() {
		StringBuffer s = new StringBuffer();
		
		s.append('\t');
		for(int i=0;i<nonBasisVariable.length;++i) {
			if(i<(nonBasisVariable.length-basisVariable.length)) {
				s.append('x');
				s.append(nonBasisVariable[i]+1);
			} else {
				s.append('s');
				s.append(nonBasisVariable[i]-(nonBasisVariable.length-basisVariable.length)+1);
			}
			s.append('\t');
		}
		s.append('\n');
		
		for(int i=0;i<m.length-1;++i) {
			if(basisVariable[i]<(nonBasisVariable.length-basisVariable.length)) {
				s.append('x');
				s.append(basisVariable[i]+1);
			} else {
				s.append('s');
				s.append(basisVariable[i]-(nonBasisVariable.length-basisVariable.length)+1);
			}
			s.append('\t');
			for(int j=0;j<m[i].length;++j) {
				s.append(m[i][j]);
				s.append('\t');
			}
			s.append('\n');
		}
		
		s.append('Z');
		s.append('\t');
		for(int i=0;i<m[m.length-1].length;++i) {
			s.append(m[m.length-1][i]);
			s.append('\t');
		}
		s.append('\n');
		
		
		return s.toString();
	}
}

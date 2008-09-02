package net.fs.algo;

public abstract class AbstractSimplex {

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

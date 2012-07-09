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

public class DualSimplex extends PrimalSimplex {

	private boolean primal;
	
	public void init() {
		super.init();
		this.primal = false;
	}

	public int iterate() {
		
		if(primal) {
			return super.iterate();
		}
		
		double quotient;
		
		// Select pivot row
		int pr = -1;
		double min = Double.POSITIVE_INFINITY;
		for(int i=0;i<m.length-1;++i) {
			if(
					m[i][m[i].length-1] < 0 &&
					m[i][m[i].length-1] < min) {
				
				pr = i;
				min = m[i][m[i].length-1];
			}
		}
		if(pr < 0) {
			for(int i=0;i<m[m.length-1].length-1;++i) {
				if(m[m.length-1][i] < 0) {
					// Start primal
					System.out.println("Continue with primal simplex");
					primal = true;
					return CONTINUE;
				}
			}
			return OPTIMAL;
		}
		
		// Select pivot column
		int pc = -1;
		double max = Double.NEGATIVE_INFINITY;
		if(pr > -1) {
			for(int i=0;i<m[pr].length-1;++i) {
				if(
						m[pr][i] < 0 &&
						(i < objective.length || !locked[i-objective.length])) {
					
					quotient = m[m.length-1][i] / m[pr][i];
					if(quotient > max) {
						/*
						 * 2012-07-09
						 *  Greathfully thanks to Andy Jose <andyymm21@gmail.com>
						 *  for finding a bug. It have to be "max" instead of "min"
						 *  
						 *  min = quotient;
						 */
						max = quotient;
						pc = i;
					}
				}
			}
			if(pc < 0) {
				return UNBOUNDED;
			}
		}
		
		// Pivot
		System.out.println("Pivo: row="+ (pr+1) +", column="+ (pc+1));
		pivot(pr, pc);
		
		return CONTINUE;
	}
}

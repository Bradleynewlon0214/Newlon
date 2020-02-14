package newlon;

import java.util.Arrays;
import Jama.Matrix;
import org.apache.commons.math3.distribution.FDistribution;
import org.apache.commons.math3.distribution.TDistribution;

public class LinearModelTwo {
	
	private Matrix X;
	private Matrix Y;
	
	private int degreesOfFreedomModel;
	private int degreesOfFreedomError;
	
	private String[] names;
	private String dependentVariable;
	
	private Matrix B;
	private Matrix residuals;
	private Matrix fittedY;
	private Matrix vcov;
	
	private double[] stdErrors;
	private double sigmaSquareHat;
	
	
	
	//utility funcion
	//Only works for arrays of all the same sizes
	//TODO: Add ability to run without intercept (remove column of 1's)
	public static double[][] zip(double[][] xs){
		
		int n = xs.length;
		double[][] ans = new double[n + 1][];
		for(int j = 0; j < n; j++ ) {
			double[] tmp = new double[ xs[0].length ];
			for(int i = 0; i < tmp.length; i++) {
				tmp[i] = xs[j][i];
			}
			ans[j] = tmp;
		}
		double[] ones = new double[ans[0].length];
		Arrays.fill(ones, 1);
		ans[ans.length-1] = ones;
		
		return ans;
	}
	
	private Matrix formatX(double[][] x) {
		Matrix xValues = new Matrix(x);
		return xValues.transpose();
	}
	
	private Matrix formatY(double[] y) {
		Matrix yValues = new Matrix(y, 1);
		return yValues.transpose();
	}
	
	private String[] setNames(String[] xNames) {
		String[] s = new String[xNames.length + 1];
		for(int i = 0; i < xNames.length; i++) {
			s[i] = xNames[i];
		}
		s[xNames.length] = "cons_";
		return s;
	}
		
	private void setDDOF() {
		degreesOfFreedomModel = X.getColumnDimension() - 1;
		degreesOfFreedomError = X.getRowDimension() - X.getColumnDimension();
	}
	
	public LinearModelTwo(String yName, String[] xNames, double[] y, double[][] x) {
		this.X = formatX(zip(x));
		this.Y = formatY(y);
		this.dependentVariable = yName;
		this.names = setNames(xNames);
		setDDOF();
		estimateBetas();
		vcovBetaHat();
		estimateStdErrors();
	}
	
	private void estimateBetas() {
		//(X'X)^-1 X'Y
		Matrix xTransposed = X.transpose();
		Matrix stepOne = xTransposed.times(X).inverse();
		Matrix stepTwo = xTransposed.times(Y);
		Matrix answer = stepOne.times(stepTwo);
		B = answer;
	}
		
	private void vcovBetaHat() {
		//sigma2_hat * (X'X)^-1
		Matrix xTransposed = X.transpose();
		Matrix s = xTransposed.times(X).inverse();
		vcov = s.timesEquals(sigmaSquareHat);
	}
	
	public void estimateStdErrors() {
		double[][] zzz = vcov.getArray();
		double[] std = new double[zzz.length];
		for(int i = 0; i < zzz.length; i++) {
			for(int j = 0; j < zzz[0].length; j++) {
				if(i == j) {
					std[i] = Math.sqrt(zzz[i][j]);
				}
			}
		}
		stdErrors = std;
	}
	
	private LinearModelTwo residuals() {
		//Y - XB
		Matrix residual;
		Matrix stepTwo = X.times(B);
		residual = Y.minus(stepTwo);
		residuals = residual;
		return this;
	}
	
	private LinearModelTwo predictedY() {
		fittedY = X.times(B);
		return this;
	}
}

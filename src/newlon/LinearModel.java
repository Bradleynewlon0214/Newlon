package newlon;

import java.util.Arrays;
import Jama.Matrix;
import org.apache.commons.math3.distribution.FDistribution;
import org.apache.commons.math3.distribution.TDistribution;

public class LinearModel {

	public Matrix x;
	public int numberOfRegressors;
	public int degreesOfFreedomError;
	public String[] names;
	public Matrix y;
	public String dependentVariable;
	private Matrix betas;
	private Matrix residuals;
	private Matrix fittedY;
	private double sigmaSquareHat;
	private double residStdErr;
	private Matrix vcov;
	private double[] stdErrors;
	private double[] t;
	private double ssr;
	private double tss;
	private double ess;
	private double msReg;
	private double msE;
	private double fStat;
	private double RSquared;
	private double adjustedR;
	private double[][] confIntervals;
	private double[] pValues;
	private double fpValue;
	
	private boolean fit;
	

	
	public LinearModel(String yName, String[] xNames, double[] y, double[][] x) {
		this.x = formatX(zip(x));
		this.y = formatY(y);
		this.dependentVariable = setDependentVariable(yName);
		this.names = setNames(xNames);
		
	}
	
	private String[] setNames(String[] xNames) {
		String[] s = new String[xNames.length + 1];
		for(int i = 0; i < xNames.length; i++) {
			s[i] = xNames[i];
		}
		s[xNames.length] = "cons_";
		return s;
	}
	
	public String setDependentVariable(String yName) {
		return yName;
	}
	
	private Matrix formatX(double[][] x) {
		Matrix xValues = new Matrix(x);
		return xValues.transpose();
	}
	private Matrix formatY(double[] y) {
		Matrix yValues = new Matrix(y, 1);
		return yValues.transpose();
	}
	
	public void setDDOF() {
		numberOfRegressors = x.getColumnDimension() - 1;
		degreesOfFreedomError = x.getRowDimension() - x.getColumnDimension();
	}
	
	public Matrix getX() {
		return this.x;
	}
	
	public Matrix getY() {
		return this.y;
	}
	
	
	private void estimateBetas() {
		//(X'X)^-1 X'Y
		Matrix xTransposed = x.transpose();
		Matrix stepOne = xTransposed.times(x).inverse();
		Matrix stepTwo = xTransposed.times(y);
		Matrix answer = stepOne.times(stepTwo);
		betas = answer;
	}
	public Matrix getBetas() {
		if(fit) return betas;
		else return null;
	}
	
	
	private void residuals() {
		//Y - XB
		Matrix residual;
		Matrix stepTwo = x.times(betas);
		residual = y.minus(stepTwo);
		residuals = residual;
	}
	public Matrix getResiduals() {
		if(fit) return residuals;
		else return null;
	}
	
	
	private void predictedY() {
		// yHat = XB
		fittedY = x.times(betas);
	}
	public Matrix getFittedY() {
		if(fit) return fittedY;
		else return null;
	}
	
	//not optimal
	private void sigmaSquaredHat() {
		//S^2 = s(E'E)/(n-p)
		int p = x.getColumnDimension();
		int n = x.getRowDimension();
		Matrix residualTransposed = residuals.transpose();
		Matrix stepOne = residualTransposed.times(residuals);
		double[][] ans = stepOne.getArray();
		sigmaSquareHat = ans[0][0]/(n-p);
	
	}
	public double getSigmaSquaredHat() {
		if(fit) return sigmaSquareHat;
		else return 0;
	}
	
	private void residualStdError() {
		residStdErr = Math.sqrt(sigmaSquareHat);
	}
	public double getResidualStandardError() {
		if(fit) return residStdErr;
		else return 0;
	}
	
	private void vcovBetaHat() {
		//sigma2_hat * (X'X)^-1
		Matrix xTransposed = x.transpose();
		Matrix s = xTransposed.times(x).inverse();
		vcov = s.timesEquals(sigmaSquareHat);
	}
	
	public Matrix getVCOVBetaHat() {
		if(fit) return vcov;
		else return null;
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
	public double[] getStandardErrors() {
		if(fit) return stdErrors;
		else return null;
	}
	
	//https://en.wikipedia.org/wiki/Explained_sum_of_squares#Partitioning_in_the_general_OLS_model
	private void SSR() {
		Matrix residualTransposed = residuals.transpose();
		Matrix stepOne = residualTransposed.times(residuals);
		ssr = stepOne.getArray()[0][0];
	}
	public double getSSR() {
		if(fit) return ssr;
		else return 0;
	}
	
	//https://en.wikipedia.org/wiki/Explained_sum_of_squares#Partitioning_in_the_general_OLS_model
	private void TSS() {
		MiscStats ms = new MiscStats();
		double [] yy = y.getRowPackedCopy();
		double mean = ms.mean(yy);
		Matrix constY = new Matrix(y.getRowDimension(), 1, mean);
		Matrix stepOne = y.minus(constY).transpose();
		Matrix stepTwo = y.minus(constY);
		Matrix stepThree = stepOne.times(stepTwo);
		tss = stepThree.getArray()[0][0];

	}
	public double getTSS() {
		if(fit) return tss;
		else return tss;
	}
	
	
	//https://en.wikipedia.org/wiki/Explained_sum_of_squares#Partitioning_in_the_general_OLS_model
	private void ESS() {
		MiscStats ms = new MiscStats();
		double[] yy = y.getRowPackedCopy();
		double mean = ms.mean(yy);
		Matrix constY = new Matrix(y.getRowDimension(), 1, mean);
		Matrix stepOne = fittedY.minus(constY).transpose();
		Matrix stepTwo = fittedY.minus(constY);
		Matrix stepThree = stepOne.times(stepTwo);
		ess = stepThree.getArray()[0][0];
		
	}
	public double getESS() {
		if(fit) return ess;
		else return 0;
	}
	
	private void meanSquareRegression() {
		// explained sum square / no of predictors
		int p = x.getColumnDimension() - 1;
		msReg = ess / p;
	}
	public double getMeanSquareRegression() {
		if(fit) return msReg;
		else return 0;
	}
	
	
	private void meanSquareError() {
		int p = x.getColumnDimension();
		int n = x.getRowDimension();
		msE = ssr / (n-p);
	}
	public double getMSE() {
		if(fit) return msE;
		else return 0;
	}
	
	
	private void tValues() {
		double[][] coef = betas.getArray();
		double[] tStats = new double[coef.length];
		for(int i = 0; i < coef.length; i++) {
			tStats[i] = coef[i][0] / stdErrors[i];
		}
		t = tStats;
	}
	public double[] getTStats() {
		if(fit) return t;
		else return null;
	}
	
	private void fStatistic() {
		fStat = msReg / sigmaSquareHat;
	}
	public double getFStat() {
		if(fit) return fStat;
		else return 0;
	}
	
	
	private void R() {
		RSquared = 1 - (ssr / tss);
	}
	public double getRSquared() {
		if(fit) return RSquared;
		else return 0;
	}
	
	
	
	private void adjR() {
		int n = x.getRowDimension();
		int p = x.getColumnDimension();
		adjustedR = 1 - (ssr * (n - 1)) / (tss * (n - p)); //with intercept
		//adjustedR = 1 - (1 - RSquared) * (n / (n - p)); //without intercept
	}
	public double getAdjR() {
		if(fit) return adjustedR;
		else return 0;
	}

	private void confint(double alpha) {
		//Bj +- tcrit * SE(Bj)
		double a = alpha / 2;
		int p = x.getColumnDimension();
		int n = x.getRowDimension();
		double[][] coefs = betas.getArray();
		int s = stdErrors.length;
		confIntervals = new double[s][2];
		//get critical t value (alpha/2) where df = n - (m+1)
		TDistribution td = new TDistribution(n-p);
		double crit = Math.abs(td.inverseCumulativeProbability(a)); //fuck this? also, don't pass an alpha to the constructor, it'll fuck it up
		for(int i = 0; i < coefs.length; i++) {
			confIntervals[i][0] = coefs[i][0] - (crit*stdErrors[i]);
			confIntervals[i][1] = coefs[i][0] + (crit*stdErrors[i]);
		}
	}
	public double[][] getConfInt(){
		if(fit) return confIntervals;
		else return null;
	}
	
	
	private void p() {
		int p = x.getColumnDimension();
		int n = x.getRowDimension();
		pValues = new double[t.length];
		TDistribution td = new TDistribution(n-p);
		for(int i = 0; i < t.length; i++) {
			if(t[i] < 0) {
				pValues[i] = td.cumulativeProbability(t[i]) * 2;
			} else {
				pValues[i] = (1-td.cumulativeProbability(t[i])) * 2;
			}
		}
	}
	public double[] getPValues() {
		 if(fit) return pValues;
		 else return null;
	}
	
	private void fp() {
		int p = x.getColumnDimension();
		int n = x.getRowDimension();
		FDistribution fd = new FDistribution(p - 1, n - p);
		fpValue = 1 - fd.cumulativeProbability(fStat);
	}
	public double getFPValue() {
		if(fit) return fpValue;
		else return 0;
	}
	
	//don't ask
	public void fit() {
		
		estimateBetas();
		residuals();
		predictedY();
		sigmaSquaredHat();
		residualStdError();
		vcovBetaHat();
		estimateStdErrors();
		tValues();
		SSR();
		TSS();
		ESS();
		meanSquareRegression();
		meanSquareError();
		fStatistic();
		R();
		adjR();
		p();
		fp();
		fit = true;
	}
	
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
	
}
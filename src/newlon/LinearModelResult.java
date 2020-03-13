package newlon;


public class LinearModelResult extends LinearModel {
	
	public LinearModelResult(String yName, String[] xNames, double[] y, double[][] x) {
		super(yName, xNames, y, x);
		fit();
	}
	
	//Modeled off R's summary() method
	public void summary() {
		double[][] betas = getBetas().getArray();
		double[] stdErrors = getStandardErrors();
		double[] pValues = getPValues();
		double[] t = getTStats();
		
		System.out.println("Dependent Variable: " + dependentVariable);
		System.out.println("Coefficients \t Estimates \t\t Std. Error \t\t T-Stat \t\t P-values");
		for(int i = 0; i < betas.length; i++) {
			System.out.println(names[i] + "\t\t" +betas[i][0] + "\t" + stdErrors[i] + "\t" + t[i] + "\t" + pValues[i]);
		}
		System.out.println("\nResidual Standard Error: " + getResidualStandardError() + " on " + (x.getRowDimension() - x.getColumnDimension()) + " degrees of freedom");
		System.out.println("R-Squared: " + getRSquared() + ", Adjusted R-Squared: " + getAdjR());
		System.out.println("F-Stat: " + getFStat() + " on " + (x.getColumnDimension() - 1) + " and " + (x.getRowDimension() - x.getColumnDimension()) + " degrees of freedom, " + "p-value: " + getFPValue());
		
		
		
		
		
		System.out.println("Total Sum Square: " + getTSS());
		System.out.println("Explained Sum Square: " + getESS());
		System.out.println("Residual Sum Square: " + getSSR());
		
		System.out.println("Vcov: " + getVCOVBetaHat());
		System.out.println("Sigma Squared Hat: " + getSigmaSquaredHat());
		System.out.println("Residual Standard Error: " + getResidualStandardError());
		System.out.println("MSE: " + getMSE());
		
	}
	
	@Override
	public String toString() {
		summary();
		return "worked";
	}

}

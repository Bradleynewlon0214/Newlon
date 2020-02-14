package newlon;

public interface Inference {
	
	public double[] confint(double[] data, int alpha, int ddof);
	
	public double[] predint(double[] data, int alpha, int ddof);
	

}

package newlon;

import org.apache.commons.math3.distribution.TDistribution;

public class MiscInference implements Inference {

	@Override
	public double[] confint(double[] data, int alpha, int ddof) {
		double[] interval = new double[2];
		MiscStats ms = new MiscStats();
		double meanData = ms.mean(data);
		TDistribution td = new TDistribution(data.length -1);
		double tCrit = td.inverseCumulativeProbability(alpha);
		double upperBound = meanData + (tCrit * Math.sqrt(ms.variance(data, data.length - 1) / data.length - 1));
		double lowerBound = meanData - (tCrit * Math.sqrt(ms.variance(data, data.length - 1) / data.length - 1));
		interval[0] = lowerBound;
		interval[1] = upperBound;
		
		return interval;
	}

	@Override
	public double[] predint(double[] data, int alpha, int ddof) {
		double[] interval = new double[2];
		MiscStats ms = new MiscStats();
		double meanData = ms.mean(data);
		TDistribution td = new TDistribution(data.length -1);
		double tCrit = td.inverseCumulativeProbability(alpha);
		
		double upperBound = meanData + (tCrit * Math.sqrt(ms.variance(data, data.length - 1) + (ms.variance(data, data.length - 1) / data.length - 1)));
		double lowerBound = meanData - (tCrit * Math.sqrt(ms.variance(data, data.length - 1) + (ms.variance(data, data.length - 1) / data.length - 1)));
		interval[0] = lowerBound;
		interval[1] = upperBound;
		
		return interval;
	}

	
	
}

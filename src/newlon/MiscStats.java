package newlon;

public class MiscStats {
	
	
	public static void selectionSort(double[] arr) {
		int n = arr.length;
		
		for(int i = 0; i < n - 1; i++) {
			int min = i;
			
			for(int j = i + 1; j < n; j++) {
				if(arr[j] < arr[min])
					min = j;
			}
			
			double tmp = arr[min];
			arr[min] = arr[i];
			arr[i] = tmp;
			
		}
	}
	
	public static double sum(double[] values) {
		double ans = 0;
		for(double val: values) {
			ans += val;
		}
		return ans;
	}
	
	public static double mean(double[] values) {
		return sum(values)/values.length;
	}
	
	public static double standardDeviation(double[] values, int ddof) {
		double meanValues = mean(values);
		double df = 1 / ddof;
		double ans = 0;
		for(double val: values) {
			ans += Math.pow(val - meanValues, 2);
		}
		
		return Math.sqrt(df * ans);
	}
	
	public static double variance(double[] values, int ddof) {
		return Math.pow(standardDeviation(values, ddof), 2);
	}
	
	public static double coVariance(double[] y, double[] x, int ddof) {
		
		double yMean = mean(y);
		double xMean = mean(x);
		double yAns = 0, xAns = 0;
		if(y.length == x.length) {
			for(int i = 0; i < y.length; i++) {
				yAns += y[i] - yMean;
				xAns += x[i] - xMean;
			}
			
		}
		return (xAns * yAns) / ddof;
	}
	
	public static double correlation(double[] y, double[] x, int ddof) {
		return coVariance(y, x, ddof) / ( standardDeviation(x, ddof) * standardDeviation(y, ddof) );
	}
	
	public static double median(double[] values) {
		selectionSort(values);
		if(values.length % 2 == 0) {
			double ans = (values[ values.length / 2 ] + values[(values.length / 2) - 1]) / 2;
			return ans;
			
		} else {
			return values[ values.length / 2];
		}
	}
	
}

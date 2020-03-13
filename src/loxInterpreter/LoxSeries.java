package loxInterpreter;

import java.util.List;

import DataFrame.Series;

public class LoxSeries {

	Series series;
	
	LoxSeries(List<Object> values){
		this.series = new Series(values.toArray());
	}
	
	@Override
	public String toString() {
		return series.toString();
	}
	
	
	public Object get(int index) {
		return series.get(index);
	}
	
	public void set(int index, Object value) {
		series.set(index, value);
	}
	
	public double sum() {
		try {
			return series.sum();
		} catch(RuntimeError e) {
			throw new RuntimeError(null, "Series must be numeric");
		}
	}
	
	public double mean() {
		try {
			return series.mean();
		} catch(RuntimeError e) {
			throw new RuntimeError(null, "Series must be numeric");
		}
	}
	
	public double variance() {
		try {
			return series.mean();
		} catch(RuntimeError e) {
			throw new RuntimeError(null, "Series must be numeric");
		}
	}
	
}

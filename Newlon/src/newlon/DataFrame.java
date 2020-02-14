package newlon;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Set;





public class DataFrame {

	private LinkedHashMap<String, ArrayList<Object>> data;
	private String path;
	private String delim;
	
	public DataFrame(String path, String delim){
		this.path = path;
		this.delim = delim;
		data = readCSV(path, delim);
	}
	
	public LinkedHashMap<String, ArrayList<Object>> readCSV(String path, String delim) {
		BufferedReader buffReader = null;
		String line = "";
		LinkedHashMap<String, ArrayList<Object>> df = new LinkedHashMap<String, ArrayList<Object>>();
		
		try {
			
			buffReader = new BufferedReader(new FileReader(path));
			String[] columns = buffReader.readLine().split(delim);
			
			for(String column: columns) {
				df.put(column, new ArrayList<Object>());
			}
			
			while( (line = buffReader.readLine()) != null ) {
				String[] contents = line.split(delim);
				for(int i = 0; i < contents.length; i++) {
					df.get(columns[i]).add(contents[i]);
				}
			}
			
			
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		} catch(IOException e) {
			e.printStackTrace();
		} finally {
			if(buffReader != null) {
				try {
					buffReader.close();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
		}
		return df;
	}
	
	public Set<String> getNames() {
		Set<String> keys = data.keySet();
		return keys;
	}
	
	public double[] columnToDouble(String key) {
		
		ArrayList<Object> test = data.get(key);
		double[] tmax = new double[test.size()];
		for(int i = 0; i < test.size(); i++) {
			tmax[i] = Double.parseDouble(test.get(i).toString());
		}
		return tmax;
	}

	
	
}

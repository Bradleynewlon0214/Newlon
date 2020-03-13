package DataFrame;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;




public class DataFrame {

	private HashMap<String, Series<Object>> data;
	private String path;
	private String delim;
	
	
	public DataFrame(String path) {
		this.path = path;
		this.delim = ",";
		readCSV(path, delim);
	}
	
	public DataFrame(String path, String delim){
		this.path = path;
		this.delim = delim;
		readCSV(path, delim);
	}
	
		
	public Series<Object> get(String key){
		return data.get(key);
	}
	
	public DataFrame readCSV(String path, String delim) {
		BufferedReader buffReader = null;
		String line = "";
		this.data = new HashMap<String, Series<Object>>();
		try {
			buffReader = new BufferedReader(new FileReader(path));
			String[] columns = buffReader.readLine().split(delim);
			for(String column: columns) {
				data.put(column, new Series<Object>());
			}		
			while( (line = buffReader.readLine()) != null ) {
				
				Object[] contents = line.split(delim);
				
				for(int i = 0; i < contents.length; i++) {
					data.get(columns[i]).add(contents[i]);
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
		return this;
	}
	
	public Set<String> getNames() {
		Set<String> keys = data.keySet();
		return keys;
	}
	
	@Override
	public String toString() {
		return getNames().toString();
	}	
}
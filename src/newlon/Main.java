package newlon;

import java.util.Arrays;
import java.util.Scanner;

//TODO:FIX THIS
public class Main {
	
	
	public static void main(String[] args) {
		
		
		//this is all stupid, write a lexer and parser for a more complete solution
		
		//Commands I would like to add:
		//wd = set working directory
		//read = read a csv
		//lm = linearmodel (already added)
		//anova = anova
		//confint = confidence interval for a list of numbers
		//predint = prediction interval for a list of numbers
		
		//I know this isn't the optimal way to add these, but it works for now
		
		
		String workingDir = "C:\\Users\\bradley\\Desktop\\R\\";
		
		
		Scanner in = new Scanner(System.in);
		System.out.println("Type 'help' for list of commands");
		System.out.println("Start by entering a csv file.");
		System.out.print(">>");
		String file = in.nextLine();
		DataFrame df = new DataFrame(workingDir + file, ",");
		System.out.println("Enter a command.");
		System.out.print(">>");
		String cmd = in.nextLine();
		String[] brk = cmd.split(" ");

		if(brk[0].contains("lm")) {
			String[] xValues = Arrays.copyOfRange(brk, 2, brk.length);
			System.out.println(Arrays.toString(xValues));
			String yValue = brk[1];
			double[][] xs = new double[xValues.length][];
			double[] y = df.columnToDouble(yValue);

			for(int i = 0; i < xValues.length; i++) {
				xs[i] = df.columnToDouble(xValues[i]);
			}
			
			LinearModelResult lm = new LinearModelResult(yValue, xValues, y, xs);
			lm.summary();	
		}
		in.close();
		
	}

}

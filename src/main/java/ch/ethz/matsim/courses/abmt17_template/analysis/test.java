package ch.ethz.matsim.courses.abmt17_template.analysis;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import ch.ethz.matsim.courses.abmt17_template.JavaUtil.MyDoubleEntry;

public class test {

	public static void main(String[] args) throws NumberFormatException, IOException {
		// TODO Auto-generated method stub
		
		String[] Fs = {"Fs100","Fs200","Fs400","Fs800"};
		String[] St = {"St5","St50"};
		String before = "scenario/Logfiles/";
		String log = "_logfile.log";
		String warn = "_logfileWarningsErrors.log";
		
		Map<String,Map<Integer,Integer>> counts = new HashMap<String,Map<Integer,Integer>>();
		
		for (int i =0; i<2; i++) {
			for (int j = 0; j<4; j++) {
				System.out.println("Counts "+ St[i]+"_"+Fs[j]);
				BelowZeroCounter counter = new BelowZeroCounter(before+St[i]+"_"+Fs[j]+warn,before+St[i]+"_"+Fs[j]+log);
				counts.put(St[i]+"_"+Fs[j],counter.count());
			}
		}
		
		try {
			FileWriter fileWriter = new FileWriter("BelowZeroStatistic2.csv");
			BufferedWriter writer = new BufferedWriter(fileWriter);
			writer.write("Iteration");
			for (int i =0; i<2; i++) {
				for (int j = 0; j<4; j++) {
					writer.write("," + St[i]+"_"+Fs[j]);
				}
			}
			writer.newLine();
			for (int it = 0; it<201; it++) {
				writer.write(it+"");
				
				for (int i =0; i<2; i++) {
					for (int j = 0; j<4; j++) {
						writer.write(","+ counts.get(St[i]+"_"+Fs[j]).get(it));
					}
				}
				writer.newLine();
			}
			
			writer.flush();
			writer.close();
			
			
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		
	}

}

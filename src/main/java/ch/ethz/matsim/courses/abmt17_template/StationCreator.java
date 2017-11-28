package ch.ethz.matsim.courses.abmt17_template;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;

import com.google.inject.Inject;
import com.google.inject.Singleton;



@Singleton
public class StationCreator {
	
	final private Network network;

	public StationCreator(Network network) {
		this.network = network;
	}

	public void createFile (File path) throws IOException {
		

		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
		FileWriter filewriter = new FileWriter("scenario/stations_zuerich.csv");
		BufferedWriter writer = new BufferedWriter( filewriter );
		
		writer.write("stationId,linkId,numberOfQueues,rechargeRate,min Distance, x-coord,y-coord");
		writer.newLine();
		
		String line = null;
		List<String> header = null;
		Map<String,Coord> stations = new HashMap<String,Coord>();
		
		while ((line = reader.readLine()) != null) {
			List<String> row = Arrays.asList(line.split(","));

			if (header == null) {
				header = row;

			} else {

				Coord coord = new Coord(Double.parseDouble(row.get(header.indexOf("x-coord"))),
						Double.parseDouble(row.get(header.indexOf("y-coord"))));
				
				String name = row.get(header.indexOf("Name"));
				
				double minDist = 1000000;
				Id<Link> closeLink= null;
				
					for ( Link links : network.getLinks().values() ) {
						Id<Link> linkId = links.getId();
						double xStart = links.getFromNode().getCoord().getX();
						double yStart = links.getFromNode().getCoord().getY();
						double xEnd = links.getToNode().getCoord().getX();
						double yEnd = links.getToNode().getCoord().getY();
					
						double x = coord.getX();
						double y = coord.getY();
						
						double s = ( Math.pow(xStart,2) - xStart*(xEnd+x) + xEnd*x + Math.pow(yStart, 2) - yStart*(yEnd+y) + yEnd*y ) /
								( Math.pow(xStart, 2) - 2*xStart*xEnd + Math.pow(xEnd, 2) + Math.pow(yStart, 2) - 2*yStart*yEnd + Math.pow(yEnd, 2) );
						
						double t = ( xStart*(yEnd-y) - xEnd*(yStart-y) + x*(yStart-yEnd) ) /
								( Math.pow(xStart, 2) - 2*xStart*xEnd + Math.pow(xEnd, 2) + Math.pow(yStart, 2) - 2*yStart*yEnd + Math.pow(yEnd, 2) );
						
						double dist = Math.sqrt( Math.pow(x-xStart, 2) + Math.pow(y-yStart, 2) ) - 
								s * Math.sqrt( Math.pow(xEnd-xStart, 2) + Math.pow(yEnd-yStart, 2) );
						
						if ( s >= 0 && s <= 1) {
							if ( minDist > dist) {
								minDist = dist;
								closeLink = linkId;
							}
						}
						/*double dist = Math.pow(x-xAvg,2) + Math.pow(y-yAvg,2);
						
						if ( dist < minDist ) {
							minDist = dist;
							closeLink = linkId;
						}
						*/
					}
					
					
					
					try {
						if ( minDist < 50 ) {
							
							writer.write( name + "," + closeLink + "," + "1,38.4," + minDist + "," + (int)coord.getX() + "," + (int) coord.getY());
							System.out.println(name + "," + closeLink + "," + "1,38.4," + minDist);
							writer.newLine();
						}
					} catch (IOException e) {
						throw new RuntimeException( e );
					}
					
					
				

			}
		}

		writer.flush();
		reader.close();
		writer.close();

	}

}

package ch.ethz.matsim.courses.abmt17_template.analysis;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.contrib.dvrp.run.DvrpConfigGroup;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.Controler;
import org.matsim.core.events.EventsUtils;
import org.matsim.core.events.MatsimEventsReader;
import org.matsim.core.network.io.NetworkReaderMatsimV2;
import org.matsim.core.population.io.PopulationReader;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.io.IOUtils;
import org.matsim.vehicles.Vehicle;
import org.matsim.vehicles.VehicleType;

import com.google.inject.Provides;
import com.google.inject.Singleton;

import ch.ethz.matsim.av.electric.assets.station.Station;
import ch.ethz.matsim.av.electric.tracker.ConsumptionTracker;
import ch.ethz.matsim.av.framework.AVConfigGroup;
import ch.ethz.matsim.courses.abmt17_template.StationReader;
import ch.ethz.matsim.courses.abmt17_template.JavaUtil.MapWriter;
import ch.ethz.matsim.courses.abmt17_template.JavaUtil.MyDoubleEntry;

public class RunWithEventHandler {

	public static void main(String[] args) {
		
		String configPath = "scenario/abmt_config.xml";
		String populationPath = "scenario/abmt_population.xml.gz";
		String networkPath = "scenario/abmt_network.xml.gz";
		
		String eventsPath = "simulation_output/ITERS/it.150/150.events.xml.gz";
		
		Config config = ConfigUtils.loadConfig(configPath, new DvrpConfigGroup(), new AVConfigGroup());
		//Config config = ConfigUtils.createConfig();
		Scenario scenario = ScenarioUtils.createScenario(config);
		
		PopulationReader populationReader = new PopulationReader(scenario);
		
		populationReader.readFile(populationPath);
		
		NetworkReaderMatsimV2 networkReader = new NetworkReaderMatsimV2(scenario.getNetwork());
		
		networkReader.readFile(networkPath);
		
		EventsManager eventsManager = EventsUtils.createEventsManager();
		
		
		MatsimEventsReader eventsReader = new MatsimEventsReader(eventsManager);
		

		ConsumptionTracker tracker = null;
		StationReader reader = new StationReader(scenario.getNetwork(), tracker, eventsManager);
		Collection<Station> stations = null;
		try {
			stations = reader.read(new File("scenario/stations_zuerich3.csv"));
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		//StationEventHandler stationEventHandler = new StationEventHandler(stations);
		//eventsManager.addHandler(stationEventHandler);
		VehicleEventHandler vehicleEventHandler = new VehicleEventHandler(scenario.getNetwork());
		eventsManager.addHandler(vehicleEventHandler);
		
		eventsReader.readFile(eventsPath);
		FileWriter fileWriter = null;
		BufferedWriter writer = null;
		
		//* ***********New Writer***************************************************
		
		//Writer to write down how many Arrivals a Station had
		
		Map<String, MyDoubleEntry> distance = vehicleEventHandler.getVehicleDistances();
		Map<String, MyDoubleEntry> time = vehicleEventHandler.getTravelTime();
		Map<String, Double> charge = vehicleEventHandler.getVehicleCharge();
		
		try {
			System.out.println("Writer writes VehicleAnalysis");
			fileWriter = new FileWriter("scenario/150VehicleAnalysis.csv");
			writer = new BufferedWriter(fileWriter);
			writer.write("VehicleId,DistanceWithPassenger,DistanceTotal,TimeWithPassenger,TimeTotal,Charge");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		String[] vehicle = new String[distance.size()];
		String[] print = new String[distance.size()];
		int i = 0;
		for (Map.Entry<String, MyDoubleEntry> entry : distance.entrySet()) {
			vehicle[i] = entry.getKey();
			print[i] = entry.getKey() +","+ entry.getValue();
			i++;
		}
		
		for (Map.Entry<String, MyDoubleEntry> entry : time.entrySet()) {
			int j = 0;
			while (j<vehicle.length) {
				if (vehicle[j] == entry.getKey()) {
					print[j] = print[j] +","+ entry.getValue();
				}
				j++;
			}
		}

		for (Map.Entry<String, Double> entry : charge.entrySet()) {
			int j = 0;
			while (j<vehicle.length) {
				if (vehicle[j] == entry.getKey()) {
					print[j] = print[j] +","+ entry.getValue();
				}
				j++;
			}
		}

		try {
			for (int j = 0; j<print.length; j++) {
				writer.newLine();
				writer.write(print[j]);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// *************End Writer***************************************************
		
	
		/* ***********New Writer***************************************************
		 * 
		 *  Writer to check whats going on, on a specific Station
		
		try {
			System.out.println("Writer writes StationActivity");
			fileWriter = new FileWriter("scenario/2Station164Activity.csv");
			writer = new BufferedWriter(fileWriter);
			writer.write("Time,VehicleId,EventType");
			List<String> stationActivity = stationEventHandler.getStationActivity();
			for (String print : stationActivity) {
				writer.newLine();
				writer.write(print);
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 *************End Writer***************************************************
		
		*/
		
		/* ***********New Writer***************************************************
		 * 
		 * Writer to write down how many Arrivals a Station had
		try {
			System.out.println("Writer writes StationChargings");
			fileWriter = new FileWriter("scenario/150StationArrivals.csv");
			writer = new BufferedWriter(fileWriter);
			writer.write("StationId,numberOfArrivals");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		Map<Station,Integer> stationArrivals = stationEventHandler.getStationArrivals();
		for (Map.Entry<Station, Integer> entry : stationArrivals.entrySet()) {
			try {
				writer.newLine();
				writer.write(entry.getKey().getId() +","+ entry.getValue());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 *************End Writer***************************************************
		*/

	}

}

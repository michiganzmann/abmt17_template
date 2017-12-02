package ch.ethz.matsim.courses.abmt17_template.analysis;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

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

import com.google.inject.Provides;
import com.google.inject.Singleton;

import ch.ethz.matsim.av.electric.assets.station.Station;
import ch.ethz.matsim.av.electric.tracker.ConsumptionTracker;
import ch.ethz.matsim.av.framework.AVConfigGroup;
import ch.ethz.matsim.courses.abmt17_template.StationReader;

public class RunWithEventHandler {

	public static void main(String[] args) {
		
		String configPath = "scenario/abmt_config.xml";
		String populationPath = "scenario/abmt_population.xml.gz";
		String networkPath = "scenario/abmt_network.xml.gz";
		
		String eventsPath = "scenario/80.events.xml.gz";
		
		
		Config config = ConfigUtils.createConfig();
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
			stations = reader.read(new File("scenario/stations_zuerich.csv"));
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		StationEventHandler stationEventHandler = new StationEventHandler(stations);
		eventsManager.addHandler(stationEventHandler);
		
		eventsReader.readFile(eventsPath);
		
		FileWriter fileWriter = null;
		BufferedWriter writer = null;
		try {
			fileWriter = new FileWriter("scenario/StationChargings.csv");
			writer = new BufferedWriter(fileWriter);
			writer.write("StationId,numberOfArrivals");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		Map<Station,Integer> stationArrivals = stationEventHandler.getStartRecharge();
		for (Map.Entry<Station, Integer> entry : stationArrivals.entrySet()) {
			System.out.println(entry.getKey().getId() +","+ entry.getValue());
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
		

	}

}

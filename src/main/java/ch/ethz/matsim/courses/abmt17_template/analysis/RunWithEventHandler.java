package ch.ethz.matsim.courses.abmt17_template.analysis;

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

import ch.ethz.matsim.av.framework.AVConfigGroup;

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
		
		eventsManager.addHandler(new StationEventHandler());
		
		MatsimEventsReader eventsReader = new MatsimEventsReader(eventsManager);
		
		eventsReader.readFile(eventsPath);
		

	}

}

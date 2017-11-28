package ch.ethz.matsim.courses.abmt17_template;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.matsim.api.core.v01.Scenario;
import org.matsim.contrib.dvrp.run.DvrpConfigGroup;
import org.matsim.contrib.dvrp.trafficmonitoring.DvrpTravelTimeModule;
import org.matsim.contrib.dynagent.run.DynQSimModule;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.AbstractModule;
import org.matsim.core.controler.Controler;
import org.matsim.core.scenario.ScenarioUtils;

import com.google.inject.Provides;
import com.google.inject.Singleton;

import abmt17.pt.ABMTPTModule;
import abmt17.scoring.ABMTScoringModule;
import ch.ethz.matsim.av.electric.NewRechargingModule;
import ch.ethz.matsim.av.electric.assets.battery.BatterySpecification;
import ch.ethz.matsim.av.electric.assets.battery.DefaultBatterySpecification;
import ch.ethz.matsim.av.electric.assets.station.Station;
import ch.ethz.matsim.av.electric.consumption.ConsumptionCalculator;
import ch.ethz.matsim.av.electric.consumption.StaticConsumptionCalculator;
import ch.ethz.matsim.av.electric.tracker.CSVConsumptionTracker;
import ch.ethz.matsim.av.electric.tracker.ConsumptionTracker;
import ch.ethz.matsim.av.extern.BinCalculator;
import ch.ethz.matsim.av.framework.AVConfigGroup;
import ch.ethz.matsim.av.framework.AVModule;
import ch.ethz.matsim.av.framework.AVQSimProvider;
import ch.ethz.matsim.baseline_scenario.analysis.simulation.ModeShareListenerModule;
import ch.ethz.matsim.courses.abmt17_template.scoring.AVScoringModuleForABMT;

/**
 * This is the template for the 2017 ABMT course at ETHZ
 * 
 * With this example you can run the scenario. The only argument to the script
 * is the path to the config file (abmt_config.xml).
 * 
 * In Eclipse, for instance, you can right click on the "main" method and choose
 * "Run As ..." -> "Java Application". At first you will get an error, because
 * no command line argument has been provided. In the run menu "Green Arrow"
 * under the menu bar you can now click on "Run configurations ..." where you
 * can choose "RunScenarioExample". Click on "Arguments" and set the command
 * line arguments to "abmt_config.xml".
 * 
 * Furthermore, add the following to VM aruments: -Xmx10G It will tell Java to
 * use up to 10GB of RAM for the simulation.
 */
public class RunScenarioExample {
	static public void main(String[] args) {
		String configPath = "scenario/abmt_config.xml";
		String stationsPath = "scenario/stations_zuerich.csv";
		
		

		// Load the config file (command line argument)
		Config config = ConfigUtils.loadConfig(configPath, new DvrpConfigGroup(), new AVConfigGroup());

		Scenario scenario = ScenarioUtils.loadScenario(config); // Load scenario
		Controler controler = new Controler(scenario); // Set up simulation controller

		// Some additional modules to create a more realistic simulation
		controler.addOverridingModule(new ABMTScoringModule()); // Required if scoring of activities is used
		controler.addOverridingModule(new ABMTPTModule()); // More realistic "teleportation" of public transport trips
		controler.addOverridingModule(new ModeShareListenerModule()); // Writes correct mode shares in every iteration

		// Additional modules for AVs
		controler.addOverridingModule(new DvrpTravelTimeModule());
		controler.addOverridingModule(new DynQSimModule<>(AVQSimProvider.class));
		controler.addOverridingModule(new AVModule());

		// Fix scoring after AVs have been added to the scenario
		controler.addOverridingModule(new AVScoringModuleForABMT());

		// Adding electric vehicles
		// controler.addOverridingModule(new RechargingModule());
		controler.addOverridingModule(new NewRechargingModule());

		// Specify what the batteries look like and how the consumption works
		controler.addOverridingModule(new AbstractModule() {
			@Override
			public void install() {
				// First argument: Maximum recharge rate of the battery (kWh / s)
				// Second argument: Maximum charge state of the battery (kWh)
				BatterySpecification batterySpecification = new DefaultBatterySpecification(38.40 / 3600.0, 19.20);
				bind(BatterySpecification.class).toInstance(batterySpecification);

				// First argument: Discharge rate by distance (kWh / m)
				// Second argument: Discharge rate by time (kWh / s)
				ConsumptionCalculator consumptionCalculator = new StaticConsumptionCalculator(0.19 / 1000.0,
						0.8 / 3600.0);
				bind(ConsumptionCalculator.class).toInstance(consumptionCalculator);

				// Track consumption
				BinCalculator binCalculator = BinCalculator.createByInterval(18000.0, 79200.0, 600.0);
				CSVConsumptionTracker consumptionTracker = new CSVConsumptionTracker(binCalculator);
				bind(ConsumptionTracker.class).toInstance(consumptionTracker);
				addControlerListenerBinding().toInstance(consumptionTracker);

				// Register the station reader
				bind(StationReader.class);
				

				StationCreator creator = new StationCreator(scenario.getNetwork());
				try {
					creator.createFile(new File ("scenario/Gasstations.csv"));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
			
			
			// Provide the loaded stations
			
			@Provides
			@Singleton
			public Collection<Station> provideStations(StationReader reader) throws NumberFormatException, IOException {
				return reader.read(new File(stationsPath));
			}
		});

		controler.run();
	}
}

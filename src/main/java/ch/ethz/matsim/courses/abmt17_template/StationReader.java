package ch.ethz.matsim.courses.abmt17_template;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.api.experimental.events.EventsManager;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import ch.ethz.matsim.av.electric.assets.station.ParallelFIFO;
import ch.ethz.matsim.av.electric.assets.station.ParallelFIFOSpecification;
import ch.ethz.matsim.av.electric.assets.station.Station;
import ch.ethz.matsim.av.electric.tracker.ConsumptionTracker;

@Singleton
public class StationReader {
	final private Network network;
	final private ConsumptionTracker consumptionTracker;
	final private EventsManager eventsManager;

	@Inject
	public StationReader(Network network, ConsumptionTracker consumptionTracker, EventsManager eventsManager) {
		this.network = network;
		this.consumptionTracker = consumptionTracker;
		this.eventsManager = eventsManager;
	}

	public Collection<Station> read(File path) throws NumberFormatException, IOException {
		Set<Station> stations = new HashSet<>();
		

		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path)));

		
		String line = null;
		List<String> header = null;

		while ((line = reader.readLine()) != null) {
			List<String> row = Arrays.asList(line.split(","));

			if (header == null) {
				header = row;

			} else {


				Id<Station> stationId = Id.create(row.get(header.indexOf("stationId")), Station.class);
				Id<Link> linkId = Id.createLinkId(row.get(header.indexOf("linkId")));

				long numberOfQueues = Long.parseLong(row.get(header.indexOf("numberOfQueues")));
				double rechargeRate = Double.parseDouble(row.get(header.indexOf("rechargeRate"))) / 3600.0;
				
				Link link = network.getLinks().get(linkId);
				
				stations.add(
						new ParallelFIFO(stationId, link, new ParallelFIFOSpecification(numberOfQueues, rechargeRate),
								consumptionTracker, eventsManager));

			}
		}

		
		reader.close();

		return stations;
	}
}
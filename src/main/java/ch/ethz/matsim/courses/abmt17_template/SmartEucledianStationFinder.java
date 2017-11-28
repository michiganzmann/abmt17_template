package ch.ethz.matsim.courses.abmt17_template;

import java.util.Collection;

import org.matsim.api.core.v01.network.Link;
import org.matsim.core.utils.geometry.CoordUtils;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import ch.ethz.matsim.av.electric.assets.station.Station;
import ch.ethz.matsim.av.electric.assets.station.StationFinder;
import ch.ethz.matsim.av.electric.assets.vehicle.BatteryVehicle;

@Singleton
public class SmartEucledianStationFinder implements StationFinder {
	final private Collection<Station> stations;
	
	@Inject
	public SmartEucledianStationFinder(Collection<Station> stations) {
		this.stations = stations;
	}
	
	@Override
	public Station findStationForVehicle(BatteryVehicle vehicle, Link link) {
		int count = 5;
		Station[] possibleStation = new Station[count];
		Station selectedStation = null;
		double[] minimumDistance = new double[count];
		for( int i = 0; i < count; i++) {
			minimumDistance[i] = Double.MAX_VALUE;
		}
		
		for (Station station : stations) {
			double distance = CoordUtils.calcEuclideanDistance(station.getLink().getCoord(), link.getCoord());
			
			for ( int i = 0; i < count-1; i--) {
				if( distance < minimumDistance[count-i-1]) {
					minimumDistance[count-i] = minimumDistance[count-i-1];
					possibleStation[count-i] = possibleStation[count-i-1];
					minimumDistance[count-i-1] = distance;
					possibleStation[count-i-1] = station;
				}
			}
			
		}
		
		for ( int i = 0; i < count-1; i++) {
			possibleStation[i].get
		}
		
		return selectedStation;
	}
}

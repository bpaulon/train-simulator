package bcp.imc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.Semaphore;

/**
 * Train simulator. Instance of this class should be executed by a thread.
 * Reports important events to registered observers
 */
public class Train extends Observable implements Runnable {
	
	/** time necessary to load or unload a cargo slot */
	public final static int CARGO_LOAD_UNLOAD_TIME_MILLIS = 1000;
	
	protected Track track;
	protected Station currStation;
	/** The train is configured with a sequence of stations to travel to.*/
	protected List<Station> journey;
	protected Cargo[] internalStorage;
	/** Train identifier. Used mainly for reporting purposes */
	protected String id;
	protected int speed;

	/* Exit condition. Set to true will stop the train. */
	private volatile boolean stop;
	
	public Train(Track track,
			List<Station> journey,
			String desc, 
			int speed, 
			int storageSize) {
		
		if(speed == 0) {
			throw new IllegalArgumentException("0 invalid value for speed");
		}
		this.id = desc;
		
		this.internalStorage = new Cargo[storageSize];
		this.track = track;
		this.speed = speed;
		
		// set journey and starting station
		this.journey = journey;
		this.currStation = journey.get(0);
		stop = false;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void run() {
		while (!stop) {
			travelNextStation();
			unload();
			load();
		}
		System.out.println(this + " STOPPED.");
	}
	
	protected void reportEvent(String eventDesc) {
		setChanged();
		notifyObservers( eventDesc);
	}

	/**
	 * Simulate traveling from currStation to station. The track segment
	 * between the two stations is guarded by a semaphore. A train must acquire
	 * the semaphore before starting the travel. The time spent traveling is 
	 * determined with the formula trackSegmentLength / trainSpeed 
	 * 
	 * @param station - where to travel to
	 */
	protected void travel(Station station) {
		
		Track.TrackSegment trackSegment = 
			track.getTrackSegment(currStation, station);
		Semaphore sem = trackSegment.getSemaphore();

		reportEvent("at " + currStation + " towards " + station 
				+ " waiting for semaphore");
		try {
			sem.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		reportEvent("start travelling at " + speed + " to " + station);
		
		int interval = trackSegment.getLength() / speed;
		sleep(interval * 1000);
		
		sem.release();
		
		currStation = station;
		reportEvent("Train " + id 
				+  " arrived at station: " + currStation.getId());
	}

	/**
	 * Unloads all cargo for current {@link Train#currStation} station.
	 * After each unload operation sleeps for 
	 * {@link Train#CARGO_LOAD_UNLOAD_TIME_MILLIS}
	 */
	protected void unload() {
		reportEvent("start unloading");
		
		// used only for reporting
		ArrayList<String> unloadedCargosDesc = new ArrayList<String>();
		for(int i = 0; i < internalStorage.length; i++ ) {
			Cargo currCargo = internalStorage[i];
			if(currCargo != null  
					&& currCargo.getDestination().equals(currStation.getId())) {
				unloadedCargosDesc.add(currCargo.toString());
				
				//remove from internal storage
				internalStorage[i] = null;
				sleep(CARGO_LOAD_UNLOAD_TIME_MILLIS);
			}
		}
		
		reportEvent("finished unloading " + unloadedCargosDesc 
				+ " - current load " + Arrays.asList(internalStorage));
	}

	/** Loads cargo from {@link Train#currStation} station until 
	 * internal storage is full or there is no more cargo available
	 * at the station. 
	 * After each load operation sleeps for 
	 * {@link Train#CARGO_LOAD_UNLOAD_TIME_MILLIS}
	 */
	protected void load() {
		reportEvent("start loading");
		// reporting only
		ArrayList<String> loadedCargosDesc = new ArrayList<String>();
		for(int i = 0; i < internalStorage.length; i++ ) {
			// find an empty slot
			if(internalStorage[i] == null) {
				Cargo tmp = currStation.unstore();
				
				if(tmp != null) {
					// add to internal storage
					internalStorage[i] = tmp;
					sleep(CARGO_LOAD_UNLOAD_TIME_MILLIS);
					
					loadedCargosDesc.add(tmp.toString());
				} else {
					break;
				}
			}
		}
		reportEvent("finished loading " + loadedCargosDesc 
				+ " - current load " + Arrays.asList(internalStorage));
	}
	
	/**
	 * Finds the next station in the configured journey and then calls 
	 * @link Train#travel(Station). If the current station is the last
	 * one in the list the next station will be the first station 
	 * in the list.
	 */
	protected void travelNextStation() {
		int index = journey.indexOf(currStation);
		index = (index + 1) % journey.size(); 
		
		travel(journey.get(index));
	}

	public String getId() {
		return id;
	}
	
	public String toString() {
		return "train[" + id + "]";
	}
	
	public void interrupt() {
		stop = true;
	}
	
	private void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e){
			e.printStackTrace();
		}		
	}
}

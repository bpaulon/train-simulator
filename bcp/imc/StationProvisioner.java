package bcp.imc;

import java.text.SimpleDateFormat;
import java.util.Observable;

/**
 * Generates cargo and stores them at the relative station.
 * Sender and destination are random and so is the interval between two 
 * generations. 
 * Runs in its own thread
 */
public class StationProvisioner extends Observable implements Runnable {

	final static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss - SS");
	private volatile boolean stop;

	Station[] stations;

	public StationProvisioner(Station[] stations) {
		this.stations = stations;
		stop = false;
	}	

	public void run() {
		reportEvent("Provisioning thread started");

		while (!stop) {
			// sender station
			int srcIndex = genRandomIndex();
			// destination sender
			int destIndex = genRandomIndex();
			// same source and destination ?
			if (destIndex == srcIndex) {
				destIndex = (destIndex + 1) % stations.length;
			}

			Station stationToStoreTo = stations[srcIndex];
			Cargo cargo = new Cargo(stationToStoreTo.getId(),
					stations[destIndex].getId());
			boolean stored = stationToStoreTo.store(cargo);
			if (!stored) {
				reportEvent("Station " + stationToStoreTo + " storage full");
				doSomething(cargo);
			} else {
				reportEvent("stored " + cargo);
			}

			sleep();
		}
		reportEvent("Provisioning thread STOPPED.");
	}

	public void interrupt() {
		stop = true;
	}
	
	int genRandomIndex() {
		return (int) (Math.random() * stations.length);
	}

	protected void reportEvent(String eventDesc) {
		setChanged();
		notifyObservers( eventDesc);
	}

	/**
	 * Method called when unable to store cargo because queue 
	 * was full
	 */
	protected void doSomething(Cargo cago) {
		// NOP
	}

	private void sleep() {
		long interval = (long) (Math.random() * 2000 + 200);
		try {
			Thread.sleep(interval);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}

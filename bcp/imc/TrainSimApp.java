package bcp.imc;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * The main class for the application. Creates the stations and the track. 
 * Starts several threads one for every train {@link Train} and another one 
 * that simulates deliveries from nearby cities to the stations 
 * {@link StationProvisioner}
 */
public class TrainSimApp {

	/* Total number of stations */
	public final static int NR_STATIONS = 8;
	/* Total number of trains */
	public final static int NR_TRAINS = 4;
	/* Default cargo capacity. Each train has a number of slots 
	 * for cargo loading */
	public final static int DEFAULT_CARGO_SLOTS = 13;
	
	/* array which contains all the created threads. Used by the shutdown
	 * hook to exit gracefully */
	ArrayList<Thread> allThreads;
	Train[] trains;
	Station[] stations;

	StationProvisioner spth;
	TrainMonitor monitor;

	public TrainSimApp() {
		allThreads = new ArrayList<Thread>();
		trains = new Train[NR_TRAINS];
		monitor = new TrainMonitor();
	}

	void createStations() {
		stations = new Station[NR_STATIONS];
		for (int i = 0; i < stations.length; i++) {
			stations[i] = new Station("S-" + i);
		}
	}

	/** 
	 * Will create {@link TrainSimApp#NR_TRAINS} with default 
	 * capacity {@link TrainSimApp#DEFAULT_CARGO_SLOTS}
	 */
	void createAndStartTrains() {
		Track track = new Track(stations);

		// create and start trains
		for (int i = 0; i < NR_TRAINS; i++) {
			int speed = genRandomTrainSpeed();
			Train train = new Train(track, Arrays.asList(stations), "T-" + i,
					speed, DEFAULT_CARGO_SLOTS);

			System.out.println("Created train:" + train + " speed:" + speed);
			train.addObserver(monitor);

			trains[i] = train;

			Thread th = new Thread(train);
			th.start();
			allThreads.add(th);
		}
	}

	void createAndStartProvThread() {
		spth = new StationProvisioner(stations);
		spth.addObserver(monitor);
		
		Thread pth = new Thread(spth);
		pth.start();
		allThreads.add(pth);
	}

	int genRandomTrainSpeed() {
		return (int) (Math.random() * 200 + 100);
	}

	void addShutdownHook() {
		ShutdownHook hook = new ShutdownHook();
		Runtime.getRuntime().addShutdownHook(hook);
	}

	public static void main(String[] args) {
		TrainSimApp app = new TrainSimApp();
		app.createStations();
		app.createAndStartTrains();
		app.createAndStartProvThread();

		app.addShutdownHook();
	}

	/*
	 * Gracefully shutdown (not really necessary)
	 */
	class ShutdownHook extends Thread {
		public void run() {
			System.out.println("Shutdown hook called");

			for (int i = 0; i < trains.length; i++) {
				trains[i].interrupt();
			}

			spth.interrupt();

			for (Thread thread : allThreads) {
				try {
					thread.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}

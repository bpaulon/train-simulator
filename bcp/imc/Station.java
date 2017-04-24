package bcp.imc;

import java.util.concurrent.ArrayBlockingQueue;

/** 
 * Represents a train station. Every station has a storage queue.
 * The queue has fixed capacity and synchronized access.
 */
public class Station {
	/** Default storage capacity */
	public static final int DEFAULT_CAPACITY_SIZE = 10;
	/** The storage queue */
	private ArrayBlockingQueue<Cargo> storage;
	/** Station identifier. Source and destination in cargo instances
	 * must match this id */
	private String id; 
	
	public Station(String id) {
		this.id = id;
		storage = new ArrayBlockingQueue<Cargo>(DEFAULT_CAPACITY_SIZE);
	}

	public Station(String id, int storageCapacity) {
		this.id = id;
		storage = new ArrayBlockingQueue<Cargo>(storageCapacity);
	}
	
	public String getId() {
		return this.id;
	}
	
	/** 
	 * Retrieves and removes the first in storage queue, or returns 
	 * null if the queue is empty. Will not block 
	 * @return
	 */
	public Cargo unstore() {
		return storage.poll();
	}
	
	/**
	 * Inserts the specified element at the tail of the queue if it 
	 * is possible to do so immediately without exceeding the queue's capacity, 
	 * returning true upon success and false if the queue is full. 
	 * @param cargo
	 * @return true if the cargo was added to this queue, else false 
	 */
	public boolean store(Cargo cargo) {
		return storage.offer(cargo);
	}
	
	public String toString() {
		return this.id;
	}
}

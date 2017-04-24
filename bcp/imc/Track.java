package bcp.imc;

import java.util.concurrent.Semaphore;

/**
 * Represents very simple CIRCULAR Track. The track is build of track segments. 
 */
public class Track {
	/** Every segment within the track has a length */
	public final static int DEFAULT_SEGMENT_LENGTH = 800;
	Station[] stations;
	TrackSegment[] segms;
	
	/** A track segment has a length and a semaphore. The 
	 * semaphore is used for synchronizing train access to the
	 * track */
	class TrackSegment {
		private int length;
		private Semaphore sem;

		public TrackSegment() {
			this(DEFAULT_SEGMENT_LENGTH);
		}

		public TrackSegment(int length) {
			this.length = length;
			// use fair policy
			this.sem = new Semaphore(1, true);
		}

		public Semaphore getSemaphore() {
			return sem;
		}

		public int getLength() {
			return length;
		}
	}

	public Track(Station[] stations) {
		this.stations = stations;
		segms = new TrackSegment[stations.length];
		for (int i = 0; i < stations.length; i++) {
			segms[i] = new TrackSegment(DEFAULT_SEGMENT_LENGTH);
		}
	}

	/**
	 * Returns the track segment between the two stations s1 and s2
	 * @param s1
	 * @param s2
	 * @return - the track segment
	 * @throws IllegalArgumentException if s1 does not exist
	 */
	public TrackSegment getTrackSegment(Station s1, Station s2) {
		for (int i = 0; i < stations.length; i++) {
			if (s1 == stations[i]) {
				return segms[i];
			}
		}
		throw new IllegalArgumentException("Non existent station");
	}
}

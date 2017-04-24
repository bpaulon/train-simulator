package bcp.imc;

/**
 * A load carried by trains.
 * A cargo has a sender and a destination station.
 */
public class Cargo {
	
	private String destination;
	private String source; 
	
	public Cargo(String source, String destination) {
		this.destination = destination;
		this.source = source;
	}
	
	public String getDestination() {
		return destination;
	}
	
	public String getSource(){
		return source;
	}
	
	public String toString() {
		return "C[" + source + "|" + destination +"]";
	}
}

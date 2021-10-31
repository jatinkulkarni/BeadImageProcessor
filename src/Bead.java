import java.awt.Color;

class Bead implements Comparable<Bead>{
	private Color color;
	public String rgbHex;
	
	public Bead(String rgbHex) {
		color = new Color(Integer.parseInt(rgbHex.substring(0,2), 16),
				Integer.parseInt(rgbHex.substring(2,4),16), Integer.parseInt(rgbHex.substring(4,6),16));
		this.rgbHex = rgbHex;
	}
	
	public boolean equals(Bead otherBead) {
		return rgbHex.equals(otherBead.rgbHex);
	}
	
	public int hashCode() {
		return rgbHex.hashCode();
	}
	
	public int compareTo(Bead otherBead) {
		return rgbHex.compareTo(otherBead.rgbHex);
	}
	
	public String toString() {
		return color.toString();
	}
}

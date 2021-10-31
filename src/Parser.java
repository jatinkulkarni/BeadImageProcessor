import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

import javax.imageio.ImageIO;

import arduino.Arduino;

public class Parser {
	public static void main(String[] args) throws IOException{
		
		
		Scanner kb = new Scanner(System.in);
		welcomeStatement();
		String filePath = kb.nextLine();
		System.out.println();
		BufferedImage beadImage = getImage(filePath);
		repeatStatement(beadImage);
		int numRepeats = kb.nextInt();
		System.out.println();
		
		Bead[] beadsInOrder = getBeads(beadImage, numRepeats);
		Set<Bead> uniqueBeads = new TreeSet<Bead>();
		for(int b=0; b<beadsInOrder.length; b++) {
			uniqueBeads.add(beadsInOrder[b]);
		}

		ArrayList<Bead> currentBucketBeads = getCurrentBucketBeads();
		analyzeBeads(uniqueBeads, currentBucketBeads, kb);
		//Make byte array for Manuel
		byte[] beadBytes = getBeadBytes(currentBucketBeads, beadsInOrder, beadImage);
		System.out.println(Arrays.toString(beadBytes));
		
		System.out.print("What COM do you want to use? ");
		String portDescription = kb.nextLine();
		System.out.println();
		//To be renamed to a much greater name
		Arduino myArduino = new Arduino(portDescription, 9600);
		
		//wait for spacebars to send N beadBytes
		Queue<Byte> qBeadBytes = new LinkedList<>();
		for(int b=1; b<beadBytes.length; b++) {
			qBeadBytes.add(beadBytes[b]);
		}
		
		int totalBeadsSent = 0;
		//Warning: will run forever until you finish!
		while(totalBeadsSent < beadBytes.length - 1) {
			// check input
			System.out.print("Enter to continue or \"quit\" to quit: ");
			String input = kb.nextLine();
			System.out.println("");
			if(input.equals("quit")) {
				break;
			}
			//send bytes
			for(int i=0; i<beadBytes[0]; i++) {
				myArduino.serialWrite((char)(qBeadBytes.remove()+65), 50);
			}
		}
		myArduino.closeConnection();
		System.out.println("Bead Image has been successfully processed.");
		System.out.println("Goodbye.");
	}
	
	// This method prints a welcome statement for the user to imput file path to the image they want us to process into beads
	public static void welcomeStatement() {
		System.out.println("Welcome to Bead Image Processor! (B.I.P. for short)");
		System.out.print("Please enter the file path of bead image: ");
	}
	
	// This method prints a statement that is used to state how large the current bead pattern is and asks the user to see how many times to repeat the pattern
	public static void repeatStatement(BufferedImage beadImage) {
		System.out.println("The file you selected is " + beadImage.getWidth() + "x" + beadImage.getHeight());
		System.out.print("How many times would you like this repeated? ");
	}
	
	// This method gets the image from the filepath and returns a BufferedImage
	public static BufferedImage getImage(String filePath) throws IOException{
		// https://stackoverflow.com/questions/10391778/create-a-bufferedimage-from-file-and-make-it-type-int-argb
		File rawImage = new File(filePath);
		BufferedImage image = ImageIO.read(rawImage);
		/*
		for (int width = 0; width < in.getWidth(); width++) {
			for (int height = in.getHeight()-1; height >= 0; height--) {
				System.out.printf("%d %d pixel info with buffered image: %x%n", width, height, in.getRGB(width,height));
			}
		}
		*/
		
		return image;
	}
	
	// The method returns the beads (pixels) in order from the bottom left going up into an array
	public static Bead[] getBeads(BufferedImage beadImage, int numRepeats) {
		Bead[] orderedBeads = new Bead[beadImage.getWidth() * beadImage.getHeight() * numRepeats];
		int b = 0;
		for (int i = 0; i < numRepeats; i++) {
			for (int width = 0; width < beadImage.getWidth(); width++) {
				for (int height = beadImage.getHeight()-1; height >= 0; height--) {
					String rgbHex = Integer.toHexString(beadImage.getRGB(width,height));
					orderedBeads[b] = new Bead(rgbHex.substring(2));
					b++;
				}
			}
		}
		return orderedBeads;
	}
	
	// This method creates an arraylist of the current beads that in the bucket on the machine that are known
	public static ArrayList<Bead> getCurrentBucketBeads() {
		ArrayList<Bead> bucketBeads = new ArrayList<>();
		Bead whiteBead = new Bead("ffffff");
		Bead redBead = new Bead("ff0000");
		Bead orangeBead = new Bead("ff6a00");
		Bead yellowBead = new Bead("ffd800");
		Bead greenBead = new Bead("009900");
		Bead lightBlue = new Bead("0094ff");
		Bead delphinium = new Bead("0098FF");
		Bead royalBlue = new Bead("0026ff");
		Bead purpleBead = new Bead("b200ff");
		Bead blackBead = new Bead("000000");
		Bead pinkBead = new Bead("ff8492");
		Bead tealBead = new Bead("269f83");
		bucketBeads.add(whiteBead);
		bucketBeads.add(redBead);
		bucketBeads.add(orangeBead);
		bucketBeads.add(yellowBead);
		bucketBeads.add(greenBead);
		bucketBeads.add(lightBlue);
		bucketBeads.add(delphinium);
		bucketBeads.add(royalBlue);
		bucketBeads.add(purpleBead);
		bucketBeads.add(blackBead);
		bucketBeads.add(pinkBead);
		bucketBeads.add(tealBead);
		return bucketBeads;
	}
	
	public static void analyzeBeads(Set<Bead> uniqueBeads, ArrayList<Bead> bucketBeads, Scanner kb) {
		if(uniqueBeads.size() > 20) {
			System.out.println("Slow down there! This here state-of-the-art B.I.P. is only capable of 20 different beads...");
			System.out.println("Stay tuned for B.I.P.P.");
		} else {
			ArrayList<Bead> unknownBeads = new ArrayList<>();
			for(Bead thisBead : uniqueBeads) {
				if(!beadContains(bucketBeads, thisBead)) {
				//if(!bucketBeads.contains(thisBead)) {
					unknownBeads.add(thisBead);
				}
			}
			
			System.out.printf("Your bead image has %d unique beads.%n", uniqueBeads.size());
			System.out.printf("You have %d UNKNOWN bead(s).%n", unknownBeads.size());
			
			if(unknownBeads.size() != 0) {
				//prompt user for new buckets
				for(Bead unknownBead : unknownBeads){
					System.out.printf("In what bucket is %s (uses zero-based indexing): ", unknownBead.rgbHex);
					int newBucket = kb.nextInt();
					System.out.println();
					bucketBeads.remove(newBucket);
					bucketBeads.add(newBucket, unknownBead);
				}
			}
		}
	}
	
	// This method checks if the bead given is in the bucketBead
	public static boolean beadContains(ArrayList<Bead> bucketBeads, Bead weirdBead) {
		for(Bead thisBead : bucketBeads) {
			if(thisBead.rgbHex.equals(weirdBead.rgbHex)) {
				return true;
			}
		}
		return false;
	}
	
	// The method creates an array of bytes that stores the height of the bead and then the which jar the bead comes from
	// Bytes are used since the Arduino has limited memory
	public static byte[] getBeadBytes(ArrayList<Bead> currentBucketBeads, Bead[] beadsInOrder, BufferedImage beadImage) {
		byte[] beadBytes = new byte[beadsInOrder.length + 1];
		beadBytes[0] = (byte)beadImage.getHeight();
		for(int b=0; b<beadsInOrder.length; b++) {
			//beadBytes[b+1] = (byte)currentBucketBeads.indexOf(beadsInOrder[b]);
			beadBytes[b+1] = beadIndexOf(currentBucketBeads, beadsInOrder[b]);
		}
		
		return beadBytes;
	}
	
	public static byte beadIndexOf(ArrayList<Bead> bucketBeads, Bead weirdBead) {
		for(int b=0; b<bucketBeads.size(); b++) {
			if(bucketBeads.get(b).rgbHex.equals(weirdBead.rgbHex)) {
				return (byte)b;
			}
		}
		return -1;
	}
}
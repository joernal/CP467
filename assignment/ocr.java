package assignment;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import javax.imageio.*;
import java.util.List;


public class ocr {
    public static boolean continueIteration = true;
    public static ArrayList<Symbol> syms = new ArrayList<Symbol>();

    public static List<List<Float>> num_data = new ArrayList<List<Float>>();
    
    // This Is to change the Image while the program is running
    public static BufferedImage get_image( BufferedImage img, String name ){	
        try {
	    img = ImageIO.read(new File(name));
        } catch (IOException e){
	    System.out.println("Image not found!");
        }
        return img;
    }

    // Call the main function when the program runs
    public static void main(String []args){

	Scanner reader = new Scanner(System.in); // To read input from keyboard

	BufferedImage img = null; // Empty image var
	BufferedImage img2 = null; // Teach OCR data
	String image = "image2.bmp";

	img = get_image(img, image); 
	img2 = get_image(img2, "numbers.bmp");
	String input = "";
	
	do { 
	    // Menu
	    System.out.println("--- Image Processing and Pattern Recognition ---");
	    System.out.println("0. Change Image");
	    System.out.println("1. Count Symbols and Pixels");
	    System.out.println("2. Convolute image");
	    System.out.println("3. Scale image down");
			System.out.println("4. Edge Detection");
	    System.out.println("5. Thin the image");
	    System.out.println("6. Find feature vectors of the image");
	    System.out.println("7. Classify an number based on feature vector");
	    System.out.println("Q to quit");

	    // Input
	    System.out.print("Select an option: ");
	    input = reader.next();

	    // Change image
	    if (input.equals("0")){
		System.out.println("------------------------------------");
		System.out.print("Enter path to image: ");
		image = reader.next();
		img = get_image(img, image);
		System.out.println("------------------------------------");
		// Number of Symbols and black pixels
	    } else if (input.equals("1")){
		System.out.println("------------------------------------");
		count_symbols(img);
		System.out.println("------------------------------------");
		// Convolute Image
	    } else if (input.equals("2")){
		System.out.println("------------------------------------");
		convolute(img);
		System.out.println("------------------------------------");
		// Scale image
	    } else if (input.equals("3")){
		System.out.println("------------------------------------");
		System.out.print("Enter width: ");
		int inputWidth = reader.nextInt();
		System.out.print("Enter height: ");
		int inputHeight = reader.nextInt();
		System.out.print("Enter threshold: ");
		double threshold = reader.nextDouble();
		scale_down(img, inputWidth, inputHeight, threshold);
		System.out.println("------------------------------------");
	    } else if (input.equals("4")){
		System.out.println("------------------------------------");
		edgeDetection(img);
		System.out.println("------------------------------------");
	    }else if (input.equals("5")){
		System.out.println("------------------------------------");
		BufferedImage img3 = thin(img);
		System.out.println("------------------------------------");
		// Thin Image
	    }else if (input.equals("6")){
		System.out.println("------------------------------------");
		featureVector(img2);
		System.out.println("------------------------------------");
		// Find feature vectors of the image
	    } else if (input.equals("7")){
		System.out.println("------------------------------------");
		featureVector2(img);
		System.out.println("------------------------------------");
	    }
	} while (!input.equals("Q"));
    }

    // A funtion to merge symobls
    public static ArrayList<Symbol> merge( ArrayList<Symbol> syms, int i, int j, Pixel pix){
	for (int k = 0; k<syms.get(i).listOfPixels.size(); k++){
	    syms.get(j).listOfPixels.add(syms.get(i).listOfPixels.get(k));
	}
	syms.get(j).listOfPixels.add(pix);
	syms.remove(i);
	return syms;
    }

    // To create symbols in image
    public static void count_symbols( BufferedImage img ){

	syms.clear();
	
	int counter = 0;

	int merge1index = -1;
	int merge2index = -1;
	
	boolean value = false;
	
	if (img.getWidth() > 0){
	    int grayscale = 0;
	    // For every Column
	    for (int xPixel = 0; xPixel < img.getWidth(); xPixel++){
		// For every Row
		for (int yPixel = 0 ; yPixel < img.getHeight(); yPixel++){
		    int color = img.getRGB(xPixel, yPixel); // Get Color
		    if (color==Color.BLACK.getRGB()){	    // If color is Black
			counter++;			    // Incremeant the number of black pixels
			Pixel pix = new Pixel(xPixel, yPixel, grayscale, false); // create Pixel Object
			if (syms.size() != 0){ // If we have more than 1 symbol
			    outerloop:	       // Label for breaking out of loop
			    for (int i = 0; i < syms.size(); i++){ // Iterate through the symbols
          			if (syms.get(i).searchSymbol(pix) == true){ // If Pixel has a neighbour in this symbol
				    merge1index = i; // If we have to merge keep this index
				    for (int j = syms.size()-1; j >= 0; j--){ // Check if neighbour exists in a different symbol
					if (syms.get(j).searchSymbol(pix) == true){ // If it does
					    value = true; // Tell Program not to create new Symbol
					    if (i != j){ // Make sure this symbol isn't the same symbol
						merge2index = j; // To merge i with j
						syms = merge(syms, i, j, pix); // Meging two symbols
						merge1index = merge2index = -1; // reset merge indicies
						break outerloop; // Break out of loop
					    } else {		 // If i and j are the same
						syms.get(i).addPixel(pix); // Just add the pixel to Symbol i
						break outerloop; // leave loop
					    }
					}
				    }
          			} else { // If no neighbours 
				    value = false; // Tell Program to create new Symbol 
          			}
			    }
			    if (value != true){ // Check if we have to create new symbo
          			Symbol s = new Symbol( pix ); // Create new Symbol
          			syms.add(s);		      //  Add Symbol to list of Symbols
			    }
			// If no symbols exist
			} else {
			    Symbol s = new Symbol(pix); // create new symbol
			    syms.add(s);		// Add symbol to symbol list
			}
		    }
		}
	    }

	    // The total black pixels in the entire Image
	    System.out.println("Total number of Black Pixels: " + counter);

	    // Go through all the symbols created
	    for (int k = 0; k < syms.size(); k++){
		// Print the number of pixels for each symbol
		System.out.println("Number of black pixels in symbol " + k + " is " + syms.get(k).listOfPixels.size());
	    }
	} else {
	    System.out.println("No Image!");
	}
    }

    // To Scale Down 
    public static void scale_down( BufferedImage img, int inputWidth, int inputHeight, double threshold ){
	
	BufferedImage newImage = new BufferedImage(inputWidth,inputHeight,BufferedImage.TYPE_INT_RGB);
	
	double colScaleRatio = (double) img.getWidth()/newImage.getWidth();
	double rowScaleRatio = (double) img.getHeight()/newImage.getHeight();
	
	int colWindow = (int) Math.round(colScaleRatio);
	int rowWindow = (int) Math.round(rowScaleRatio);
	
	int[] pixels = new int[colWindow*rowWindow];
	double graylevel;
	double sum;
	for (int i = 0; i < newImage.getWidth(); i++){
	    for (int j = 0; j < newImage.getHeight(); j++){
		pixels = img.getRGB(((int)(i*colScaleRatio)), ((int)(j*rowScaleRatio)), colWindow, rowWindow, null, 0, colWindow);
		sum = 0;
		for (int p: pixels){
		    int r = (p >> 16) & 0xFF;
		    int g = (p >> 8) & 0xFF;
		    int b = (p & 0xFF);
		    
		    int gray = (r + g + b) / 3; // get gray value
		    System.out.print(gray+" ");
		    
		    sum=sum+gray;
		}
		graylevel = ((sum/pixels.length));
		if (graylevel > threshold){
		    graylevel=graylevel;
		    newImage.setRGB(i,j,(int)graylevel*65536+(int)graylevel*256+(int)graylevel);
		}
		System.out.println(sum+"/"+pixels.length+"="+graylevel);
	    }
	}
	try{
	    File outputfile = new File("scaled.bmp");
	    ImageIO.write(newImage, "bmp", outputfile);
	    Desktop.getDesktop().open(outputfile);
	    System.out.println("Scaled down image saved as scaled.bmp");
	} catch (IOException e) {
	    System.out.println("Image not saved!");
	}
    }
    
    public static BufferedImage thin(BufferedImage img){
    	// Apply ZS algorithm
    	int padding = 1;
    	BufferedImage newImage = new BufferedImage(img.getWidth()+2*padding, img.getHeight()+2*padding, img.getType());
    	// add padding around image
    	Graphics g1 = newImage.getGraphics();
	
    	g1.setColor(Color.white);
    	g1.fillRect(0,0,img.getWidth()+2*padding,img.getHeight()+2*padding);
    	g1.drawImage(img, padding, padding, null);
    	g1.dispose();
	
    	int rgb;
    	List<Pixel> pixels = new ArrayList<Pixel>();
    	for (int yPixel = 0; yPixel < newImage.getHeight(); yPixel++){
	    
    	    for (int xPixel = 0 ; xPixel < newImage.getWidth(); xPixel++){
    		rgb = newImage.getRGB(xPixel, yPixel);
		
    		int r = (rgb >> 16) & 0xFF;
    		int g = (rgb >> 8) & 0xFF;
    		int b = (rgb & 0xFF);
		
    		int gray = (r + g + b) / 3; // get gray value
		
    		pixels.add(new Pixel(xPixel, yPixel, gray, false));
		
    	    }
    	}
	

    	while(continueIteration == true){
	    int iteration = 1;
	    pixels = ZSRules(pixels, iteration, img, newImage);
	    pixels = delete(pixels, img, newImage);
	    iteration = 2;
	    pixels = ZSRules(pixels, iteration, img, newImage);
	    pixels = delete(pixels, img, newImage);
    	}
	
    	for (int y = 0; y < newImage.getHeight(); y++){
    	    for (int x = 0 ; x < newImage.getWidth(); x++){
    		newImage.setRGB(x, y, ((int) (pixels.get(newImage.getWidth()*y + x).grayscale) * 0x00010101) );	
    	    }
    	}
	
    	try {
    	    File outputfile = new File("thinned.bmp");
    	    ImageIO.write(newImage, "bmp", outputfile);
    	    Desktop.getDesktop().open(outputfile);
    	    System.out.println("Thinned image saved as thinned.bmp");
    	} catch (IOException e) {
    	    System.out.println("Image not saved!");
    	}

	return newImage;
    }
    
    public static List<Pixel> delete(List<Pixel> pixels, BufferedImage img, BufferedImage newImage){
	// Delete
	
    	continueIteration = false;
	
	for(int y = 1; y <= newImage.getHeight()-1; y++){
	    for(int x = 1; x <= newImage.getWidth()-1; x++){
		if (pixels.get(newImage.getWidth()*y + x).delete){
		    pixels.get(newImage.getWidth()*y + x).grayscale = 255;
		    
		    continueIteration = true;
		    pixels.set(newImage.getWidth()*y + x, new Pixel(pixels.get(newImage.getWidth()*y + x).x, pixels.get(newImage.getWidth()*y + x).y, 255, false));

		}
	    }
	}
	
	return pixels;
    }
    
    public static List<Pixel> ZSRules(List<Pixel> pixels, int iteration, BufferedImage img, BufferedImage newImage){
	
	// Iteration 1
	for(int y = 1; y < newImage.getHeight()-1; y++){
	    for(int x = 1; x < newImage.getWidth()-1; x++){
		int j = newImage.getWidth()*y;
		int p1 = pixels.get(j + x).grayscale;
		int p2 = pixels.get(j + x - newImage.getWidth()).grayscale;
		int p3 = pixels.get(j + x - newImage.getWidth()+1).grayscale;
		int p4 = pixels.get(j + x + 1).grayscale;
		int p5 = pixels.get(j + x + 1 + newImage.getWidth()).grayscale;
		int p6 = pixels.get(j + x + newImage.getWidth()).grayscale;
		int p7 = pixels.get(j + x + newImage.getWidth()-1).grayscale;
		int p8 = pixels.get(j + x - 1).grayscale;
		int p9 = pixels.get(j + x - newImage.getWidth() - 1).grayscale;
		
		int[] grays = {p2,p3,p4,p5,p6,p7,p8,p9};
		if(p1 < 255){
		    int blackCounter = 0;
		    int zeroOneCounter = 0;
		    for (int i = 0; i < grays.length; i++){
			if (grays[i] < 255){
			    blackCounter++;
			}
			if (i != 7 && grays[i]==255 && grays[i+1]<255){
			    zeroOneCounter++;
			}
		    }
		    if (grays[7]==255 && grays[0] <255){
			zeroOneCounter++;
		    }
		    
		    if (iteration == 1){
			if((blackCounter >= 2 && blackCounter <= 6)
			   &&(p2 == 255 || p4 == 255 || p6 == 255)
			   &&(p4 == 255 || p6 == 255 || p8 == 255)
			   &&(zeroOneCounter == 1)){
			    pixels.get(newImage.getWidth()*y + x).delete = true;
			}
		    }
		    // Iteration 2
		    if (iteration == 2){
			if((blackCounter >= 2 && blackCounter <= 6)
			   &&(p2 == 255 || p4 == 255 || p8 == 255)
			   &&(p2 == 255 || p6 == 255 || p8 == 255)
			   &&(zeroOneCounter == 1)){
			    pixels.get(newImage.getWidth()*y + x).delete = true;
			}
		    }
		}
	    }
	}
    	return pixels;
    }
    
    public static void convolute( BufferedImage img ){
	
	int padding = 2;
	
	List<Pixel> pixels = new ArrayList<Pixel>();
	
	BufferedImage newImage = new BufferedImage(img.getWidth()+2*padding, img.getHeight()+2*padding, img.getType());
	// add padding around image
	Graphics g1 = newImage.getGraphics();
	
	g1.setColor(Color.white);
	g1.fillRect(0,0,img.getWidth()+2*padding,img.getHeight()+2*padding);
	g1.drawImage(img, padding, padding, null);
	g1.dispose();

	int rgb;
	double[][] pixData = new double[newImage.getWidth()][newImage.getHeight()];
	for (int yPixel = 0; yPixel < newImage.getHeight(); yPixel++){
	    for (int xPixel = 0 ; xPixel < newImage.getWidth(); xPixel++){
		rgb = newImage.getRGB(xPixel, yPixel);
		
		int r = (rgb >> 16) & 0xFF;
		int g = (rgb >> 8) & 0xFF;
		int b = (rgb & 0xFF);
		
		int gray = (r + g + b) / 3; // get gray value
		
		pixData[xPixel][yPixel] = gray;
	    }
	}
	
	for (int y = 0; y < newImage.getHeight(); y++){
	    for (int x=0; x < newImage.getWidth()-4; x++){
		pixData[x+2][y] = 0.2*pixData[x][y] +
		    0.2*pixData[x+1][y] +
		    0.2*pixData[x+2][y] +
		    0.2*pixData[x+3][y] +
		    0.2*pixData[x+4][y];
		newImage.setRGB(x, y, ((int) (pixData[x+2][y]) * 0x00010101) );
	    }
	}
	
	for (int x=0; x < newImage.getWidth(); x++){
	    for (int y = 0; y < newImage.getHeight()-4; y++){
		pixData[x][y+2] = 0.2*pixData[x][y] +
		    0.2*pixData[x][y+1] +
		    0.2*pixData[x][y+2] +
		    0.2*pixData[x][y+3] +
		    0.2*pixData[x][y+4];
		newImage.setRGB(x, y, ((int) (pixData[x][y+2]) * 0x00010101) );
	    }
	}
	
	newImage = newImage.getSubimage(2, 2, img.getWidth(), img.getHeight());
	
	try {
	    File outputfile = new File("convoluted.bmp");
	    ImageIO.write(newImage, "bmp", outputfile);
	    Desktop.getDesktop().open(outputfile);
	    System.out.println("Convoluted image saved as convoluted.bmp");
	} catch (IOException e) {
	    System.out.println("Image not saved!");
	}
    }
    
    // EDGE DETECTION METHOD
    public static void edgeDetection( BufferedImage img ){
	
	int padding = 1;
	
	List<Pixel> pixels = new ArrayList<Pixel>();
	
	BufferedImage newImage = new BufferedImage(img.getWidth()+2*padding, img.getHeight()+2*padding, img.getType());
	// add padding around image
	Graphics g1 = newImage.getGraphics();
	
	g1.setColor(Color.white);
	g1.fillRect(0,0,img.getWidth()+2*padding,img.getHeight()+2*padding);
	g1.drawImage(img, padding, padding, null);
	g1.dispose();
	
	
	int rgb;
	double[][] pixData = new double[newImage.getWidth()][newImage.getHeight()];
	for (int yPixel = 0; yPixel < newImage.getHeight(); yPixel++){
	    
	    for (int xPixel = 0 ; xPixel < newImage.getWidth(); xPixel++){
		rgb = newImage.getRGB(xPixel, yPixel);
		
		int r = (rgb >> 16) & 0xFF;
		int g = (rgb >> 8) & 0xFF;
		int b = (rgb & 0xFF);
		
		int gray = (r + g + b) / 3; // get gray value
		
		pixData[xPixel][yPixel] = gray;
	    }
	}
	
	for (int y = 0; y < newImage.getHeight(); y++){
	    for (int x=0; x < newImage.getWidth()-2; x++){
		pixData[x][y] = (-1)*pixData[x][y] +
		    0*pixData[x+1][y] +
		    1*pixData[x+2][y];
		newImage.setRGB(x, y, ((int) (pixData[x+1][y]) * 0x00010101) );
	    }
	}
	
	for (int x=0; x < newImage.getWidth(); x++){
	    for (int y = 0; y < newImage.getHeight()-2; y++){
		pixData[x][y] = (-1)*pixData[x][y] +
		    0*pixData[x][y+1] +
		    1*pixData[x][y+2];
		newImage.setRGB(x, y, ((int) (pixData[x][y+1]) * 0x00010101) );
	    }
	}
	newImage = newImage.getSubimage(1, 1, img.getWidth(), img.getHeight());
	
	
	try {
	    File outputfile = new File("edge_detection.bmp");
	    ImageIO.write(newImage, "bmp", outputfile);
	    Desktop.getDesktop().open(outputfile);
	    System.out.println("Edge detected image saved as edge_detection.bmp");
	} catch (IOException e) {
	    System.out.println("Image not saved!");
	}
    }

    public static void featureVector( BufferedImage img ){
    	count_symbols(img);
    	int q1Black,q1White,q2Black,q2White,q3Black,q3White,q4Black,q4White,q5Black,q5White
    	,q6Black,q6White,q7Black,q7White,q8Black,q8White,q9Black,q9White;
    	q1White = q2White = q3White = q4White = q5White = q6White = q7White = q8White =q9White
	    = q1Black =q2Black =q3Black =q4Black =q5Black =q6Black =q7Black =q8Black =q9Black = 0;
	
    	for (int i=0; i<syms.size(); i++){
	    List<Float> featureVector = new ArrayList<Float>();
	    int left = syms.get(i).getExtremeLeft();
	    int right = syms.get(i).getExtremeRight();
	    int top = syms.get(i).getExtremeTop();
	    int bottom = syms.get(i).getExtremeBottom();
	    
	    double qWidth = Math.floor((right - left)/3);
	    double qHeight = Math.floor((bottom-top)/3);
	    int qArea = (int)qWidth*(int)qHeight;
	    
	    // Quandrant 1 - top left section of 9 sectioned symbol
	    double q1x = qWidth+left;
	    double q1y = qHeight+top;
	    
	    // Quandrant 2 - top middle section of 9 sectioned symbol
	    double q2Leftx = q1x;
	    double q2Rightx = q1x+qWidth;
	    
	    // Quandrant 4 - left section of 9 sectioned symbol
	    double q4Bottomy = q1y+qHeight;
	    
	    for (int j = 0; j < syms.get(i).listOfPixels.size(); j++){
		int pixx = syms.get(i).listOfPixels.get(j).x;
		int pixy = syms.get(i).listOfPixels.get(j).y;
		//Q1
		if ((pixx > left && pixx < q1x) && (pixy > top && pixy < q1y)){
		    q1Black++;
		}
		//Q2
		if ((pixx > q2Leftx && pixx < q2Rightx) && (pixy < q1y && pixy > top)){
		    q2Black++;
		}
		//Q3
		if ((pixx > q2Rightx && pixx < right) && (pixy < q1y && pixy > top)){
		    q3Black++;
		}
    			//Q4
		if ((pixx > left && pixx < q1x) && (pixy < q4Bottomy && pixy > q1y)){
		    q4Black++;
		}
    			//Q5
		if ((pixx > q2Leftx && pixx < q2Rightx) && (pixy < q4Bottomy && pixy > q1y)){
		    q5Black++;
    			}
		//Q6
		if ((pixx > q2Rightx && pixx < right) && (pixy < q4Bottomy && pixy > q1y)){
		    q6Black++;
		}
		//Q7
		if ((pixx > left && pixx < q2Leftx) && (pixy < bottom && pixy > q4Bottomy)){
		    q7Black++;
		}
		//Q8
		if ((pixx > q2Leftx && pixx < q2Rightx) && (pixy > q4Bottomy && pixy < bottom)){
		    q8Black++;
		}
		//Q9
		if ((pixx > q2Rightx && pixx < right) && (pixy > q4Bottomy && pixy < bottom)){
		    q9Black++;
		}
	    }
	    //Q1
	    featureVector.add((float)q1Black/(float)qArea);
	    //Q2
	    featureVector.add((float)q2Black/(float)qArea);
	    //Q3
	    featureVector.add((float)q3Black/(float)qArea);
	    //Q4
	    featureVector.add((float)q4Black/(float)qArea);
	    //Q5
	    featureVector.add((float)q5Black/(float)qArea);
	    //Q6
	    featureVector.add((float)q6Black/(float)qArea);
	    //Q7
	    featureVector.add((float)q7Black/(float)qArea);
	    //Q8
	    featureVector.add((float)q8Black/(float)qArea);
	    //Q9
	    featureVector.add((float)q9Black/(float)qArea);
	    
	    System.out.println(Arrays.toString(featureVector.toArray()));
	    
	    qArea = q1White = q2White = q3White = q4White = q5White = q6White = q7White = q8White =q9White
		= q1Black =q2Black =q3Black =q4Black =q5Black =q6Black =q7Black =q8Black =q9Black = 0;
	    
	    
	    // Running this method with computer text versions of the numbers 0-9 yields the following resultant feature vectors (respectively)
	    /*
	      [0.3094099, 0.7033493, 0.0, 0.0, 0.61244017, 0.0, 0.28548643, 0.72727275, 0.28548643]
	      [0.3121212, 0.34848484, 0.55454546, 0.0, 0.25, 0.3969697, 0.5863636, 0.4969697, 0.28787878]
	      [0.27058825, 0.30147058, 0.5588235, 0.15294118, 0.3867647, 0.51911765, 0.31764707, 0.29411766, 0.59117645]
	      [0.0065876152, 0.42687747, 0.42160738, 0.38735178, 0.052700922, 0.42160738, 0.31752306, 0.3188406, 0.6350461]
	      [0.56666666, 0.31666666, 0.23939393, 0.33333334, 0.33030304, 0.48333332, 0.3151515, 0.30454546, 0.56969696]
	      [0.41036415, 0.3067227, 0.2507003, 0.6442577, 0.30252102, 0.49019608, 0.52380955, 0.29691878, 0.51960784]
	      [0.31746033, 0.31746033, 0.6926407, 0.0, 0.36507937, 0.1904762, 0.14718615, 0.45021644, 0.0]
	      [0.4973262, 0.26604277, 0.51336896, 0.42780748, 0.46122995, 0.4331551, 0.54278076, 0.2606952, 0.52272725]
	      [0.5154062, 0.29411766, 0.53361344, 0.47619048, 0.30532214, 0.6554622, 0.2647059, 0.30532214, 0.41736695]
	      [0.45721924, 0.29545453, 0.53475934, 0.5294118, 0.0, 0.5294118, 0.5, 0.29946524, 0.4973262]
	    */
	    
	    // We can now write the numbers 0-9 by hand and see how well it matches up with the computer text versions
	    
	    /*
	      [0.03809524, 0.22328043, 0.0, 0.0, 0.14497355, 0.0, 0.11957672, 0.23068783, 0.11851852]
	      [0.120089784, 0.16498317, 0.023569023, 0.0, 0.024691358, 0.112233445, 0.08417509, 0.26823795, 0.12345679]
	      [0.1125731, 0.100877196, 0.19005848, 0.0, 0.17690058, 0.21783626, 0.03362573, 0.09210526, 0.24561404]
	      [0.03809524, 0.16984127, 0.24444444, 0.23174603, 0.11111111, 0.30952382, 0.0, 0.0, 0.22857143]
	      [0.2462585, 0.10612245, 0.03537415, 0.14285715, 0.11292517, 0.14829932, 0.084353745, 0.11292517, 0.24081632]
	      [0.046616543, 0.21954887, 0.019548872, 0.20902255, 0.07218045, 0.019548872, 0.2556391, 0.15488721, 0.3112782]
	      [0.09851552, 0.09851552, 0.27260458, 0.0, 0.0, 0.21187584, 0.0, 0.0, 0.2240216]
	      [0.2550505, 0.21717171, 0.0, 0.11742424, 0.30176768, 0.08207071, 0.14393939, 0.10858586, 0.24747474]
	      [0.21040975, 0.15393133, 0.13953489, 0.11738649, 0.15393133, 0.19379845, 0.0, 0.0, 0.1915836]
	      [0.30402932, 0.12820514, 0.21062271, 0.22344323, 0.0, 0.23076923, 0.22893773, 0.11904762, 0.31868133]
	    */
	    
	    // By subtracting these two feature vectors, we may obtain a margin of error for each number
	    
	    // Now we should be able to compare the feature vectors of hand written numbers/letters with the computer generated numbers/letters and guess which number the feature vector represents!

	    num_data.add(featureVector);

    	}
    }

    public static void featureVector2( BufferedImage img ){
	count_symbols(img);
    	int q1Black,q1White,q2Black,q2White,q3Black,q3White,q4Black,q4White,q5Black,q5White
	    ,q6Black,q6White,q7Black,q7White,q8Black,q8White,q9Black,q9White;
	
    	q1White = q2White = q3White = q4White = q5White = q6White = q7White = q8White =q9White
	    = q1Black =q2Black =q3Black =q4Black =q5Black =q6Black =q7Black =q8Black =q9Black = 0;
	
    	for (int i=0; i<syms.size(); i++){
	    List<Float> featureVector = new ArrayList<Float>();
	    int left = syms.get(i).getExtremeLeft();
	    int right = syms.get(i).getExtremeRight();
	    int top = syms.get(i).getExtremeTop();
	    int bottom = syms.get(i).getExtremeBottom();
	    
	    double qWidth = Math.floor((right - left)/3);
	    double qHeight = Math.floor((bottom-top)/3);
	    int qArea = (int)qWidth*(int)qHeight;
	    
	    // Quandrant 1 - top left section of 9 sectioned symbol
	    double q1x = qWidth+left;
	    double q1y = qHeight+top;
	    
	    // Quandrant 2 - top middle section of 9 sectioned symbol
	    double q2Leftx = q1x;
	    double q2Rightx = q1x+qWidth;
	    
	    // Quandrant 4 - left section of 9 sectioned symbol
	    double q4Bottomy = q1y+qHeight;
	    
	    for (int j = 0; j < syms.get(i).listOfPixels.size(); j++){
		int pixx = syms.get(i).listOfPixels.get(j).x;
		int pixy = syms.get(i).listOfPixels.get(j).y;
		//Q1
		if ((pixx > left && pixx < q1x) && (pixy > top && pixy < q1y)){
		    q1Black++;
		}
		//Q2
		if ((pixx > q2Leftx && pixx < q2Rightx) && (pixy < q1y && pixy > top)){
		    q2Black++;
		}
		//Q3
		if ((pixx > q2Rightx && pixx < right) && (pixy < q1y && pixy > top)){
		    q3Black++;
		}
    			//Q4
		if ((pixx > left && pixx < q1x) && (pixy < q4Bottomy && pixy > q1y)){
		    q4Black++;
		}
    			//Q5
		if ((pixx > q2Leftx && pixx < q2Rightx) && (pixy < q4Bottomy && pixy > q1y)){
		    q5Black++;
    			}
		//Q6
		if ((pixx > q2Rightx && pixx < right) && (pixy < q4Bottomy && pixy > q1y)){
		    q6Black++;
		}
		//Q7
		if ((pixx > left && pixx < q2Leftx) && (pixy < bottom && pixy > q4Bottomy)){
		    q7Black++;
		}
		//Q8
		if ((pixx > q2Leftx && pixx < q2Rightx) && (pixy > q4Bottomy && pixy < bottom)){
		    q8Black++;
		}
		//Q9
		if ((pixx > q2Rightx && pixx < right) && (pixy > q4Bottomy && pixy < bottom)){
		    q9Black++;
		}
	    }
	    //Q1
	    featureVector.add((float)q1Black/(float)qArea);
	    //Q2
	    featureVector.add((float)q2Black/(float)qArea);
	    //Q3
	    featureVector.add((float)q3Black/(float)qArea);
	    //Q4
	    featureVector.add((float)q4Black/(float)qArea);
	    //Q5
	    featureVector.add((float)q5Black/(float)qArea);
	    //Q6
	    featureVector.add((float)q6Black/(float)qArea);
	    //Q7
	    featureVector.add((float)q7Black/(float)qArea);
	    //Q8
	    featureVector.add((float)q8Black/(float)qArea);
	    //Q9
	    featureVector.add((float)q9Black/(float)qArea);
	    
	    qArea = q1White = q2White = q3White = q4White = q5White = q6White = q7White = q8White =q9White
		= q1Black =q2Black =q3Black =q4Black =q5Black =q6Black =q7Black =q8Black =q9Black = 0;
	    
	    double min_val = 1;
	    int min_pos = 0;
	    for (int k = 0; k < num_data.size(); k++){
		double total_distance = 0;
		for (int l = 0; l < featureVector.size(); l++){
		    double distance = Math.pow((num_data.get(k).get(l)-featureVector.get(l)),2);
		    total_distance += distance;
		}
		
		total_distance = Math.sqrt(total_distance);
		if (total_distance < min_val){
		    min_val = total_distance;
		    min_pos = k;
		}
	    }
	    System.out.println("This is " + min_pos );
    	}
    }    
}

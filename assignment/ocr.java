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
    public static BufferedImage get_image( BufferedImage img, String name ){
	
        try {
	    img = ImageIO.read(new File(name));
        } catch (IOException e){
	    System.out.println("Image not found!");
        }
	
        return img;
    }
    
    public static void main(String []args){
	
	Scanner reader = new Scanner(System.in);
	
	BufferedImage img = null;
	String image = "image2.bmp";
	
	img = get_image(img, image);
	
	String input = "";
	
	do {
	    // Menu
	    System.out.println("--- Image Processing and Pattern Recognition ---");
	    System.out.println("0. Change Image");
	    System.out.println("1. Count Symbols and Pixels");
	    System.out.println("2. Convolute image");
	    System.out.println("3. Scale image down");
	    System.out.println("5. Thin the image");
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
	    }else if (input.equals("5")){
		System.out.println("------------------------------------");
		thin(img);
		System.out.println("------------------------------------");
		// Thin Image
		}
	} while (!input.equals("Q"));
    }
    
    public static ArrayList<Symbol> merge( ArrayList<Symbol> syms, int i, int j, Pixel pix){
	for (int k = 0; k<syms.get(i).listOfPixels.size(); k++){
	    syms.get(j).listOfPixels.add(syms.get(i).listOfPixels.get(k));
	}
	syms.get(j).listOfPixels.add(pix);
	syms.remove(i);
	return syms;
    }
    
    public static void count_symbols( BufferedImage img ){
	
	ArrayList<Symbol> syms = new ArrayList<Symbol>();
	
	int counter = 0;
	
	int merge1index = -1;
	int merge2index = -1;
	
	boolean value = false;
	
	if (img.getWidth() > 0){
	    int grayscale = 0;
	    for (int xPixel = 0; xPixel < img.getWidth(); xPixel++){
		for (int yPixel = 0 ; yPixel < img.getHeight(); yPixel++){
		    int color = img.getRGB(xPixel, yPixel);
		    if (color==Color.BLACK.getRGB()){
			counter++;
			Pixel pix = new Pixel(xPixel, yPixel, grayscale, false);
			if (syms.size() != 0){
			    outerloop:
			    for (int i = 0; i < syms.size(); i++){
          			if (syms.get(i).searchSymbol(pix)==true){
				    merge1index = i;
				    for (int j = syms.size()-1; j >= 0; j--){
					if (syms.get(j).searchSymbol(pix)==true){
					    value = true;
					    if(i!=j){
						merge2index=j;
						syms=merge(syms, i, j, pix);
						merge1index = merge2index = -1;
						break outerloop;
					    }else{
						syms.get(i).addPixel(pix);
						break outerloop;
					    }
					    
					}
				    }
				    
          			} else {
				    value = false;
          			}
				
			    }
			    
			    if (value != true){
          			Symbol s = new Symbol( pix );
          			syms.add(s);
				
			    }
			    
			} else {
			    Symbol s = new Symbol(pix);
			    syms.add(s);
			}
			
			
		    }
		    
		}
		
	    }
	    System.out.println("Total number of Black Pixels: " + counter);
	    
	    for (int k = 0; k < syms.size(); k++){
		System.out.println("Number of black pixels in symbol " + k + " is " + syms.get(k).listOfPixels.size());
	    }
	    
	} else {
	    System.out.println("No Image!");
	}
	
    }
    
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
    
    public static void thin(BufferedImage img){
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

}

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
	    System.out.println("6. Find feature vectors of the image");
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
		}else if (input.equals("6")){
		System.out.println("------------------------------------");
		featureVector(img);
		System.out.println("------------------------------------");
		// Find feature vectors of the image
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
    		q1White = qArea - q1Black;
    		featureVector.add((float)q1Black/(float)q1White);
    		//Q2
    		q2White = qArea - q2Black;
    		featureVector.add((float)q2Black/(float)q2White);
    		//Q3
    		q3White = qArea - q3Black;
    		featureVector.add((float)q3Black/(float)q3White);
    		//Q4
    		q4White = qArea - q4Black;
    		featureVector.add((float)q4Black/(float)q4White);
    		//Q5
    		q5White = qArea - q5Black;
    		featureVector.add((float)q5Black/(float)q5White);
    		//Q6
    		q6White = qArea - q6Black;
    		featureVector.add((float)q6Black/(float)q6White);
    		//Q7
    		q7White = qArea - q7Black;
    		featureVector.add((float)q7Black/(float)q7White);
    		//Q8
    		q8White = qArea - q8Black;
    		featureVector.add((float)q8Black/(float)q8White);
    		//Q9
    		q9White = qArea - q9Black;
    		featureVector.add((float)q9Black/(float)q9White);
    		
    		System.out.println(Arrays.toString(featureVector.toArray()));
    		
    		qArea = q1White = q2White = q3White = q4White = q5White = q6White = q7White = q8White =q9White 
    				= q1Black =q2Black =q3Black =q4Black =q5Black =q6Black =q7Black =q8Black =q9Black = 0;
    		
    		
    		// Running this method with computer text versions of the numbers 0-9 yields the following resultant feature vectors (respectively)
    		/*
			[0.84236455, 0.41935483, 1.1494253, 1.125, 0.0, 1.125, 1.0, 0.4274809, 0.9893617]
    		[0.44803694, 2.3709676, 0.0, 0.0, 1.5802469, 0.0, 0.39955357, 2.6666667, 0.39955357]
			[0.4537445, 0.53488374, 1.244898, 0.0, 0.33333334, 0.65829146, 1.4175824, 0.9879518, 0.40425533]
			[0.37096775, 0.43157893, 1.2666667, 0.18055555, 0.63069546, 1.0795107, 0.46551725, 0.41666666, 1.4460431]
			[0.0066313, 0.74482757, 0.7289294, 0.63225806, 0.055632822, 0.7289294, 0.46525097, 0.4680851, 1.7400723]
			[1.3076923, 0.46341464, 0.31474105, 0.5, 0.49321267, 0.9354839, 0.460177, 0.4379085, 1.3239436]
			[0.695962, 0.44242424, 0.33457944, 1.8110236, 0.43373495, 0.96153843, 1.1, 0.42231077, 1.0816326]
			[0.4651163, 0.4651163, 2.2535212, 0.0, 0.575, 0.23529412, 0.17258883, 0.81889766, 0.0]
			[0.9893617, 0.36247724, 1.0549451, 0.74766356, 0.8560794, 0.7641509, 1.1871345, 0.35262206, 1.0952381]
			[1.0635839, 0.41666666, 1.1441442, 0.90909094, 0.43951613, 1.902439, 0.36, 0.43951613, 0.71634614]
    		 */
    		
    		// We can now write the numbers 0-9 by hand and see how well it matches up with the computer text versions
    		
    		/*
    		[0.23411979, 0.16438356, 0.18881118, 0.24542125, 0.0, 0.21863799, 0.30518234, 0.09149278, 0.30268198]
    		[0.17475729, 0.59911895, 0.0, 0.0, 0.54468083, 0.0, 0.12037037, 0.7794118, 0.09009009]
			[0.09874327, 0.10669078, 0.2413793, 0.0, 0.0016366612, 0.28301886, 0.15689981, 0.40689656, 0.14179105]
			[0.10683761, 0.1285403, 0.2853598, 0.057142857, 0.26960784, 0.39247313, 0.05498982, 0.121212125, 0.3631579]
			[0.15677321, 0.3893967, 0.0, 0.35957065, 0.26245847, 0.1764706, 0.0, 0.022880215, 0.19496855]
			[0.35969868, 0.11764706, 0.047895502, 0.19536424, 0.11591963, 0.19536424, 0.079222724, 0.09228442, 0.2778761]
			[0.24951644, 0.1313485, 0.09677419, 0.31836733, 0.1393298, 0.07666667, 0.3374741, 0.09677419, 0.35146442]
			[0.086419754, 0.086419754, 0.3702422, 0.0, 0.036649216, 0.15789473, 0.008917198, 0.26923078, 0.0]
			[0.31341302, 0.11499436, 0.16079812, 0.13417432, 0.25826973, 0.21498771, 0.19588876, 0.1013363, 0.30647293]
			[0.29263914, 0.09422492, 0.32596686, 0.13207547, 0.125, 0.36363637, 0.0, 0.0, 0.29496402]
    		*/
    		
    		// By subtracting these two feature vectors, we may obtain a margin of error for each number
    		/*
    		(0.608245, 0.254971, 0.960614, 0.879579, 0., 0.906362, 0.694818, 0.335988, 0.68668)
    		(0.27328, 1.77185, 0., 0., 1.03557, 0., 0.279183, 1.88725, 0.309463)
    		.
    		.
    		.
    		.
    		.
    		etc
    		
    		*/
    		
    		// Now we should be able to compare the feature vectors of hand written numbers/letters with the computer generated numbers/letters and guess which number the feature vector represents!
    		
    	}
    }

}

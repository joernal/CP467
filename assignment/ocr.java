package assignment;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;

import javax.imageio.*;
import javax.swing.*;

import assignment.pixel;
import assignment.symbol;
import java.util.List;


public class ocr {

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
      System.out.println("Q to quit");

      // Input
      System.out.print("Select an option: ");
      input = reader.next();

      //Change image
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
      }

    } while (!input.equals("Q"));
  }

  public static List<symbol> merge(List<symbol> syms, int i, int j, pixel pix){
	  for (int k = 0; k<syms.get(i).list_of_pixels.size(); k++){
		  syms.get(j).list_of_pixels.add(syms.get(i).list_of_pixels.get(k));
	  }
	  syms.get(j).list_of_pixels.add(pix);
	  syms.remove(i);
	  return syms;
  }

  public static void count_symbols( BufferedImage img ){

    List<symbol> syms = new ArrayList<symbol>();

    int counter = 0;

    int merge1index = -1;
    int merge2index = -1;

    boolean value = false;

    if (img.getWidth() > 0){
      int grayscale = 0;
      for (int yPixel = 0; yPixel < img.getHeight(); yPixel++){
        for (int xPixel = 0 ; xPixel < img.getWidth(); xPixel++){
          int color = img.getRGB(xPixel, yPixel);
          if (color==Color.BLACK.getRGB()){
          	counter++;
          	pixel pix = new pixel(xPixel, yPixel, grayscale);
          	if (syms.size()!=0){
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
          			symbol s = new symbol();
          			s.addPixel(pix);
          			syms.add(s);

          		}

          	}else{
          		symbol s = new symbol();
          		s.addPixel(pix);
          		syms.add(s);
          	}


          }

        }

      }
      System.out.println("Total number of Black Pixels: " + counter);

      for (int k=0; k<syms.size(); k++){
      	System.out.println("Number of black pixels in symbol " + k + " is " + syms.get(k).list_of_pixels.size());
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
    int sum;
    for (int i = 0; i < newImage.getWidth(); i++){
      for (int j = 0; j < newImage.getHeight(); j++){
        pixels = img.getRGB(((int)(i*colScaleRatio)), ((int)(j*rowScaleRatio)), colWindow, rowWindow, null, 0, colWindow);
        sum = 0;
        for (int p: pixels){
          if (p == Color.BLACK.getRGB()){
            sum++;
          }
        }
        graylevel = ((double)sum/pixels.length);
        if (graylevel > threshold){
          newImage.setRGB(i,j,Color.BLACK.getRGB());
        } else {
          newImage.setRGB(i,j,Color.WHITE.getRGB());
        }
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


  public static void convolute( BufferedImage img ){

	  int padding = 2;

	  List<pixel> pixels = new ArrayList<pixel>();

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

		        int gray = (r + g + b) / 3;  // get gray value

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

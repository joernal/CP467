import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import javax.swing.*;
//import java.util.array.*;

public class ReadImage {

  public static void main(String []args){
    BufferedImage img = null;
    int counter = 0;
    try {
      img = ImageIO.read(new File("img2.bmp"));
      Symbol[] symbols = new Array;
      if (img.getWidth() > 0){
        System.out.println("Got image!");
        for (int xPixel = 0 ; xPixel < img.getWidth(); xPixel++){
          for (int yPixel = 0; yPixel < img.getHeight(); yPixel++){
            int color = img.getRGB(xPixel, yPixel);
            if (color==Color.BLACK.getRGB()){
              Pixel new_pixel = new Pixel(xPixel, yPixel);
              if (symbols == []){ //Adding method later in life
                Symbol new_symbol = new Symbol(new_pixel);
                symbols.append(new_symbol);
              } else {
                symbols.length;

              }

              counter++;
            }
          }
        }
        System.out.println("Black Pixels: " + counter);
      } else {
        System.out.println("No Image!");
      }
    } catch (IOException e){
      System.out.println("Image not found!");
    }
  }
}

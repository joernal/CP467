import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import javax.swing.*;

public class ReadImage {

  public static void main(String []args){
    BufferedImage img = null;
    try {
      img = ImageIO.read(new File("img.bmp"));
      if (img.getWidth() > 0){
        System.out.println("Got image!");
      } else {
        System.out.println("No Image!");
      }
    } catch (IOException e){
      System.out.println("Image not found!");
    }
  }

}

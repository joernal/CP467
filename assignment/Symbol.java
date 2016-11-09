package assignment;

import java.io.*;
import java.util.*;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.lang.reflect.Array;
import javax.imageio.ImageIO;

public class Symbol {
    public ArrayList<Pixel> listOfPixels = new ArrayList<Pixel>();
    //private int extremeTop, extremeBottom, extremeLeft, extremeRight;
    
    public Symbol( Pixel newPixel ) {
	this.listOfPixels.add( newPixel );
	//this.extremeTop = this.extremeBottom = newPixel.y;
	//this.extremeLeft = this.extremeRight = newPixel.x;
    }
    
    public void addPixel( Pixel newPixel ){
	this.listOfPixels.add( newPixel );
    }
    
    public boolean removePixel( Pixel pixelToRemove ){
	return this.listOfPixels.remove( pixelToRemove );
    }
    
    public boolean pixelExists( Pixel pixel ){
	return this.listOfPixels.contains( pixel );
    }
    
    public boolean searchSymbol( Pixel p ){
	boolean result = false;
	int size = this.listOfPixels.size();
	int i;
	for (i=0; i < size; i++){
	    if((this.listOfPixels.get(i).x == p.x -1 && this.listOfPixels.get(i).y == p.y)
	       || (this.listOfPixels.get(i).x == p.x && this.listOfPixels.get(i).y == p.y-1)
	       ||(this.listOfPixels.get(i).x == p.x -1 && this.listOfPixels.get(i).y == p.y-1)
	       ||(this.listOfPixels.get(i).x == p.x +1 && this.listOfPixels.get(i).y == p.y-1)){
		return true;
	    }
	}
	return result;
    }
    
    public int getExtremeTop(){
    	int top = this.listOfPixels.get(0).y;
    	for (int i = 0; i < this.listOfPixels.size(); i++){
    		int y = this.listOfPixels.get(i).y;
    		if (y < top){
    			top = y; 
    		}
    	}
    	return top;
    }
    
    public int getExtremeBottom(){
    	int bottom = this.listOfPixels.get(0).y;
    	for (int i = 0; i < this.listOfPixels.size(); i++){
    		int y = this.listOfPixels.get(i).y;
    		if (y > bottom){
    			bottom = y; 
    		}
    	}
    	return bottom;
    }
    
    public int getExtremeLeft(){
    	int left = this.listOfPixels.get(0).x;
    	for (int i = 0; i < this.listOfPixels.size(); i++){
    		int x = this.listOfPixels.get(i).x;
    		if (x < left){
    			left = x; 
    		}
    	}
    	return left;
    }
    
    public int getExtremeRight(){
    	int right = this.listOfPixels.get(0).x;
    	for (int i = 0; i < this.listOfPixels.size(); i++){
    		int x = this.listOfPixels.get(i).x;
    		if (x > right){
    			right = x; 
    		}
    	}
    	return right;
    }
    
}

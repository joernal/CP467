package assignment;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import assignment.pixel;

public class symbol {
	public List<pixel> list_of_pixels = new ArrayList<pixel>();
	
	public void addPixel(pixel p){
		this.list_of_pixels.add(p);
	}
	
	public boolean searchSymbol(pixel p){
		boolean result = false;
		int size = this.list_of_pixels.size();
		int i;
		for (i=0; i < size; i++){
			if((this.list_of_pixels.get(i).x == p.x -1 && this.list_of_pixels.get(i).y == p.y)
					|| (this.list_of_pixels.get(i).x == p.x && this.list_of_pixels.get(i).y == p.y-1)
					||(this.list_of_pixels.get(i).x == p.x -1 && this.list_of_pixels.get(i).y == p.y-1)
					||(this.list_of_pixels.get(i).x == p.x +1 && this.list_of_pixels.get(i).y == p.y-1)){
				//System.out.println("TRUEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE");
				return true;
			}
			/* for testing purposes only
			else{
				System.out.println("FAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAALSE");
			}*/
		}
		return result;
	}
}

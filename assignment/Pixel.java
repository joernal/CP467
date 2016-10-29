package assignment;

public class Pixel {
    
    int x, y;
    int grayscale;
    boolean delete = false;
    
    public Pixel(int x, int y, int grayscale, boolean delete){
	this.x = x;
	this.y = y;
	this.grayscale = grayscale;
	this.delete = delete;
    }
}

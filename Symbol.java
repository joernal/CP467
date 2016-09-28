import Pixel.java;

public class Symbol {

  Pixel pixels = new Array [900];

  Symbol (Pixel pixel){
    //this.pixels = new Array [900];
    this.pixels.append(pixel);
  }

  void addPixel (Pixel pixel){
    pixels.append(pixel);
  }

  Pixel getPixel(int i) {
    return pixels[i];
  }


}

package RFBService;

import java.awt.Color;
import java.awt.image.DirectColorModel;

/**
 * This class provide closest match for RGB pixel value in 8-bit palette range.
 * Suitable for RFB protocol.
 * 
 * @author igor.delac@gmail.com
 *
 */
public class ColorMap8bit {

	private Color[] colors;
	private double[] colorDistance;
	
	private DirectColorModel cm8;
	
	/**
	 * Instance of color map with following masks:<BR>
	 * <UL>
	 * <LI>Red   color mask: 0x00000111</LI>
	 * <LI>Green color mask: 0x00111000</LI>
	 * <LI>Blue  color mask: 0x11000000</LI>
	 * </UL>
	 */
	public ColorMap8bit() {

		cm8 = new DirectColorModel(8, 7, (7 << 3), (3 << 6));

		colors = new Color[256];
		colorDistance = new double[256];
		
		for (int i = 0; i < 256; i++) {
			int rgbValue = cm8.getRGB(i);
			colors[i] = new Color(rgbValue);
			colorDistance[i] = distance(colors[i]); 
		}

	}
	
	/**
	 * Return closest 8-bit pixel value for RGB pixel.
	 * A distance is calculated as:<BR>
	 *  <PRE>sqrt(red^2 + green^2 + blue^2)</PRE>
	 * 
	 * @param red red component
	 * @param green green component
	 * @param blue blue component
	 * @return pixel value
	 */
	public int get8bitPixelValue(int red, int green, int blue) {
		int retVal = 0;
		
		double distance = distance(red, green, blue);
		double delta = Double.MAX_VALUE;
		
		for (int i = 0; i < colors.length; i++) {
			double current = Math.abs(distance - colorDistance[i]); 
			if (current < delta) {
				retVal = i;
				delta = current;
			}
		}
		
		return retVal;
	}
	
	private double distance(Color rgb) {
		return distance (rgb.getRed(), rgb.getGreen(), rgb.getBlue());
	}
	
	private double distance(int red, int green, int blue) {
		if (red == 0 && green == 0 && blue == 0) {
			return 0;
		}
		return Math.sqrt(red * red + green * green + blue * blue);
	}
}

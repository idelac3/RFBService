package RFBDemo;

public class BufferCopyTest {

	static int width = 6, height = 5;
	
	static int[] buffer = {
		22, 33, 44, 55, 66, 77,
		1,   2,  3,  4,  5,  6,
		15, 25, 35, 45, 55, 65,
		 0,  0,  0,  0,  0,  0,
		66, 66, 66, 66, 66, 66 
	};

	static int[] buffer1 = {
		22, 33, 44, 55, 66, 77,
		1,  22,  3,  4,  5,  6,
		15, 25, 35, 45, 55, 65,
		 0,  0,  0,  0,   0,  0,
		66, 66, 66, 66, 66, 67 
	};
	
	static int[] subBuffer(int x1, int y1, int x2, int y2) {
	
		int w1 = x2 - x1;
		int h1 = y2 - y1;
		
		int[] newBuffer = new int[w1 * h1];
		
		for (int y = y1; y <= h1; y++) {
			int srcPos = y * width + x1;
			int destPos = (y - y1) * w1;
			int length = w1;
			System.arraycopy(buffer, srcPos, newBuffer, destPos, length);
		}
		return newBuffer;
	}
	
	static void print(int[] buffer, int w, int h) {
		for (int row = 0; row < h; row++) {
			for (int col = 0; col < w; col++) {
				System.out.print(buffer[row * w + col] + ", ");
			}
			System.out.println();
		}
		System.out.println();
	}
	

    static boolean colMatch(int[] buf1, int col1, int[] buf2, int col2, int width) {
        boolean match = true;
        
        for (int i = col1, j = col2;
                i < buf1.length && j < buf2.length;
                i = i + width, j = j + width) {
            if (buf1[i] != buf2[j]) {
                match = false;
                break;
            }
        }
        
        return match;
    }
    
    static boolean rowMatch(int[] buf1, int row1, int[] buf2, int row2, int width) {
        boolean match = true;
        
        for (int i = row1 * width, j = row2 * width;
                i < (row1 + 1) * width && j < (row2 + 1) * width;
                i++, j++) {
            if (buf1[i] != buf2[j]) {
                match = false;
                break;
            }
        }
        
        return match;
    }

	public static void main(String[] args) {

		print(buffer, width, height);
		print(buffer1, width, height);
		
		int x1 = 1, y1 = 1, x2 = 4, y2 = 5;

		for (int col = 0; col < width; col++) {
			/*
			 * Look for different columns ...
			 */
			if (! colMatch(buffer, col, buffer1, col, width)) {																
				x1 = col;
				break;
			}
		}
		
		for (int row = 0; row < height; row++) {
			/*
			 * Look for different rows ...
			 */
			if (! rowMatch(buffer, row, buffer1, row, width)) {																
				y1 = row;
				break;
			}
		}

		for (int col = width - 1; col >= 0; col--) {
			/*
			 * Look for different columns ...
			 */
			if (! colMatch(buffer, col, buffer1, col, width)) {																
				x2 = col;
				break;
			}
		}
		
		for (int row = height - 1; row >= 0; row--) {
			/*
			 * Look for different rows ...
			 */
			if (! rowMatch(buffer, row, buffer1, row, width)) {																
				y2 = row;
				break;
			}
		}
		
		System.out.println("(x1, y1) = " + x1 + ", " + y1 + "   (x2, y2) = " + x2 + ", " + y2);
	}

}

package files;

import java.io.IOException;
import java.io.RandomAccessFile;

public class RandomAccess {
    /**
     * Treat the file as an array of (unsigned) 8-bit values and sort them
     * in-place using a bubble-sort algorithm.
     * You may not read the whole file into memory!
     *
     * @param file
     */
    public static void sortBytes(RandomAccessFile file) throws IOException {
        int curr = -1, next = -1;
        int j, i;
        for (i = 0; i < file.length(); i++) {
            for (j = 0; j < file.length() - i - 1; j++) {
                file.seek(j);
                curr = file.read();
                next = file.read();
                if (curr > next) { //switch in bubbleSort
                    file.seek(j);
                    file.write(next);
                    file.write(curr);
                }

            }
        }
    }
    /**
     * Treat the file as an array of unsigned 24-bit values (stored MSB first) and sort
     * them in-place using a bubble-sort algorithm.
     * You may not read the whole file into memory!
     *
     * @param file
     * @throws IOException
     */
    public static void sortTriBytes(RandomAccessFile file) throws IOException {
        long curr=0, next=0;
        byte [] currArr = new byte [3]; //helper array to save the current 3 bytes in order to build the number
        byte [] nextArr = new byte [3]; //helper array to save the next 3 byte in order to build the number
        file.seek(0); //make sure the pointer is in the beginning of the file
        for(int i=0; i<=file.length()-3; i+=3) {
            for (int j = 3; j < (file.length() - i); j += 3) {
                currArr[0] = file.readByte();
                currArr[1] = file.readByte();
                currArr[2] = file.readByte();
                nextArr[0] = file.readByte();
                nextArr[1] = file.readByte();
                nextArr[2] = file.readByte();
                for (byte b : currArr) // building the current number
                {
                    curr = (curr << 8) + (b & 0xFF);
                }
                for (byte b : nextArr) //building the next number
                {
                    next = (next << 8) + (b & 0xFF);
                }
                if (curr > next) // switch in bubble sort
                {
                    file.seek(j - 3);
                    file.write(nextArr);
                    file.seek(j);
                    file.write(currArr);
                }
                file.seek(j); //continue the sort from the next number
                curr = 0;
                next = 0;
            }
            file.seek(0); //pointer goes back to the begging of the file in every full iteration
        }
    }
}

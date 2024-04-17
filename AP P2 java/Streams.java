package files;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class Streams {
    /**
     * Read from an InputStream until a quote character (") is found, then read
     * until another quote character is found and return the bytes in between the two quotes.
     * If no quote character was found return null, if only one, return the bytes from the quote to the end of the stream.
     *
     * @param in
     * @return A list containing the bytes between the first occurrence of a quote character and the second.
     */
    public static List<Byte> getQuoted(InputStream in) throws IOException {
        int temp = in.read();
        List<Byte> l1 = new ArrayList<>();
        while(temp!=-1&&temp!=(int)'\"') //iterate until the first "
        {
            temp = in.read();
        }
        if(temp==-1)//no " was found
            return null;
        temp = in.read(); // go over to the next char after the first "
        while(temp!=-1&&temp!=(int)'\"') //iterate until the end of the file or until the second "
        {
            l1.add((byte)(temp));
            temp=in.read();
        }
        return l1;
    }


    /**
     * Read from the input until a specific string is read, return the string read up to (not including) the endMark.
     *
     * @param in      the Reader to read from
     * @param endMark the string indicating to stop reading.
     * @return The string read up to (not including) the endMark (if the endMark is not found, return up to the end of the stream).
     */
    public static String readUntil(Reader in, String endMark) throws IOException {
        StringBuilder mainString = new StringBuilder(); //gets the string char after char
        StringBuilder helperString = new StringBuilder(); //gets a part of the string that can possibly be an endMark
        int i = 0; //endMark length counter
        char currChar = (char)in.read();
        while(currChar!=-1){ //end of stream
            if(currChar==endMark.charAt(i))//while in a possible endMark
            {
                helperString.append(currChar);
                i++;
                if(i==endMark.length())//found a full endmark
                    return mainString.toString();
            }
            else
                {
                        mainString.append(helperString);
                        i=0; //reset the length counter
                        helperString.delete(0, helperString.length()); // reset the endMark helper string
                        mainString.append(currChar);
                }
            currChar=(char)(in.read());
        }
        mainString.append(helperString); //if the stream ends in the middle of an endMark still append it
        return mainString.toString();
    }

    /**
     * Copy bytes from input to output, ignoring all occurrences of badByte.
     *
     * @param in
     * @param out
     * @param badByte
     */
    public static void filterOut(InputStream in, OutputStream out, byte badByte) throws IOException {
        int currByte=in.read();
        while(currByte!=-1)//end of input stream
        {
            if((byte)currByte!=badByte)
                out.write((byte)currByte);
            currByte=in.read();
        }

    }

    /**
     * Read a 40-bit (unsigned) integer from the stream and return it. The number is represented as five bytes,
     * with the most-significant byte first.
     * If the stream ends before 5 bytes are read, return -1.
     *
     * @param in
     * @return the number read from the stream
     */
    public static long readNumber(InputStream in) throws IOException {
        long number = 0;
        int currByte;
        for(int i=0; i<=4; i++) // 5 Bytes input
        {
            currByte = in.read();
            if(currByte==-1)//less than 5 bytes
                return -1;
            number = number<<8 | currByte; //moves the current 8 bits "forward" and adds the new Byte

        }
        return number;
    }
}

package dict;

import com.sun.source.tree.Tree;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Map;
import java.util.TreeMap;

/**
 * Implements a persistent dictionary that can be held entirely in memory.
 * When flushed, it writes the entire dictionary back to a file.
 * <p>
 * The file format has one keyword per line:
 * <pre>word:def</pre>
 * <p>
 * Note that an empty definition list is allowed (in which case the entry would have the form: <pre>word:</pre>
 *
 * @author talm
 */
public class InMemoryDictionary extends TreeMap<String, String> implements PersistentDictionary {
    private static final long serialVersionUID = 1L; // (because we're extending a serializable class)
    private File dictFile;

    public InMemoryDictionary(File dictFile) {
        super();
        this.dictFile=dictFile;
    }

    @Override
    public void open() throws IOException {
        try {
            RandomAccessFile rndFile = new RandomAccessFile(dictFile, "rw");
            String currLine = rndFile.readLine(); //reads line by line
            String word, def;
            while (currLine != null) {
                word = currLine.substring(0, currLine.indexOf(':'));
                def = currLine.substring(currLine.indexOf(':')+1, currLine.length());
                put(word, def); // adds the ket and def to the structure
                currLine = rndFile.readLine(); //reads next line
            }
            rndFile.close();
            }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() throws IOException {
        try {
            RandomAccessFile rndFile = new RandomAccessFile(dictFile, "rw"); //creates random access file for use
            rndFile.setLength(0); //clears the file
            String key, value, currline;
            int dictSize = size();
            for (int i = 0; i < dictSize; i++) {
                key = firstKey();
                value = remove(key); //key and value gets the word and def from the structure and delete them
                currline = (key + ":" + value);
                rndFile.writeBytes(currline + System.lineSeparator()); //writing the word and def to the file

            }
            rndFile.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}

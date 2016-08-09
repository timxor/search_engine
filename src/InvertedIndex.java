import java.io.IOException;
import java.nio.file.Path;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * This class contains the main logic for the data structure used in the SearchEngine project.
 * 
 * @author Tim Siwula
 * @version 1.5
 */
public class InvertedIndex
{
	private TreeMap<String, TreeMap<Path, TreeSet<Integer>>> index; // TODO Which keyword can you use here?

	public InvertedIndex()
	{
		index = new TreeMap<String, TreeMap<Path, TreeSet<Integer>>>();
	}

	/**
	 * This method returns true if the index is empty, otherwise false.
	 * 
	 * @return True if empty, false otherwise.
	 */
	public boolean isEmpty()
	{
		// TODO This is fine, but there is a Map.isEmpty() method too.
		return index.size() == 0;
	}

	/**
	 * This method adds a word, fileName and location into the index.
	 * 
	 * @param word
	 *            The word to put in the index if the index does not contain the word.
	 * @param fileName
	 *            The path of the file where the word was discovered.
	 * @param location
	 *            The location for a word inside a given @fileName @see fileName.
	 */
	public void add(String word, Path fileName, Integer location)
	{
		if (!index.containsKey(word))
		{
			index.put(word, new TreeMap<Path, TreeSet<Integer>>());
		}

		if (!index.get(word).containsKey(fileName)) // add fileName to index if non existent.		// word --> file
		{
			index.get(word).put(fileName, new TreeSet<Integer>());
		}

		index.get(word).get(fileName).add(location); // add location to index if non existent.  		// word --> file --> location
	}

	/**
	 * This method writes out the index to a file.
	 * 
	 * @param outputFile
	 *            The path the write the index to.
	 * @exception IOException
	 *                on output error.
	 * @see IOException
	 */
	public void toJSON(Path outputFile) throws IOException
	{
		JSONTreeWriter.writeInvertedIndex(outputFile, index);
	}

	/**
	 * This method returns a String representation of the index.
	 * 
	 * @return A String representation of the index.
	 */
	public String toString()
	{
		return index.toString();
	}

	/**
	 * This method returns the number of words inside the index.
	 * 
	 * @return An integer of the number of words inside the index.
	 */
	public int numWords()
	{
		return index.size();
	}

	/**
	 * This method checks if a given word is inside the index.
	 * 
	 * @param word
	 *            The word to search for in the index.
	 * @return True if found, otherwise false.
	 */
	public boolean hasWord(String word)
	{
		return index.get(word) != null;
	}

	/**
	 * This method checks if a given word is in a given path, inside the index.
	 * 
	 * @param word
	 *            The word to search for in the index.
	 * @param pathName
	 *            The path to search for in the index.
	 * @return True if found, otherwise false.
	 */
	public boolean hasPath(String word, Path pathName)
	{
		// TODO Potential for null pointer exception here! What if index.get(...) returns null?
		return index.get(word).get(pathName) != null;
	}

	/**
	 * This method checks if a given word is in a given path is at a specific location, inside the index.
	 * 
	 * @param word
	 *            The word to search for in the index.
	 * @param pathName
	 *            The path to search for in the index.
	 * @param location
	 *            The location of the word for a given pathName.
	 * @return True if found, otherwise false.
	 */
	public boolean hasPosition(String word, Path pathName, Integer location)
	{
		// TODO Potential for null pointer exception here! What if index.get(...) returns null?
		return index.get(word).get(pathName).contains(location);
	}

}

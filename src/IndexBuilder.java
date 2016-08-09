import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

/**
 * The class Builder is responsible for building the index.
 *
 */
public class IndexBuilder
{
	/** Regular expression for removing special characters. */
	public static final String CLEAN_REGEX = "(?U)[^\\p{Alnum}\\p{Space}]+";

	/** Regular expression for splitting text into words by whitespace. */
	public static final String SPLIT_REGEX = "(?U)\\p{Space}+";

	/**
	 * The method buildIndex is responsible for populating the data structure with the word, file and location data.
	 *
	 * @param files
	 *            This is the list of files to process.
	 *
	 * @param invertedIndex
	 *            This is a triple nested data structure.
	 */
	public static void buildIndex(Set<Path> files, InvertedIndex invertedIndex) throws IOException
	{
		for (Path i : files)
		{
			if (Files.exists(i))
			{
				try (BufferedReader reader = Files.newBufferedReader(i))
				{
					Integer location = 1;
					String line = null;
					while ((line = reader.readLine()) != null)
					{
						String[] words = split(line);

						for (String s : words)
						{
							invertedIndex.add(s, i, location);
							location++;
						}
					}
				}
				catch (FileNotFoundException e)
				{
					System.err.println("Please check that your file exists. It was not found.");
				}
				catch (IOException x)
				{
					System.err.println("Please check with your local Builder expert. You have an exception in the method buildIndex()");
				}
				catch (Exception e)
				{
					System.err.println("This shouldn't ever happen, but then again, bugs happen");
					System.err.println(e.toString());
				}
			}
		}
	}

	/**
	 * The method split() splits all the words and returns them in an array.
	 *
	 * @param text
	 *            This is the list of files to process.
	 */
	public static String[] split(String text)
	{
		String[] arrayOfWords = new String[] // declare and initialize array to return
		{};

		text = clean(text); // clean word
		if (text.isEmpty()) // check to see if there is any text and return array if empty
		{
			return arrayOfWords;
		}
		else
		{
			arrayOfWords = text.split(SPLIT_REGEX); // put words into array and return array
			return arrayOfWords;
		}
	}

	/**
	 * The method clean() processes all words and removes special characters, converts to lower case.
	 *
	 * @param word
	 *            This is the word to clean.
	 */
	public static String clean(String word)
	{
		word = word.toLowerCase();
		word = word.replaceAll(CLEAN_REGEX, ""); // replace special characters
		word = word.trim(); // remove white space
		return word;
	}
}

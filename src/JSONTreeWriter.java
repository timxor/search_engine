import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;

public class JSONTreeWriter
{
	// TOOD Whoa, what is this? A PUBLIC INSTANCE member initialized as if it were static? I think you can remove this.
	public boolean first = true;

	public static String indent(int times) throws IOException
	{
		return times > 0 ? String.format("%" + (times * 2) + "s", " ") : "";
	}

	/**
	 * Helper method to quote text for output. This requires escaping the quotation mark " as \" for use in Strings. For
	 * example:
	 * 
	 * <pre>
	 * String text = "hello world";
	 * System.out.println(text); // output: hello world
	 * System.out.println(quote(text)); // output: "hello world"
	 * </pre>
	 * 
	 * @param text
	 *            input to surround with quotation marks
	 * @return quoted text
	 */
	public static String quote(String text)
	{
		return "\"" + text + "\"";
	}

	/**
	 * The method writeInvertedIndex is responsible for writing out the data structure to a text file.
	 *
	 * @param outputFile
	 *            is the path representation for the file to output.
	 *
	 * @param invertedIndex
	 *            is the data structure to extract the data to the outputFile.
	 */
	public static void writeInvertedIndex(Path outputFile,
			TreeMap<String, TreeMap<Path, TreeSet<Integer>>> invertedIndex) throws IOException
	{
		Charset charset = Charset.forName("UTF-8");
		try (BufferedWriter writer = Files.newBufferedWriter(outputFile, charset))
		{
			writer.write("{" + System.lineSeparator());
			String lastWord = invertedIndex.lastKey();

			for (String word : invertedIndex.headMap(lastWord, false).keySet())
			{
				Path lastFile = invertedIndex.get(word).lastKey();
				if (invertedIndex.keySet().size() == 0)
				{
					writer.write("{" + System.lineSeparator() + "}");
				}
				else if (invertedIndex.keySet().size() == 1)
				{
					writer.write(indent(1) + quote(word) + ": {" + System.lineSeparator());
				}
				else
				{
					writer.write(indent(1) + quote(word) + ": {" + System.lineSeparator());
					for (Entry<Path, TreeSet<Integer>> file : invertedIndex.get(word).entrySet())
					{
						String fileName = file.getKey().toString();
						Integer lastLocation = file.getValue().last();
						if (invertedIndex.get(word).size() == 0)
						{
							writer.write(indent(2) + quote(fileName.toString()) + ": [" + System.lineSeparator());
						}
						else if (invertedIndex.get(word).size() == 1)
						{
							writer.write(indent(2) + quote(fileName.toString()) + ": [" + System.lineSeparator());
							for (Integer location : file.getValue().headSet(lastLocation, false))
							{
								writer.write(indent(3) + location.toString() + "," + System.lineSeparator());
							}
							writer.write(indent(3) + lastLocation.toString() + System.lineSeparator());
							writer.write(indent(2) + "]" + System.lineSeparator());
						}
						else
						{
							writer.write(indent(2) + quote(fileName.toString()) + ": [" + System.lineSeparator());
							for (Integer location : file.getValue().headSet(lastLocation, false))
							{
								writer.write(indent(3) + location.toString() + "," + System.lineSeparator());
							}
							writer.write(indent(3) + lastLocation.toString() + System.lineSeparator());
							if (fileName.equalsIgnoreCase(lastFile.toString()))
							{
								writer.write(indent(2) + "]" + System.lineSeparator());
							}
							else
							{
								writer.write(indent(2) + "]," + System.lineSeparator());
							}

						}
					}
				}
				writer.write(indent(1) + "}," + System.lineSeparator());
			}
			writer.write(indent(1) + quote(lastWord) + ": {" + System.lineSeparator());
			writer.write(indent(2) + quote(invertedIndex.get(lastWord).firstKey().toString()) + ": [" + System.lineSeparator());
			Integer loc = invertedIndex.get(lastWord).get(invertedIndex.get(lastWord).firstKey()).first();
			writer.write(indent(3) + loc.toString() + System.lineSeparator());
			writer.write(indent(2) + "]" + System.lineSeparator());
			writer.write(indent(1) + "}" + System.lineSeparator());
			writer.write("}" + System.lineSeparator());
			writer.flush();
		}
		catch (IOException x)
		{
			System.err.println("Please check with your local JSONTreeWriter expert. You have an exception in the method writeInvertedIndex()");
		}
		catch (Exception e)
		{
			// TODO Fix exception handling here!
			System.err.println("JSONTreeWriter.writeInvertedIndex( )");
			e.printStackTrace(System.out);
		}
	}
	
	// TODO If we have time during your next code review, lets talk about how to generalize/reuse code here a bit better.
}

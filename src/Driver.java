import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

/**
 * This software driver class provides a consistent entry point for the search engine. Based on the arguments provided
 * to {@link #main(String[])}, it creates the necessary objects and calls the necessary methods to build an inverted
 * index, process search queries, configure multithreading, and launch a web server (if appropriate).
 */
public class Driver
{
	/**
	 * Flag used to indicate the following value is an input directory of text files to use when building the inverted
	 * index.
	 * 
	 * @see "Projects 1 to 5"
	 */
	public static final String INPUT_FLAG = "-input";

	/**
	 * Flag used to indicate the following value is the path to use when outputting the inverted index to a JSON file.
	 * If no value is provided, then {@link #INDEX_DEFAULT} should be used. If this flag is not provided, then the
	 * inverted index should not be output to a file.
	 * 
	 * @see "Projects 1 to 5"
	 */
	public static final String INDEX_FLAG = "-index";

	/**
	 * Flag used to indicate the following value is a text file of search queries.
	 * 
	 * @see "Projects 2 to 5"
	 */
	public static final String QUERIES_FLAG = "-query";

	/**
	 * Flag used to indicate the following value is the path to use when outputting the search results to a JSON file.
	 * If no value is provided, then {@link #RESULTS_DEFAULT} should be used. If this flag is not provided, then the
	 * search results should not be output to a file.
	 * 
	 * @see "Projects 2 to 5"
	 */
	public static final String RESULTS_FLAG = "-results";

	/**
	 * Flag used to indicate the following value is the number of threads to use when configuring multithreading. If no
	 * value is provided, then {@link #THREAD_DEFAULT} should be used. If this flag is not provided, then multithreading
	 * should NOT be used.
	 * 
	 * @see "Projects 3 to 5"
	 */
	public static final String THREAD_FLAG = "-threads";

	/**
	 * Flag used to indicate the following value is the seed URL to use when building the inverted index.
	 * 
	 * @see "Projects 4 to 5"
	 */
	public static final String SEED_FLAG = "-seed";

	/**
	 * Flag used to indicate the following value is the port number to use when starting a web server. If no value is
	 * provided, then {@link #PORT_DEFAULT} should be used. If this flag is not provided, then a web server should not
	 * be started.
	 */
	public static final String PORT_FLAG = "-port";

	/**
	 * Default to use when the value for the {@link #INDEX_FLAG} is missing.
	 */
	public static final String INDEX_DEFAULT = "index.json";

	/**
	 * Default to use when the value for the {@link #RESULTS_FLAG} is missing.
	 */
	public static final String RESULTS_DEFAULT = "results.json";

	/**
	 * Default to use when the value for the {@link #THREAD_FLAG} is missing.
	 */
	public static final int THREAD_DEFAULT = 5;

	/**
	 * Default to use when the value for the {@link #PORT_FLAG} is missing.
	 */
	public static final int PORT_DEFAULT = 8080;

	/**
	 * Parses the provided arguments and, if appropriate, will build an inverted index from a directory or seed URL,
	 * process search queries, configure multithreading, and launch a web server.
	 * 
	 * @param args
	 *            set of flag and value pairs
	 * @throws IOException
	 */
	public static void main(String[] args)
	{
		// Step #1 Initialize helper classes to use.
		ArgumentParser argParser = new ArgumentParser();
		InvertedIndex index = new InvertedIndex();

		// Step #2 Process arguments and file paths.
		argParser.parseArguments(args);
		
		// TODO If you are going to use hard-coded flags, go ahead and delete the constants at the top of this file.
		String inputFileOfIndex = getOrDefault(argParser, "-input", null);
		String outputFileOfIndex = getOrDefault(argParser, "-index", "index.json");
		Path outputFile = Paths.get(outputFileOfIndex).toAbsolutePath().normalize();

		if (hasNoErrors(argParser))
		{
			try
			{
				// Step #3 Add files to a list.
				Set<Path> files = DirectoryTraverser.getAllFiles(inputFileOfIndex);

				// Step #4 Build index from files.
				IndexBuilder.buildIndex(files, index);

				// Step #5 Output results to file.
				if (createOutputFileForIndex(argParser))
				{
					if (!index.isEmpty())
					{
						index.toJSON(outputFile);
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
		else
		{
			System.err.println("Now terminating program. Please enter correct args.");
		}
	}

	/**
	 * See if user included a "-input" argument. If so then we need to make a file.
	 * 
	 * @see #hasValue(String)
	 */
	public static boolean createOutputFileForIndex(ArgumentParser argParser)
	{
		return argParser.hasValue("-input");
	}

	/**
	 * See if user included a "-query" argument. If so then we need to build the search results.
	 * 
	 * @see #hasValue(String)
	 */
	public boolean buildSearchResults(ArgumentParser argParser)
	{
		return argParser.hasValue("-query");
	}

	/**
	 * See if user included a "-results" argument. If so then we need to output the search results.
	 * 
	 * @see #hasFlag(String)
	 */
	public boolean createOutputFileForSearch(ArgumentParser argParser)
	{
		return argParser.hasFlag("-results");
	}

	// TODO Maybe move this to ArgumentParser and use the Map.getOrDefault() method:
	// TODO https://docs.oracle.com/javase/8/docs/api/java/util/Map.html#getOrDefault-java.lang.Object-V-
	
	/**
	 * Determine the path for a given flag. If specified, get specific path else return default.
	 * 
	 * @see #getValue(String)
	 */
	public static String getOrDefault(ArgumentParser argParser, String flag, String defaultFile)
	{
		if (flag.equalsIgnoreCase("-index") && argParser.getValue("-index") != null) // #1 output file for index 
		{
			return argParser.getValue("-index");
		}
		else if (flag.equalsIgnoreCase("-input") && argParser.getValue("-input") != null) //#2 input file for index
		{
			return argParser.getValue("-input");
		}
		else
		{
			return defaultFile;
		}
	}

	/**
	 * Returns true if the command line arguments are incorrect else false.
	 *
	 * @param args
	 *            command-line arguments
	 *
	 * @see #isFlag(String)
	 * @see #isValue(String)
	 */
	public static boolean hasNoErrors(ArgumentParser argParser)
	{
		return (argParser.hasFlag("-index") || argParser.hasFlag("-results"));
	}

}

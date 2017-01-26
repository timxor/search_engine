/*
 * =============================================================================
 * Production:   =        http://youtalky.com
 * Source:       =        https://github.com/usf-cs212-2016/project-tcsiwula
 * File:         =        SearchBuilderThreaded.java
 * Created:      =        12/11/16
 * Author:       =        Tim Siwula <tcsiwula@gmail.com>
 * University:   =        University of San Francisco
 * Class:        =        CS 212: Software Development
 * License:      =        GPLv2
 * Version:      =        0.001
 * ==============================================================================
 */

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.TreeMap;

public class SearchResultBuilderThreaded extends SearchResultBuilder implements SearchResultBuilderInterface
{
	/**
	 * A sweet logger for logging.
	 */
	private static final Logger logger = LogManager.getLogger();

	/**
	 * A diligent work queue that loves to work.
	 */
	private final ThreadedWorkQueue worker;

	/**
	 * Stores the inverted searchResults in a triple nested data structure.
	 */
	private final TreeMap<String, ArrayList<SearchResult>> searchResults;

	/**
	 * A reference to the inverted globalIndex.
	 */
	private final IndexThreaded globalIndex;

	/**
	 * Class constructor that builds the searchResults from the query list.
	 *
	 * @param globalIndex A index that stores a database of words.
	 * @param worker      A work queue for delegating work to threads.
	 */
	public SearchResultBuilderThreaded(IndexThreaded globalIndex, ThreadedWorkQueue worker)
	{
		super(globalIndex);
		searchResults = new TreeMap<>();
		this.worker = worker;
		this.globalIndex = globalIndex;
		logger.debug("SearchResultBuilderThreaded() instantiated.");
	}

	/**
	 * The method processed all queries and adds them to the globalIndex.
	 *
	 * @param inputDirectory This is the file that has the queries.
	 * @param exactSearch    This is flag what kind of search to perform.
	 */
	public void parseQueryFile(String inputDirectory, boolean exactSearch)
	{
		Path fileToRead = Paths.get(inputDirectory);

		if(Files.exists(fileToRead))
		{
			try (BufferedReader reader = Files.newBufferedReader(fileToRead))
			{
				String query;

				while ((query = reader.readLine()) != null)
				{
					worker.execute(new SearchResultMinion(query, exactSearch));
				}
			} catch (IOException e)
			{
				System.err.println("Unable to parse query's from this directory: " + inputDirectory);
			}
		}

		// close up threads and return to system.
		worker.finish();
	}

	/**
	 * Handles per-directory parsing. If a subdirectory is encountered, a new
	 * {@link IndexBuilderThreaded.TextFileMinion} is created to handle that subdirectory.
	 */
	public class SearchResultMinion implements Runnable
	{
		private final String query;
		private final boolean exactSearch;

		public SearchResultMinion(String query, boolean exactSearch)
		{
			logger.debug("Minion created for {}", query);
			this.query = query;
			this.exactSearch = exactSearch;
		}

		@Override public void run()
		{
			processQuery(query, exactSearch);
		}
	}

	/**
	 * The method processed all queries and adds them to the globalIndex.
	 *
	 * @param query A query to process.
	 * @param exactSearch This is flag what kind of search to perform.
	 */
	private void processQuery(String query, boolean exactSearch)
	{
		//clean the lines and sort.
		query = super.clean(query);
		String[] words = query.split(super.SPLIT_REGEX);
		Arrays.sort(words);
		query =  String.join(" ", words);
		ArrayList<SearchResult> results = (exactSearch) ? globalIndex.exactSearch(words) : globalIndex.partialSearch(words);

		synchronized (searchResults)
		{
			searchResults.put(query, results);
		}
	}

	/**
	 * This method writes out the searchResults to a file.
	 *
	 * @param outputFile The path the write the searchResults to.
	 */
	public void toJSON(String outputFile)
	{
		logger.debug("entered IndexThreaded.toJSON() outputfile = " + outputFile);

		try
		{
			synchronized (searchResults)
			{
				JSONTreeWriter.writeSearchResults(outputFile, searchResults);
			}
		} catch (ArithmeticException | IllegalArgumentException | IOException | NoSuchElementException e)
		{
			System.err.println("Unable to write the search results builder to JSON to the file " + outputFile + " .");
		}
	}
}

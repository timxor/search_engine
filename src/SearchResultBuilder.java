/*
 * =============================================================================
 * Production:   =        http://youtalky.com
 * Source:       =        https://github.com/usf-cs212-2016/project-tcsiwula
 * File:         =        SearchResultBuilder.java
 * Created:      =        11/6/16
 * Author:       =        Tim Siwula <tcsiwula@gmail.com>
 * University:   =        University of San Francisco
 * Class:        =        CS 212: Software Development
 * License:      =        GPLv2
 * Version:      =        0.001
 * ==============================================================================
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class SearchResultBuilder implements SearchResultBuilderInterface
{
	/**
	 * Stores the inverted searchResults in a triple nested data structure.
	 */
	private final TreeMap<String, ArrayList<SearchResult>> searchResults;

	/**
	 * Stores the inverted searchResults in a triple nested data structure.
	 */
	private final TreeMap<String, ArrayList<SearchResult>> searchHistory;

	/**
	 * Regular expression for removing special characters.
	 */
	private static final String CLEAN_REGEX = "(?U)[^\\p{Alnum}\\p{Space}]+";

	/**
	 * Regular expression for splitting text into words by whitespace.
	 */
	protected static final String SPLIT_REGEX = "(?U)\\p{Space}+";

	/**
	 * Stores the index.
	 */
	private final InvertedIndex index;

	/**
	 * Class constructor that builds the searchResults from the query list.
	 */
	public SearchResultBuilder(InvertedIndex index)
	{
		this.index = index;
		searchResults = new TreeMap<>();
		searchHistory = new TreeMap<>();
	}

	// TODO Breaks encapsulation!
	public TreeMap<String, ArrayList<SearchResult>> getSearchResults()
	{
//		final TreeMap<String, ArrayList<SearchResult>> tmp = new TreeMap<>();
//		tmp = Collections.copy(searchResults);
		//searchResults = Collections.addAll(searchResults.entrySet());
		return (TreeMap<String, ArrayList<SearchResult>>) searchResults.clone();
	}

	/**
	 * The method processed all queries and adds them to the index.
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
					//clean the lines and sort.
					query = clean(query);
					query = sort(query);

					//add each query query to the output list
					searchResults.putIfAbsent(query, new ArrayList<>());

					String[] words = query.split(" ");

					if(exactSearch)
					{
						searchResults.put(query, index.exactSearch(words));
					} else
					{
						searchResults.put(query, index.partialSearch(words));
					}
				}
			} catch (IOException e)
			{
				System.err.println("Unable to parse query's from this directory: " + inputDirectory);
			}
		}
	}

	/**
	 * The method processed all queries and adds them to the index.
	 *
	 * @param inputDirectory This is the file that has the queries.
	 * @param exactSearch    This is flag what kind of search to perform.
	 */
	public void parseQueryFile(String inputDirectory, String query, boolean exactSearch)
	{
		Path fileToRead = Paths.get(inputDirectory);

		if(Files.exists(fileToRead))
		{
			//add each query query to the output list
			searchResults.putIfAbsent(query, new ArrayList<>());

			String[] words = query.split(" ");

			if(exactSearch)
			{
				searchResults.put(query, index.exactSearch(words));
			} else
			{
				searchResults.put(query, index.partialSearch(words));
			}
		}
	}

	/**
	 * The method split() splits all the words and returns them in an array.
	 *
	 * @param text This is the list of files to process.
	 */
	protected static String sort(String text)
	{
		String[] theWords = text.split(SPLIT_REGEX);
		Arrays.sort(theWords);
		return String.join(" ", theWords);
	}

	/**
	 * The method clean() processes all words and removes special characters, converts to lower case.
	 *
	 * @param word This is the word to clean.
	 */
	protected static String clean(String word)
	{
		word = word.toLowerCase();
		word = word.replaceAll(CLEAN_REGEX, ""); // replace special characters
		word = word.trim(); // remove white space
		return word;
	}

	/**
	 * This method writes out the searchResults to a file.
	 *
	 * @param outputFile The path the write the searchResults to.
	 */

	public void toJSON(String outputFile)
	{
		try
		{
			JSONTreeWriter.writeSearchResults(outputFile, searchResults);
		} catch (ArithmeticException | IllegalArgumentException | IOException | NoSuchElementException e)
		{
			System.err.println("Unable to write the search results builder to JSON to the file " + outputFile + " .");
		}
	}

	public void writeSearchResultsToHtml(PrintWriter writer, String searchTime)
	{
		try
		{
			JSONTreeWriter.	writeSearchResultsToHtml(searchResults, writer, searchTime);
		} catch (ArithmeticException | IllegalArgumentException | IOException | NoSuchElementException e)
		{
			System.err.println("Unable to write the search results builder to JSON to the file html.");
		}

		// clear results for next search otherwise
		// the next search will contain the previous
		// results, store them in history.

		searchHistory.putAll(searchResults);
		searchResults.clear();
	}

	public void writeSearchHistory(PrintWriter writer, String searchTime)
	{
		try
		{
			JSONTreeWriter.	writeSearchHistoryToHtml(searchHistory, writer, searchTime);
		} catch (ArithmeticException | IllegalArgumentException | IOException | NoSuchElementException e)
		{
			System.err.println("Unable to write the search results builder to JSON to the file html.");
		}
	}

	public String writeToString()
	{
		try
		{
			return JSONTreeWriter.writeHtml(searchHistory);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		return null;
	}
}
/*
 * =============================================================================
 * Production:   =        http://youtalky.com
 * Source:       =        https://github.com/usf-cs212-2016/project-tcsiwula
 * File:         =        InvertedIndex.java
 * Created:      =        11/6/16
 * Author:       =        Tim Siwula <tcsiwula@gmail.com>
 * University:   =        University of San Francisco
 * Class:        =        CS 212: Software Development
 * License:      =        GPLv2
 * Version:      =        0.001
 * ==============================================================================
 */
package com.timsiwula.searchengineapp;

import java.io.IOException;
import java.util.*;

public class InvertedIndex
{
	/**
	 * Stores the inverted globalIndex in a triple nested data structure.
	 */
	private final TreeMap<String, TreeMap<String, TreeSet<Integer>>> globalIndex;

	/**
	 * Constructor creates a new instance of globalIndex for each object.
	 */
	public InvertedIndex()
	{
		globalIndex = new TreeMap<>();
	}

	/**
	 * Constructor the allows for an easy way to convert between Concurrent and non-concurrent indexes.
	 */
	public InvertedIndex(InvertedIndex other)
	{
		this.globalIndex = other.globalIndex;
	}

	/**
	 * Perform an partial search with the line on the globalIndex.
	 *
	 * @param words The word to put in the globalIndex if the globalIndex does not contain the word.
	 */
	public ArrayList<SearchResult> partialSearch(String[] words)
	{
		ArrayList<SearchResult> results = new ArrayList<>();
		Map<String, SearchResult> resultMap = new HashMap<>();

		for (String word : words)
		{
			for (String wordInTheIndex : globalIndex.tailMap(word).keySet())
			{
				if(wordInTheIndex.startsWith(word))
				{
					for (String file : globalIndex.get(wordInTheIndex).keySet())
					{
						int count = globalIndex.get(wordInTheIndex).get(file).size();
						int indexLocation = globalIndex.get(wordInTheIndex).get(file).first();

						if(resultMap.containsKey(file))
						{
							resultMap.get(file).update(count, indexLocation);

						} else
						{
							SearchResult newResult = new SearchResult(file, count, indexLocation);
							results.add(newResult);
							resultMap.put(file, newResult);
						}
					}
				} else
				{
					break;
				}
			}
		}
		Collections.sort(results);
		return results;
	}

	/**
	 * Perform an exact search with the line on the globalIndex.
	 *
	 * @param words The word to put in the globalIndex if the globalIndex does not contain the word.
	 */
	public ArrayList<SearchResult> exactSearch(String[] words)
	{
		ArrayList<SearchResult> results = new ArrayList<>();
		Map<String, SearchResult> resultMap = new HashMap<>();

		for (String word : words)
		{
			if(globalIndex.containsKey(word))
			{
				for (String file : globalIndex.get(word).keySet())
				{
					int count = globalIndex.get(word).get(file).size();
					int indexLocation = globalIndex.get(word).get(file).first();

					if(resultMap.containsKey(file))
					{
						resultMap.get(file).update(count, indexLocation);
					} else
					{
						SearchResult newResult = new SearchResult(file, count, indexLocation);
						results.add(newResult);
						resultMap.put(file, newResult);
					}
				}
			}
		}
		Collections.sort(results);
		return results;
	}

	/**
	 * This method adds a word, fileName and location into the globalIndex.
	 *
	 * @param word     The word to put in the globalIndex if the globalIndex does not contain the word.
	 * @param fileName The path of the file where the word was discovered.
	 * @param location The location for a word inside a given @fileName @see fileName.
	 */
	public void add(String word, String fileName, Integer location)
	{
		if(!globalIndex.containsKey(word))
		{
			globalIndex.put(word, new TreeMap<>());
		}

		// add fileName to globalIndex if non existent.		// word --> file
		if(!globalIndex.get(word).containsKey(fileName))
		{
			globalIndex.get(word).put(fileName, new TreeSet<>());
		}

		// add location to globalIndex if non existent.  		// word --> file --> location
		globalIndex.get(word).get(fileName).add(location);
	}

	/**
	 * This method adds a word, fileName and location into the globalIndex.
	 *
	 * @param localIndex An InvertedIndex that we would like to add to the global InvertedIndex.
	 */
	public void allGather(InvertedIndex localIndex)
	{
		// for each word
		for (String word : localIndex.globalIndex.keySet())
		{
			// if the word is not in the index
			if(!globalIndex.containsKey(word))
			{
				// add the word's set/map
				globalIndex.put(word, localIndex.globalIndex.get(word));
			} else
			{
				// for each file
				for (String fileName : localIndex.globalIndex.get(word).keySet())
				{
					// add fileName to globalIndex if non existent.
					if(!globalIndex.get(word).containsKey(fileName))
					{
						globalIndex.get(word).put(fileName, localIndex.globalIndex.get(word).get(fileName));
					} else
					{
						// TODO Call addAll instead

						globalIndex.get(word).get(fileName).addAll(localIndex.globalIndex.get(word).get(fileName));

						//						// for each location
//						for (Integer location : localIndex.globalIndex.get(word).get(fileName))
//						{
//							if(!globalIndex.get(word).get(fileName).contains(location))
//							{
//								// add location to globalIndex if non existent.
//								globalIndex.get(word).get(fileName).add(location);
//							}
//						}
					}
				}
			}
		}
	}

	/**
	 * This method returns a String representation of the globalIndex.
	 *
	 * @return A String representation of the globalIndex.
	 */
	public String toString()
	{
		return globalIndex.toString();
	}

	/**
	 * This method writes out the globalIndex to a file.
	 *
	 * @param outputFile The path the write the globalIndex to.
	 */
	public void toJSON(String outputFile)
	{
		try
		{
			JSONTreeWriter.toJSON(outputFile, globalIndex);
		} catch (ArithmeticException | IllegalArgumentException | IOException | NoSuchElementException e)
		{
			System.err.println("Unable to write the globalIndex to JSON in your inverted globalIndex to the file " + outputFile + " .");
		}
	}
}

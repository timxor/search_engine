/*
 * =============================================================================
 * Production:   =        http://youtalky.com
 * Source:       =        https://github.com/usf-cs212-2016/project-tcsiwula
 * File:         =        ConcurrentInvertedIndex.java
 * Created:      =        11/6/16
 * Author:       =        Tim Siwula <tcsiwula@gmail.com>
 * University:   =        University of San Francisco
 * Class:        =        CS 212: Software Development
 * License:      =        GPLv2
 * Version:      =        0.001
 * ==============================================================================
 */
package com.timsiwula.searchengineapp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

/**
 * A thread safe inverted index that uses custom lock class @ThreadedReadWriteLock.
 */
public class IndexThreaded extends InvertedIndex
{
	/**
	 * A lock with integrity. It allows multiple concurrent read operations only.
	 */
	private final ThreadedReadWriteLock readWriteLock;

	/**
	 * A sweet logger for logging.
	 */
	private static final Logger logger = LogManager.getLogger();

	/**
	 * A superb IndexThreaded constructor.
	 */
	public IndexThreaded()
	{
		super();
		readWriteLock = new ThreadedReadWriteLock();
	}

	/**
	 * Perform an partial search with the line on the index.
	 *
	 * @param words The word to put in the index if the index does not contain the word.
	 */
	@Override public ArrayList<SearchResult> partialSearch(String[] words)
	{
		logger.debug("entered IndexThreaded.partialSearch()");

		readWriteLock.lockReadOnly();
		try
		{
			return super.partialSearch(words);
		} finally
		{
			readWriteLock.unlockReadOnly();
		}
	}

	/**
	 * Perform an exact search with the line on the index.
	 *
	 * @param words The word to put in the index if the index does not contain the word.
	 */
	@Override public ArrayList<SearchResult> exactSearch(String[] words)
	{
		logger.debug("entered IndexThreaded.exactSearch()");
		readWriteLock.lockReadOnly();

		try
		{
			return super.exactSearch(words);
		} finally
		{
			readWriteLock.unlockReadOnly();
		}
	}

	/**
	 * This method adds a word, fileName and location into the index.
	 *
	 * @param word     The word to put in the index if the index does not contain the word.
	 * @param fileName The path of the file where the word was discovered.
	 * @param location The location for a word inside a given @fileName @see fileName.
	 */
	@Override public void add(String word, String fileName, Integer location)
	{
		logger.debug("entered IndexThreaded.add()");
		readWriteLock.lockReadWrite();

		try
		{
			super.add(word, fileName, location);
		} finally
		{
			readWriteLock.unlockReadWrite();
		}
	}

	/**
	 * This method returns a String representation of the index.
	 *
	 * @return A String representation of the index.
	 */
	@Override public String toString()
	{
		logger.debug("entered IndexThreaded.toString()");
		readWriteLock.lockReadOnly();

		try
		{
			return super.toString();
		} finally
		{
			readWriteLock.unlockReadOnly();
		}
	}

	/**
	 * This method writes out the index to a file.
	 *
	 * @param outputFile The path the write the index to.
	 */
	@Override public void toJSON(String outputFile)
	{
		logger.debug("entered IndexThreaded.toJSON() outputfile = " + outputFile);
		readWriteLock.lockReadOnly();

		try
		{
			super.toJSON(outputFile);
		} finally
		{
			readWriteLock.unlockReadOnly();
		}
	}

	/**
	 * This method adds a localIndex the the globalIndex.
	 *
	 * @param localIndex This is a localIndex that we want to add to the globalIndex.
	 */
	@Override public void allGather(InvertedIndex localIndex)
	{
		logger.debug("entered IndexThreaded.add()");
		readWriteLock.lockReadWrite();

		try
		{
			super.allGather(localIndex);
		} finally
		{
			readWriteLock.unlockReadWrite();
		}
	}
}

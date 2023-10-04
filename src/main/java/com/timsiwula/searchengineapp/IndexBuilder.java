/*
 * =============================================================================
 * Production:   =        http://youtalky.com
 * Source:       =        https://github.com/usf-cs212-2016/project-tcsiwula
 * File:         =        IndexBuilder.java
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.NoSuchElementException;
import java.util.Set;

public class IndexBuilder implements IndexBuilderInterface
{
	/**
	 * Stores the index.
	 */
	private final InvertedIndex index;

	/**
	 * Class constructor that builds the searchResults from the query list.
	 */
	public IndexBuilder(InvertedIndex index)
	{
		this.index = index;
	}

	/**
	 * The method buildIndex is responsible for populating the data structure with the word, file and location data.
	 *
	 * @param files This is the list of files to process.
	 */
	public void recurse(Set<Path> files)
	{
		for (Path i : files)
		{
			if(Files.exists(i))
			{
				try
				{
					IndexBuilderInterface.buildIndex(i, index);
				} catch (ArithmeticException | IllegalArgumentException | NoSuchElementException | IOException e)
				{
					System.err.println("Unable to construct the index with your files and index.");
				}
			}
		}
	}

	@Override public void buildIndex(String directory)
	{
		Set<Path> files = DirectoryTraverser.getAllFiles(directory);
		recurse(files);
	}
}

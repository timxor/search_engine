/*
 * =============================================================================
 * Production:   =        http://youtalky.com
 * Source:       =        https://github.com/usf-cs212-2016/project-tcsiwula
 * File:         =        IndexBuilderThreaded.java
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

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class IndexBuilderThreaded extends IndexBuilder implements IndexBuilderInterface
{
	/**
	 * A sweet logger for logging.
	 */
	private static final Logger logger = LogManager.getLogger();

	/**
	 * Stores the index.
	 */
	private final InvertedIndex globalIndex;

	/**
	 * A diligent work queue that loves to work.
	 */
	private final ThreadedWorkQueue worker;

	/**
	 * A superb IndexBuilderThreaded constructor.
	 *
	 * @param globalIndex A index that stores a database of words.
	 * @param worker      A work queue for delegating work to threads.
	 */
	public IndexBuilderThreaded(InvertedIndex globalIndex, ThreadedWorkQueue worker)
	{
		super(globalIndex);
		this.worker = worker;
		this.globalIndex = globalIndex;
		logger.debug("IndexBuilderThreaded() instantiated.");
	}

	/**
	 * Gets all localThreadFiles and directories.
	 *
	 * @param directory A starting directory.
	 */
	public void buildIndex(String directory)
	{
		String extension = ".txt";

		if(Files.isDirectory(Paths.get(directory)))
		{
			try
			{
				traverse(directory, extension);
			} catch (IOException x)
			{
				System.err.println("Unable to build index from the file " + directory + ".");
			}
		}
		// stop threads and return to system.
		worker.finish();
	}

	/**
	 * Traverses all sub-directories.
	 *
	 * @param directory Starting directory.
	 * @param extension Extension to include.
	 * @throws IOException on output error.
	 * @see IOException
	 */
	public void traverse(String directory, String extension) throws IOException
	{
		Path path = Paths.get(directory);

		try (DirectoryStream<Path> listing = Files.newDirectoryStream(path))
		{
			for (Path currentDirectory : listing)
			{
				String currentFile = currentDirectory.getFileName().toString();

				// if it is a text file
				if(currentFile.toLowerCase().endsWith(extension.toLowerCase()))
				{
					worker.execute(new TextFileMinion(currentDirectory));
					logger.debug("Minion created for {}", directory);
				}
				// if it is a directory
				else if(Files.isDirectory(currentDirectory))
				{
					traverse(currentDirectory.toString(), extension);
				}
			}
		}
	}

	/**
	 * Handles per-directory parsing. If a subdirectory is encountered, a new
	 * {@link TextFileMinion} is created to handle that subdirectory.
	 */
	public class TextFileMinion implements Runnable
	{
		private final Path file;
		private final InvertedIndex localIndex;

		public TextFileMinion(Path file)
		{
			localIndex = new InvertedIndex();
			logger.debug("Minion created for {}", file);
			this.file = file;
		}

		@Override public void run()
		{
			try
			{
				IndexBuilderInterface.buildIndex(file, localIndex);
				globalIndex.allGather(localIndex);
			} catch (IOException e)
			{
				System.err.println("Worker unable to parse " + file + ".");
			}
		}
	}
}

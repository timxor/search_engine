/*
 * =============================================================================
 * Project:      =        project-tcsiwula-master
 * Package:      =        PACKAGE_NAME
 * Created:      =        12/1/16
 * Author:       =        Tim Siwula <tcsiwula@usfca.edu>
 * University:   =        University of San Francisco
 * Class:        =        Computer Science 345: Programming Languages
 * Liscense:     =        GPLv2
 * Version:      =        0.001
 * ==============================================================================
 */
package com.timsiwula.searchengineapp;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

public class DirectoryTraverser
{
	/**
	 * Gets all files and directories.
	 * @param directory A starting directory.
	 */
	public static Set<Path> getAllFiles(String directory)
	{
		String extension = ".txt";
		Set<Path> files = new HashSet<>();

		if (Files.isDirectory(Paths.get(directory)))
		{
			try
			{
				traverse(files, directory, extension);
			}
			catch (IOException x)
			{
				System.err.println("Unable to build index from the file " + directory + " .");
			}
		}
		return files;
	}

	/**
	 * Traverses all sub-directories.
	 *
	 * @param files Files to process.
	 * @param directory Starting directory.
	 * @param extension Extension to include.
	 * @throws IOException on output error.
	 * @see IOException
	 */
	public static void traverse(Set<Path> files, String directory, String extension) throws IOException
	{
		Path path = Paths.get(directory);

		try (DirectoryStream<Path> listing = Files.newDirectoryStream(path))
		{
			for (Path currentDirectory : listing)
			{
				String currentFile = currentDirectory.getFileName().toString();

				if (currentFile.toLowerCase().endsWith(extension.toLowerCase()))
				{
					files.add(currentDirectory);
				}
				else if (Files.isDirectory(currentDirectory))
				{
					traverse(files, currentDirectory.toString(), extension);
				}
			}
		}
	}
}
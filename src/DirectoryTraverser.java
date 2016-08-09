import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * The class DirectoryTraverser finds and builds a list of all files to be processed for a given path.
 *
 */
public class DirectoryTraverser
{
	/**
	 * Gets all files in all directories and sub directories for a given path.
	 *
	 * @param files
	 *            a list of files to return?
	 * 
	 * @param dirName
	 *            a list of files to return?
	 * @see #parseArguments(String[])
	 */
	public static Set<Path> getAllFiles(String directory) throws IOException
	{
		String extension = ".txt";
		Set<Path> files = new HashSet<Path>();
		if (Files.isDirectory(Paths.get(directory)))
		{
			traverse(files, directory, extension);
		}
		return files;
	}

	/**
	 * Traverses all sub-directories and adds the files to the list.
	 *
	 * @param files
	 *            a list to add each filename to.
	 * 
	 * @param directory
	 *            a directory to search for files
	 * 
	 * @param extension
	 *            a way to select what file extension you want to include.
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

	/**
	 * Prints all the files in the current list.
	 *
	 * @param files
	 *            a list of file names found.
	 */
	public static void printFiles(Collection<Path> files)
	{
		System.out.println("Files found: ");
		for (Path i : files)
		{
			System.out.println("\t" + i.getFileName());
		}
		System.out.println();
	}
}

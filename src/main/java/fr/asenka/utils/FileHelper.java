package fr.asenka.utils;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.WRITE;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.DirectoryStream.Filter;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.digester3.RegexMatcher;
import org.apache.commons.digester3.SimpleRegexMatcher;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

public class FileHelper {
	
	private static final RegexMatcher MATCHER = new SimpleRegexMatcher();

	public static final Filter<Path> ALL = path -> true;
	public static final Filter<Path> FILE_FILTER = path -> Files.isRegularFile(path);
	public static final Filter<Path> DIRECTORY_FILTER = path -> Files.isDirectory(path);
	public static final Filter<Path> CSV_FILTER = extensionFileFilter("csv");
			
	public static Filter<Path> extensionFileFilter(String extension) {
		return path -> Files.isRegularFile(path) 
				&& StringUtils.equalsIgnoreCase(extension, FilenameUtils.getExtension(path.getFileName().toString())) ;
	}
	
	public static Filter<Path> fileNameFilter(String regexPattern) {
		return path -> Files.isRegularFile(path)
				 && MATCHER.match(path.getFileName().toString(), regexPattern);
	}
	
	public static List<Path> getPaths(String dirPath, Filter<Path> filter) throws IOException {
		return getPaths(getPath(dirPath), filter);
	}
	
	public static List<Path> getPaths(Path dirPath, Filter<Path> filter) throws IOException {
		
		assertPathAreDirectories(dirPath);
		
		List<Path> paths = new ArrayList<>();
		
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath, filter)) {
			for (Path path : stream) 
				paths.add(path);
		}
		return paths;
	}

	public static Path getPath(String filePath) throws IOException {
		try {
			return Paths.get(filePath);
		} catch (InvalidPathException e) {
			throw new IOException(e);
		}
	}

	public static String getString(String filePath) throws IOException {
		return getString(getPath(filePath));
	}
	
	public static String getString(Path filePath) throws IOException {
		return new String(Files.readAllBytes(filePath), UTF_8);
	}
	
	public static List<String> getLines(String filePath) throws IOException {
		return Files.readAllLines(getPath(filePath), UTF_8);
	}
	
	public static List<String> getLines(Path filePath) throws IOException {
		return Files.readAllLines(filePath, UTF_8);
	}
	
	public static Path write(String filePath, String content, boolean createParentIfMissing) throws IOException {
		return write(getPath(filePath), content, createParentIfMissing);
	}

	public static Path write(Path filePath, String content, boolean createParentIfMissing) throws IOException {
		
		Path parentPath = filePath.getParent();
		
		if (Files.notExists(parentPath)) 
			if (createParentIfMissing)
				Files.createDirectories(parentPath);
			else
				throw new IOException(parentPath + " n'existe pas");
		
		return Files.write(filePath, content!= null ? content.getBytes() : new byte[0]);
	}
	
	public static Path append(String filePath, String content) throws IOException {
		return append(getPath(filePath), content);
	}

	public static Path append(Path filePath, String content) throws IOException {
		return Files.write(filePath, content.getBytes(), WRITE, APPEND);
	}
	
	public static Path moveFile(String filePath, String targetDirPath) throws IOException {
		return moveFile(getPath(filePath), getPath(targetDirPath));
	}
	
	public static Path moveFile(Path filePath, Path targetDirPath) throws IOException {
		assertPathAreDirectories(targetDirPath);
		return Files.move(filePath, getPath(targetDirPath.toString() + "/" + filePath.getFileName().toString()));
	}
	
	public static int moveAllFiles(String sourceDirPath, String targetDirPath) throws IOException {
		return moveAll(sourceDirPath, targetDirPath, FILE_FILTER);
	}
	
	public static int moveAllFiles(Path sourceDirPath, Path targetDirPath) throws IOException {
		return moveAll(sourceDirPath, targetDirPath, FILE_FILTER);
	}
	
	public static int moveAll(String sourceDirPath, String targetDirPath, Filter<Path> filter) throws IOException {
		return moveAll(getPath(sourceDirPath), getPath(targetDirPath), filter);
	}

	public static int moveAll(Path sourceDirPath, Path targetDirPath, Filter<Path> filter) throws IOException {
		
		int count = 0;
		for (Path path : getPaths(sourceDirPath, filter)) { 
			Files.move(path, getPath(targetDirPath.toString() + "/" + path.getFileName().toString()));
			count++;
		}
		return count;
	}
	
	public static int deleteFiles(String targetDirPath, Filter<Path> filter, boolean recursive) throws IOException {
		return deleteFiles(getPath(targetDirPath), filter, recursive);
	}
	
	public static int deleteFiles(Path targetDirPath, Filter<Path> filter, boolean recursive) throws IOException {
		
		if (filter == DIRECTORY_FILTER && recursive)
			throw new IllegalArgumentException("L'usage de DIRECTORY_FILTER n'est pas autorisé en récursif");
		
		int count = 0;
		for (Path path : getPaths(targetDirPath, ALL)) { 
			
			if (Files.isRegularFile(path)) {
				count += deleteIf(path, filter);
			} else if (Files.isDirectory(path)) { 
				if (isEmpty(path)) {
					count += deleteIf(path, filter);
				} else if (recursive) {
					count += deleteFiles(path, ALL, true);
					count += deleteIf(path, filter);
				} else {
					throw new IOException(path + " n'est pas vide");
				}
			}
		}
		return count;
	}
	
	public static Path createDirectories(String dirPath) throws IOException {
		return createDirectories(getPath(dirPath));
	}
	
	public static Path createDirectories(Path dirPath) throws IOException {
		return Files.createDirectories(dirPath);
	}
	
	public static boolean exists(String path) throws IOException {
		return Files.exists(getPath(path));
	}
	
	public static boolean isEmpty(String path) throws IOException {
		return isEmpty(getPath(path));
	}
	
	public static boolean isEmpty(Path path) throws IOException {
		
		if (!Files.exists(path))
			throw new IOException(path + " n'existe pas");
		else if (Files.isRegularFile(path))
			return StringUtils.isEmpty(getString(path));
		else if (Files.isDirectory(path)) 
			return Files.list(path).count() == 0;
		else 
			throw new IOException(Objects.toString(path));
	}
	
	private static int deleteIf(Path path, Filter<Path> filter) throws IOException {
		
		if (filter.accept(path)) {
			Files.delete(path);
			return 1;
		} else {
			return 0;
		}
	}

	private static void assertPathAreDirectories(Path... paths) throws IOException {
		
		List<Path> notDirectory = new ArrayList<>();
		
		for (Path path : paths) 
			if (!Files.isDirectory(path))
				notDirectory.add(path);
		
		if (CollectionUtils.isNotEmpty(notDirectory))
			if (notDirectory.size() == 1)
				throw new IOException(notDirectory.get(0) + " n'est pas un répertoire ou n'existe pas");
			else
				throw new IOException(notDirectory + " ne sont pas des répertoires ou n'existent pas");
	}
}

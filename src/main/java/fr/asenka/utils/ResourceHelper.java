package fr.hm.bjs.io.resources;

import static java.util.stream.Collectors.toMap;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

public final class ResourceHelper {

	private static final String JAR = "jar!";
	private static final String CLASSES = "classes";

	public static String getString(ResourceLoader resourceLoader, String resourceFileName) throws IOException {
		Resource resource = resourceLoader.getResource(resourceFileName);
		return getString(resource);
	}
	
	public static String getString(Resource fileResource) throws IOException {
		InputStream inputStream = fileResource.getInputStream();
		return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
	}
	
	public static List<Resource> getResources(ResourceLoader resourceLoader, String locationPattern) throws IOException {
		ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(resourceLoader);
		Resource[] resources = resolver.getResources(locationPattern);
		return Arrays.asList(resources);	
	}
	
	public static List<Resource> getClasspathResources(ResourceLoader resourceLoader, String classpathLocationPattern) throws IOException {
		return getResources(resourceLoader, "classpath:" + classpathLocationPattern);	
	}
	
	public static Map<String, Resource> getResourcesMap(ResourceLoader resourceLoader, String classpathLocationPattern) throws IOException {
		
		List<Resource> resources = getClasspathResources(resourceLoader, classpathLocationPattern);
		return resources.stream()
				.collect(toMap(ResourceHelper::uncheckedRelativePath, resource -> resource));
	}
	
	public static Map<String, String> getOpenedResourcesMap(ResourceLoader resourceLoader, String classpathLocationPattern) throws IOException {
		
		List<Resource> resources = getClasspathResources(resourceLoader, classpathLocationPattern);
		return resources.stream()
				.collect(toMap(ResourceHelper::uncheckedRelativePath, ResourceHelper::uncheckedGetString));
	}
	
	private static String relativePath(Resource resource) throws IOException {
		
		URL url = resource.getURL();
		String path = url.getPath();
		
		int indexOfJar = path.indexOf(JAR);
		
		if (indexOfJar >= 0)
			return path.substring(indexOfJar + JAR.length());
		
		int indexOfClasses = path.indexOf(CLASSES);
		
		if (indexOfClasses >= 0)
			return path.substring(indexOfClasses + CLASSES.length());
		
		return path;
	}
	
	private static String uncheckedRelativePath(Resource resource) {
		try {
			return relativePath(resource);
		} catch(IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
	private static String uncheckedGetString(Resource resource) {
		try {
			return getString(resource);
		} catch(IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
	private ResourceHelper() {
	}
}

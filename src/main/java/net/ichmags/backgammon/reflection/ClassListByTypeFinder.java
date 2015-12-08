/*
 * www.ichmags.net - Backgammon
 */
package net.ichmags.backgammon.reflection;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import net.ichmags.backgammon.generic.Visitor;

/**
 * The {@link ClassListByTypeFinder} is searching the class path for classes matching
 * the provided {@link Class} type.
 * The search will return all matches found.
 * 
 * @param <T> the {@link Class} type to find.
 * 
 * @author Anastasios Patrikis
 * 
 * @see ClassByTypeFinder
 */
public class ClassListByTypeFinder<T> implements Visitor<String> {

	private Class<T> searchClassType;
	private boolean canCreateInstance;
	private String searchNameRegEx;
	private List<Class<T>> foundClasses;
	
	/**
	 * Constructor.
	 * 
	 * @param searchClassType the {@link Class} to find, which can be an {@code interface} to look for.
	 * @param canCreateInstance {@code true} if {@link Class#newInstance()} should be possible; for 
	 * an {@code interface} or {@code abstract class} this must be set to {@code false}.
	 * @param searchNameRegEx limit the packages and classes to search according to the given
	 * {@code regular expression}.
	 */
	public ClassListByTypeFinder(Class<T> searchClassType, boolean canCreateInstance, String searchNameRegEx) {
		this.searchClassType = searchClassType;
		this.canCreateInstance = canCreateInstance;
		this.searchNameRegEx = searchNameRegEx;
		foundClasses = new ArrayList<Class<T>>(10);
	}
	
	/**
	 * Start the search and return a {@link List} of found matching classes.
	 * 
	 * @return a {@link List} with matching classes or {@code null}.
	 */
	public List<Class<T>> get() {
		new ClassPathTraverser(false).findClasses(this);
		return foundClasses;
	}
	
	/**
	 * Check if {@link #get()} found a match.
	 * 
	 * @return {@code true} if at least one matching class was found.
	 */
	public boolean found() {
		return (foundClasses.size() > 0);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean visit(String className) {
		Class<?> candidate = null;
		try {
			if(className.matches(searchNameRegEx)) {
				candidate = this.getClass().getClassLoader().loadClass(className);
				if(searchClassType.isAssignableFrom(candidate)) {
					if(canCreateInstance) {
						int classModifyers = candidate.getModifiers();
						if( ! (Modifier.isAbstract(classModifyers) || Modifier.isInterface(classModifyers)) ) {
							foundClasses.add((Class<T>)candidate);
						}
					} else {
						foundClasses.add((Class<T>)candidate);
					}
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("Cannot load class: " + className, e);
		}
		
		return true;
	}
}

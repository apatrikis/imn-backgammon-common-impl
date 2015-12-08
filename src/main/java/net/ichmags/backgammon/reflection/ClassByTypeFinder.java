/*
 * www.ichmags.net - Backgammon
 */
package net.ichmags.backgammon.reflection;

import java.lang.reflect.Modifier;

import net.ichmags.backgammon.generic.Visitor;

/**
 * The {@link ClassByTypeFinder} is searching the class path for classes matching
 * the provided {@link Class} type.
 * The search will return the first match.
 * 
 * @param <T> the {@link Class} type to find.
 * 
 * @author Anastasios Patrikis.
 * 
 * @see ClassListByTypeFinder
 */
public class ClassByTypeFinder<T> implements Visitor<String> {

	private Class<T> searchClassType;
	private boolean canCreateInstance;
	private String searchNameRegEx;
	private Class<T> foundClass;
	
	/**
	 * Constructor.
	 * 
	 * @param searchClassType the {@link Class} to find, which can be an {@code interface} to look for.
	 * @param canCreateInstance {@code true} if {@link Class#newInstance()} should be possible; for 
	 * an {@code interface} or {@code abstract class} this must be set to {@code false}.
	 * @param searchNameRegEx limit the packages and classes to search according to the given
	 * {@code regular expression}.
	 */
	public ClassByTypeFinder(Class<T> searchClassType, boolean canCreateInstance, String searchNameRegEx) {
		this.searchClassType = searchClassType;
		this.canCreateInstance = canCreateInstance;
		this.searchNameRegEx = searchNameRegEx;
	}
	
	/**
	 * Get an new instance of the found class.
	 * 
	 * @return a new instance of the found class. To Avoid a {@link RuntimeException} check fist if
	 * the class was found by using {@link #found()}.
	 */
	public T getInstance() {
		if( ! found()) {
			get();
		}
		
		if(found()) {
			try {
				return foundClass.newInstance();
			} catch (Exception e) {
				throw new RuntimeException("Cannot create instance of class: " + foundClass.getName() , e);
			}
		} else {
			throw new RuntimeException("No class found to instantiate");
		}
	}
	
	/**
	 * Start the search and return the first found matching class.
	 * 
	 * @return the first found matching class or {@code null}.
	 */
	public Class<T> get() {
		new ClassPathTraverser(false).findClasses(this);
		return foundClass;
	}
	
	/**
	 * Check if {@link #get()} found a match.
	 * 
	 * @return {@code true} if a matching class was found.
	 */
	public boolean found() {
		return (foundClass != null);
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
							foundClass = (Class<T>)candidate;
						}
					} else {
						foundClass = (Class<T>)candidate;
					}
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("Cannot load class: " + className, e);
		}
		
		return !found();
	}
}

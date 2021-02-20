package test.reflection.singleton;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Singleton. 
 * 
 * Holds a map of class names and corresponding map of getters (and their names).
 */
public final class MethodsCache {

	/**
	 * The Unique instance of this class.
	 */
	private static MethodsCache instance;

	/**
	 * The getters Map.
	 * 
	 * Key = The class name
	 * Value = Map of field name and field getter
	 */
	private final Map<String, Map<String, Method>> map;
	
	/**
	 * Constructor (Hidden).
	 */
	private MethodsCache() { 
		this.map = new HashMap<>();
	}

	/**
	 * 
	 * @return 
	 * 		the singleton instance
	 */
	public static MethodsCache getInstance() {

		if (instance == null) {
			instance = new MethodsCache();
		}
		return instance;
	}
	
	/**
	 * Add a new list of getters.
	 * 
	 * @param className
	 * 		the class name
	 * @param getters
	 *      the map of the field names along with their getters
	 */
	public void put(String className, Map<String, Method> getters) {
		this.map.putIfAbsent(className, getters);
	}
	
	/**
	 * Retrieve the getters for the given class name (if exist)
	 * 
	 * @param className 
	 * 		the method name
	 * @return the map of the field names along with their getters
	 */
	public Map<String, Method> get(String className) {
		return map.get(className);
	}
}

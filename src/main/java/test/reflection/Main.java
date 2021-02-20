package test.reflection;

import static test.reflection.result.Diffs.STATUS.*;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import test.reflection.result.Diffs;
import test.reflection.singleton.MethodsCache;

/**
 * Find differences between any two objects.
 * 
 * @author Haritos Hatzidimitriou
 * @author Sotiris Moschopoulos
 * @since 5/11/2020
 */
public class Main {

	public static MethodsCache cache = MethodsCache.getInstance();

	public static void main(String[] args) {
		
		try {
			Diffs root = new Diffs(oldAfa.getClass().getSimpleName());
			traverse(oldAfa, newAfa, root);
			System.out.println(root.toString());
		} catch (Exception e) {
			System.out.println("Exception: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public static void traverse(final Object oldObject, final Object newObject, final Diffs parent) throws Exception {

		if (oldObject == null && newObject == null) {
			return;
		}

		Map.Entry<String, Method> entry;
		Object oldValue, newValue;
		List<?> oldList, newList;
		boolean isList, bothObjectsExist, isPrimitiveType;
		Map<String, Method> listOfGetters = getMethods(oldObject != null ? oldObject : newObject);
		Iterator<Entry<String, Method>> iterator = listOfGetters.entrySet().iterator();

		while(iterator.hasNext()) {
			entry = iterator.next();
			final String name = entry.getKey();
			oldValue = oldObject != null ? entry.getValue().invoke(oldObject) : null;
			newValue = newObject != null ? entry.getValue().invoke(newObject) : null;
			
			if (oldValue == null && newValue == null) {
				continue;
			} 

			bothObjectsExist = (oldValue != null) && (newValue != null);
			isList = oldValue != null ? (oldValue instanceof List) : (newValue instanceof List);

			if (isList) {
				oldList = oldValue != null ? (List<?>) oldValue : null;
				newList = newValue != null ? (List<?>) newValue : null;
				
				if (listsAreEmpty(oldList, newList)) {
					continue;
				}
				
				final Diffs child = new Diffs(name);
				isPrimitiveType = (oldList != null && oldList.size() > 0) ? isPrimitive(oldList.get(0)) : isPrimitive(newList.get(0));

				if (bothObjectsExist && isPrimitiveType) {
					int max = newList.size() > oldList.size() ? newList.size() : oldList.size();
					for (int i = 0; i < max; i++) {
						compareValues(name, oldList.get(i), newList.get(i), child);
					}
					if (child.hasChanges()) { // Only add if there are changes
						parent.addChild(child);
					}
				} else if (bothObjectsExist) {
					int max = newList.size() > oldList.size() ? newList.size() : oldList.size();
					Diffs child2 = null;
					for (int i = 0; i < max; i++) {
						// 1st time get from parent, for all the other times get from child
						child2 = (i == 0) ? parent.getNewChild(name) : child2.getNewChild(name);
						oldValue = oldList.size() > i ? oldList.get(i) : null;
						newValue = newList.size() > i ? newList.get(i) : null;
						traverse(oldValue, newValue, child2);
						if (!child2.hasChanges()) {
							parent.removeLastChild();
						}
					}
				} else if (oldValue != null) {
					oldList.forEach(item -> child.addChild(new Diffs(name, item, null, DELETED)));
					parent.addChild(child);
				} else if (newList != null) {
					newList.forEach(item -> child.addChild(new Diffs(name, null, item, NEW)));
					parent.addChild(child);
				}
			} else { 
				isPrimitiveType = (oldValue != null) ? isPrimitive(oldValue) : isPrimitive(newValue);
				if (!isPrimitiveType) {
					Diffs newChild = parent.getNewChild(name);
					traverse(oldValue, newValue, newChild);
					if (!newChild.hasChanges()) {
						parent.removeLastChild();
					}
				} else {
					compareValues(name, oldValue, newValue, parent);
				}
			}
		}
	}

	/**
	 * Compares primitive (or BigInteger) values. 
	 * If changes are found a new (child) Diff is added to the given parent
	 * 
	 * @param name
	 * 		the name of the field
	 * @param oldValue
	 * 		the old value
	 * @param newValue
	 * 		the new value
	 * @param parent
	 * 		the parent diff object
	 */
	private static void compareValues(String name, Object oldValue, Object newValue, Diffs parent) {
		
		if (oldValue != null && oldValue instanceof BigInteger)
			oldValue = ((BigInteger) oldValue).intValue();
		if (newValue != null && newValue instanceof BigInteger)
			newValue = ((BigInteger) newValue).intValue();

		if (oldValue != null && newValue != null) {
			if (oldValue.equals(newValue)) {
				parent.addChild(new Diffs(name, oldValue, newValue, NO_CHANGE));
			} else {
				parent.addChild(new Diffs(name, oldValue, newValue, MODIFIED));
			}
		} else if (oldValue != null && newValue == null) {
			parent.addChild(new Diffs(name, oldValue, newValue, DELETED));
		} else if (newValue != null && oldValue == null) {
			parent.addChild(new Diffs(name, oldValue, newValue, NEW));
		} 
	}

	private static boolean listsAreEmpty(List<?> oldList, List<?> newList) {
		if (oldList != null && newList != null) {
			return oldList.isEmpty() && newList.isEmpty();
		} else if (oldList != null) {
			return oldList.isEmpty();
		} else {
			return newList.isEmpty();
		}
	}

	private static boolean isPrimitive(Object object) {
		String name = object.getClass().getName();
		return name.startsWith("java.lang") || object instanceof BigInteger;
	}

	/**
	 * Returns the Map of methods for this bean.
	 */
	private static Map<String, Method> getMethods(Object bean) {

		Map<String, Method> methods = cache.get(bean.getClass().getName());
		if (methods == null) {
			methods = getBeanMethods(bean);
		}
		return methods;
	}

	/**
	 * Returns a list of public 'getter' methods for the given bean
	 */
	private static Map<String, Method> getBeanMethods(Object bean) {
		
		Map<String, Method> methods = new HashMap<>();
		List<String> protectedFields = new ArrayList<>();
		Field[] beanFields = bean.getClass().getDeclaredFields();
		Arrays.asList(beanFields).stream()
			.filter(field -> isProtected(field) && !field.getName().equalsIgnoreCase("uuid"))
		    .forEach(field -> protectedFields.add(field.getName()));

		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass(), Object.class);
			PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
			Arrays.asList(propertyDescriptors).stream()
				.filter(pd -> protectedFields.contains(pd.getName()))
				.forEach(pd -> methods.put(pd.getName(), pd.getReadMethod()));
			
			cache.put(bean.getClass().getName(), methods); // add to the cache for later use
		} catch (IntrospectionException e) {
			System.out.println(e.getMessage());
		}
		return methods;
	}
	
	private static boolean isProtected(Field field) {
		return Modifier.isProtected(field.getModifiers());
	}
}

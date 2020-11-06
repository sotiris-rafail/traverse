package test.reflection;

import static test.reflection.result.Diffs.STATUS.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;

import test.reflection.objects.AFA;
import test.reflection.objects.DepperInner;
import test.reflection.objects.Inner;
import test.reflection.result.Diffs;

/**
 * Find differences between any two objects.
 * 
 * @author Sotiris Moschopoulos
 * @author Haritos Hatzidimitriou
 * 
 * @since  5/11/2020
 */
public class Main {
	
	public static void main(String[] args) {
		
		List<Inner> inners = new ArrayList<>();
		inners.add(new Inner("INNER 1", new DepperInner("deep1", 2, false), "InnerString1"));
		inners.add(new Inner("INNER 2", new DepperInner("deep2", 5, true), "InnerString2", "InnerString3"));
		inners.add(new Inner("INNER 3", new DepperInner("deep3", 1, true), "Haritos", "Swtos"));
		
		List<Inner> inners2 = new ArrayList<>();
		inners2.add(new Inner("INNER 1", new DepperInner("deep1", 3, false), "InnerString1"));
		inners2.add(new Inner("INNER 2", new DepperInner("deep2", 5, true), "InnerString2", "InnerString5"));
		// inners2.add(new Inner("INNER 3", new DepperInner("deep3", 1, true), "InnerString2", "InnerString5"));

		AFA publishedAfa = new AFA("PUBLISHED", inners);
		publishedAfa.setPublished(true);
		AFA newAfa = new AFA("NEW", inners2);
		newAfa.setPublished(false);
		Diffs root;

		try {
			root = new Diffs(publishedAfa.getClass().getSimpleName());
			traverse(publishedAfa, newAfa, root);
			System.out.println(root.toString());
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception: " + e.getMessage());
		}
	}

	public static void traverse(final Object oldObject, final Object newObject, final Diffs parent) throws Exception {

		if (oldObject == null && newObject == null) {
			return;
		}
		
		List<?> newList, oldList;
		Field[] fields = oldObject != null ? oldObject.getClass().getDeclaredFields() : newObject.getClass().getDeclaredFields();
		Object objectToTest = oldObject != null ? oldObject : newObject;
		boolean objectIsList, bothListsExist, bothValuesExist, isPrimitiveType;

		for (Field field : fields) { 
			if (field.getName().equalsIgnoreCase("serialVersionUID") || field.getName().equalsIgnoreCase("uuid") || 
				field.getName().equalsIgnoreCase("logger")) {
				continue;
			} 
			field.setAccessible(true);
			objectIsList = field.get(objectToTest) instanceof List;
			
			if (objectIsList) {
				final Diffs child = new Diffs(field.getName());
				Object oldItem, newItem;
				newList = newObject !=null ? (List<?>) field.get(newObject) : null;
				oldList = oldObject != null ? (List<?>) field.get(oldObject) : null;
				bothListsExist = (newList != null && oldList != null) ? true : false;
				isPrimitiveType = isPrimitive(field.getGenericType().getTypeName());
				
				if (bothListsExist && isPrimitiveType) { 
					int max = newList.size() > oldList.size() ? newList.size() : oldList.size();
					for (int i=0; i< max; i++) {
						oldItem = oldList.get(i);
						newItem = newList.get(i);
						if (oldItem != null && newItem != null && !oldItem.equals(newItem)) {
							child.addChild(new Diffs(field.getName(), oldItem, newItem, MODIFIED)); 
						} else if (oldItem != null && newItem == null) {
							child.addChild(new Diffs(field.getName(), oldItem, newItem, DELETED)); 
						} else if (newItem != null && oldItem == null) { 
							child.addChild(new Diffs(field.getName(), oldItem, newItem, NEW)); 
						}
					}
					if (child.hasChildren()) { // Only add if there are changes
						parent.addChild(child);
					}
				} else if (bothListsExist) {
					int max = newList.size() > oldList.size() ? newList.size() : oldList.size();
					Object old2, new2;
					Diffs child2 = null;
					for (int i = 0; i < max; i++) {
						// 1st time get from parent, for all the other times get from child
						child2 = (i==0) ? parent.getNewChild(field.getName()) : child2.getNewChild(field.getName());
						old2 = oldList.size() > i ? oldList.get(i) : null;
						new2 = newList.size() > i ? newList.get(i) : null;
						traverse(old2, new2, child2);
					}
				} else if (oldList != null) {
					oldList.forEach(item -> child.addChild(new Diffs(field.getDeclaringClass().getSimpleName(), item, null , DELETED)));
					parent.addChild(child);
				} else if (newList != null) {
					newList.forEach(item -> child.addChild(new Diffs(field.getDeclaringClass().getSimpleName(), null, item, NEW)));
					parent.addChild(child);
				} 	
			} else { // not a list
				Object existingValue = oldObject != null ? PropertyUtils.getSimpleProperty(oldObject, field.getName()) : null;
				Object newValue = newObject != null ? PropertyUtils.getSimpleProperty(newObject, field.getName()) : null;

				bothValuesExist = (existingValue != null) && (newValue != null);
				isPrimitiveType = (existingValue != null) ? isPrimitive(existingValue.getClass().getName()) :
					                                        isPrimitive(newValue.getClass().getName());
				
				if (!isPrimitiveType) {
					traverse(existingValue, newValue, parent.getNewChild(field.getName()));
				} else if (bothValuesExist && !existingValue.equals(newValue)) {
					parent.addChild(new Diffs(field.getName(), existingValue, newValue, MODIFIED));
				} else if (existingValue != null && newValue == null) {
					parent.addChild(new Diffs(field.getName(), existingValue, newValue, DELETED)); 
				} else if (newValue != null && existingValue == null){ 
					parent.addChild(new Diffs(field.getName(), existingValue, newValue, NEW));
				}  
			}
		}
	}

	private static boolean isPrimitive(String name) {
		return name.contains("java.lang");
	}
}

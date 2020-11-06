package test.reflection;

import static test.reflection.objects.Diffs.STATUS.DELETED;
import static test.reflection.objects.Diffs.STATUS.MODIFIED;
import static test.reflection.objects.Diffs.STATUS.NEW;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;

import test.reflection.objects.AFA;
import test.reflection.objects.DepperInner;
import test.reflection.objects.Diffs;
import test.reflection.objects.Inner;

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
		newAfa.setPublished(true);
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

	public static void traverse(Object published, Object newObject, Diffs parent) throws Exception {

		Field[] fields;
		List<Field> listOfFields = new ArrayList<>();

		if (published != null) {
			fields = published.getClass().getDeclaredFields();
			Collections.addAll(listOfFields, published.getClass().getDeclaredFields());
			listOfFields.forEach(field -> {
				field.setAccessible(true);
			});
		} else if (newObject != null) {
			fields = newObject.getClass().getDeclaredFields();
			Collections.addAll(listOfFields, newObject.getClass().getDeclaredFields());
			listOfFields.forEach(field -> field.setAccessible(true));
		} else {
			return;
		}
	
		Object objectToTest = published != null ? published : newObject;
		
		for (Field field : fields) { 
			if (field.getName().equalsIgnoreCase("serialVersionUID") ||
				field.getName().equalsIgnoreCase("uuid") || 
				field.getName().equalsIgnoreCase("logger")) {
				continue;
			} 
			field.setAccessible(true);
			
			if (field.get(objectToTest) instanceof List) {
				List<?> newList = newObject !=null ? (List<?>) field.get(newObject) : null;
				List<?> publishedList = published != null ? (List<?>) field.get(published) : null;

				if (newList != null && publishedList != null) {
					if (isPrimitive(field.getGenericType().getTypeName())) { // IS LIST OF PRIMITIVES?
						int max = newList.size() > publishedList.size() ? newList.size() : publishedList.size();
						Diffs child = new Diffs("Line 81: " + field.getName());
						for(int i=0; i< max; i++) {
							if (publishedList.get(i) != null && newList.get(i) != null) { // Check for difference
								if(!publishedList.get(i).equals(newList.get(i))) {
									child.addChild(new Diffs(field.getName(), publishedList.get(i), newList.get(i), MODIFIED)); 
								}
							} else if (publishedList.get(i) != null) {// value has been deleted
								child.addChild(new Diffs(field.getName(), publishedList.get(i), null, DELETED)); 
								
							} else if (newList.get(i) != null) { // New value exists 
								child.addChild(new Diffs(field.getName(),null, newList.get(i), NEW)); 
							}
						}
						if (child.getChildren().size() > 0) { // ONLY ADD IF THERE ARE ACTUAL CHANGES
							parent.addChild(child);
						}
					} else { // ELSE (IS NOT LIST OF PRIMITIVES)
						int max = newList.size() > publishedList.size() ? newList.size() : publishedList.size();
						Diffs child = parent.getNewChild(field.getName());
						for (int i = 0; i < max; i++) {
							if (i > 0 ) {
								child = child.getNewChild("Line 102: " + field.getName());
							}
							Object oldList = publishedList.size() > i ? publishedList.get(i) : null;
							Object newList2 = newList.size() > i ? newList.get(i) : null;
							traverse(oldList, newList2, child);
						}
					}
				} else if (newList != null) {
					Diffs child = new Diffs("Line 112: " + field.getName());
					newList.forEach(item -> child.addChild(new Diffs(field.getDeclaringClass().getSimpleName(), null, item, NEW)));
					parent.addChild(child);
				} else if (publishedList != null) {
					Diffs child = new Diffs("Line 119: " + field.getName());
					publishedList.forEach(item -> child.addChild(new Diffs(field.getDeclaringClass().getSimpleName(), item, null , DELETED)));
					parent.addChild(child);
				} 	
			} else {
				Object existingValue = published != null ? PropertyUtils.getProperty(published, field.getName()) : null;
				Object newValue = newObject != null ? PropertyUtils.getProperty(newObject, field.getName()) : null;

				if (existingValue != null) {
					if (!isPrimitive(existingValue.getClass().getName())) {
						traverse(existingValue, newValue /* is null */, parent.getNewChild(field.getName()));
					} else if (newValue != null) { 
						if(!existingValue.equals(newValue)) {
							parent.addChild(new Diffs(field.getName(), existingValue, newValue, MODIFIED)); 
						}
					} else {// value has been deleted
						parent.addChild(new Diffs(field.getName(), existingValue, null, DELETED)); 
					} 
				} else if (newValue != null) { // New value exists 
					if (isPrimitive(newValue.getClass().getName())) {
						parent.addChild(new Diffs(field.getName(), null, newValue, NEW));
					} else  { // Call self
						traverse(existingValue /* is null */, newValue, parent.getNewChild(field.getName()));
					} 
				} 
			}
		}
	}

	private static boolean isPrimitive(String name) {
		return name.contains("java.lang");
	}
}

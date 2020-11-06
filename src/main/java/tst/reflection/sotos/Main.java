package tst.reflection.sotos;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.beanutils.PropertyUtils;

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
		AFA newAfa = new AFA("NEW", inners2); // AFA.INNER.List<InnerStrings>[0::InnerString2]
		                                      // AFA.INNER.List<InnerStrings>[1::InnerString5]
		newAfa.setPublished(false);
		List<Diffs> diffs = new ArrayList<>();
		
		// tst.reflection.sotos.AFA --> "AFA"
		

		try {
			traverse(publishedAfa, newAfa, diffs, 1);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception: " + e.getMessage());
		}
		diffs.forEach(System.out::println);
	}

	public static void traverse(Object published, Object newObject, List<Diffs> diffs, int depth) throws Exception {

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
						
						for(int i=0; i< max; i++) {
							if (publishedList.get(i) != null && newList.get(i) != null) { // Check for difference
									if(!publishedList.get(i).equals(newList.get(i))) {
										diffs.add(new Diffs(publishedList.get(i), newList.get(i), depth, field.getName(), field.getDeclaringClass().getSimpleName()));
									}
							} else if (publishedList.get(i) != null) {// value has been deleted
								diffs.add(new Diffs(publishedList.get(i), "HAS BEEN DELETED", depth, field.getName(), field.getDeclaringClass().getSimpleName()));
							} else if (newList.get(i) != null) { // New value exists 
								diffs.add(new Diffs("DID NOT EXIST", newList.get(i), depth, field.getName(), field.getDeclaringClass().getSimpleName()));
							}
						}
					} else { // ELSE (IS NOT LIST OF PRIMITIVES)
						int max = newList.size() > publishedList.size() ? newList.size() : publishedList.size();
						for (int i = 0; i < max; i++) {
							depth++;
							traverse(publishedList.size() > i ? publishedList.get(i) : null, newList.size() > i ? newList.get(i) : null, diffs, depth);
						   	 addMissingFields(diffs, depth, field.getDeclaringClass().getSimpleName());
							depth--;
						}
					}
				} else if (newList != null) {
					System.out.println("old is NULL");
					final int finalDepth = depth; 
					newList.forEach(item -> diffs.add(new Diffs("DID NOT EXIST", item, finalDepth, field.getName(), field.getDeclaringClass().getSimpleName())));
				} else if (publishedList != null) {
					System.out.println("new is NULL");
					final int finalDepth = depth;
					publishedList.forEach(item -> diffs.add(new Diffs(item, "HAS BEEN DELETED", finalDepth, field.getName(), field.getDeclaringClass().getSimpleName())));
				} else {
					// DO NOTHING
				}	
			} else {
				Object existingValue = published != null ? PropertyUtils.getProperty(published, field.getName()) : null;
				Object newValue = newObject != null ? PropertyUtils.getProperty(newObject, field.getName()) : null;

				if (existingValue != null) {
					if (!isPrimitive(existingValue.getClass().getName())) {
						depth++;
						traverse(existingValue, newValue /* is null */, diffs, depth);
					    	addMissingFields(diffs, depth, field.getDeclaringClass().getSimpleName());
						depth--;
					} else if (newValue != null) { // Check for differenceIF(
						if(!existingValue.equals(newValue)) {
							diffs.add(new Diffs(existingValue, newValue, depth, field.getName(), field.getDeclaringClass().getSimpleName()));
						}
					} else {// value has been deleted
						diffs.add(new Diffs(existingValue, "HAS BEEN DELETED", depth, field.getName(), field.getDeclaringClass().getSimpleName()));
					} 
				} else if (newValue != null) { // New value exists 
					if (isPrimitive(newValue.getClass().getName())) {
						diffs.add(new Diffs("DID NOT EXIST", newValue, depth, field.getName(), field.getDeclaringClass().getSimpleName()));
					} else  { // Call self
						depth++;
						traverse(existingValue /* is null */, newValue, diffs, depth);
					    	addMissingFields(diffs, depth, field.getDeclaringClass().getSimpleName());
						depth--;
					} 
				} 
			}
		}
		if(depth == 1) {
		    addMissingFields(diffs, depth, published != null ? published.getClass().getSimpleName() : newObject != null ? newObject.getClass().getSimpleName() : published.getClass().getSimpleName());
		}
	}

	public static boolean isPrimitive(String name) {
		return name.contains("java.lang") ? true : false;
	}

	private static void addMissingFields(List<Diffs> diffs, int depth, String clazz) {
	    if(depth == 1) {
		diffs.stream().filter(diff -> diff.getDepth() != depth).forEach(diff -> diff.getFields().add(clazz));
	    } else {
		diffs.stream().filter(diff -> diff.getDepth() == depth && (diff.getFields().size() > depth ? diff.getFields().size() - depth : depth - diff.getFields().size()) == 1).forEach(diff -> diff.getFields().add(clazz));
	    }
	}
}

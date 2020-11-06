//package tst.reflection.sotos;
//
//import java.lang.reflect.Field;
//import java.lang.reflect.InvocationTargetException;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//
//import org.apache.commons.beanutils.PropertyUtils;
//
//public class Main_old {
//	
//	public static void main(String[] args)
//			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
////		List<Inner> inners = new ArrayList<>();
////		inners.add(new Inner("INNER 1", new DepperInner("deep1", 2, false)));
////		inners.add(new Inner(null, new DepperInner("deep2", 5, true)));
////		inners.add(new Inner("INNER 3", new DepperInner("deep3", 5, true)));
////		List<Inner> inners2 = new ArrayList<>();
////		inners2.add(new Inner(null, new DepperInner("deep1", 1, true)));
////		inners2.add(new Inner("INNER 2", new DepperInner("deep2", 0, true)));
//		
//		List<String> innerStringList = new ArrayList<>();
//		innerStringList.add("InnerString1");
//		
//		List<String> innerStringList2 = new ArrayList<>();
//		innerStringList2.add("InnerString2");
//		innerStringList2.add("InnerString3");
//		List<Inner> inners = new ArrayList<>();
////		inners.add(new Inner("INNER 1", new DepperInner("deep1", 2, false), new ArrayList<String>()));
////		inners.add(new Inner("INNER 2", new DepperInner("deep2", 5, true), innerStringList));
////		List<Inner> inners2 = new ArrayList<>();
////		inners2.add(new Inner("INNER 1", new DepperInner("deep1", 2, false), innerStringList));
////		inners2.add(new Inner("INNER 2", new DepperInner("deep2", 5, true), innerStringList2));
////		inners2.add(new Inner("INNER 3", new DepperInner("deep3", 1, true), new ArrayList<String>()));
//		
//
//		AFA publishedAfa = new AFA("PUBLISHED", inners);
//		publishedAfa.setPublished(true);
//		//AFA newAfa = new AFA("NEW", inners2);
//		newAfa.setPublished(false);
//		List<Diffs> diffs = new ArrayList<>();
//		try {
//			traverse(publishedAfa, newAfa, diffs, 0);
//		} catch (Exception e) {
//			System.out.println(e.getMessage());
//		}
//		diffs.forEach(System.out::println);
//	}
//
//	public static void traverse(Object published, Object newObject, List<Diffs> diffs, int depth) throws Exception {
//		
//		Field[] fields;
//		List<Field> listOfFields = new ArrayList<>();
//
//		if (published != null) {
//			fields = published.getClass().getDeclaredFields();
//			Collections.addAll(listOfFields, published.getClass().getDeclaredFields());
//			listOfFields.forEach(field -> field.setAccessible(true));
//		} else if (newObject != null) {
//			fields = newObject.getClass().getDeclaredFields();
//			Collections.addAll(listOfFields, newObject.getClass().getDeclaredFields());
//			listOfFields.forEach(field -> field.setAccessible(true));
//		} else {
//			return;
//		}
//		
//		for (Field field : fields) {
//			field.setAccessible(true);
//			
//			Object objToTest = published != null ? published : newObject;
//
//			if (field.get(objToTest) instanceof List) {
//				List<?> newList = newObject !=null ? (List<?>) field.get(newObject) : null;
//				List<?> list = published != null ? (List<?>) field.get(published) : null;
//				
//				if (newList != null && !newList.isEmpty() && list != null && !list.isEmpty()) {
//					int max = newList.size() > list.size() ? newList.size() : list.size();
//					for (int i = 0; i < max; i++) {
//						depth++;
//						traverse(list.size() > i ? list.get(i) : null, newList.size() > i ? newList.get(i) : null, diffs, depth);
//						depth--;
//					}
//				}
//			} else {
//				Object existingValue = published != null ? PropertyUtils.getProperty(published, field.getName()) : null;
//				Object newValue = newObject != null ? PropertyUtils.getProperty(newObject, field.getName()) : null;
//
//				if (existingValue != null) {
//					boolean existingIsPrimitive = isPrimitive(existingValue);
//
//					if (existingIsPrimitive) {
//						if (newValue != null) { // Check for difference
//							diffs.add(new Diffs(existingValue, newValue, field.getName(), depth));
//						} else { // value has been deleted
//							// HAS BEEN DELETED
//							diffs.add(new Diffs(existingValue, "HAS BEEN DELETED", field.getName(), depth));
//						}
//					} else { // IS NOT PRIMITIVE --> CALL SELF
//						depth++;
//						traverse(existingValue, newValue, diffs, depth);
//						depth--;
//					}
//				} else if (newValue != null) { // New value exists 
//					if (!isPrimitive(newValue)) { // IS NOT PRIMITIVE --> CALL SELF
//						depth++;
//						traverse(existingValue, newValue, diffs, depth);
//						depth--;
//					} else {
//						diffs.add(new Diffs("DID NOT EXIST", newValue, field.getName(), depth));
//					}
//				}
//			}
//		}
//	}
//
//	public static boolean isPrimitive(Object obj) {
//		return obj.getClass().getName().startsWith("java.lang") ? true : false;
//	}
//}

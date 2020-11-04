import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IllegalAccessException, InvocationTargetException {
        List<Inner> inners = new ArrayList<>();
        inners.add(new Inner("INNER 1", new DepperInner("deep1", 2)));
        inners.add(new Inner("INNER 2", new DepperInner("deep3", 5)));
	List<Inner> inners2 = new ArrayList<>();
	inners2.add(new Inner("INNER 1", new DepperInner("deep1", 1)));
	inners2.add(new Inner("INNER 3", new DepperInner("deep6", 0)));

        AFA publishedAfa = new AFA("PUBLISHED", inners);
	AFA newAfa = new AFA("NEW", inners2);
	List<Diffs> diffs = new ArrayList<>();
	traverse(publishedAfa, newAfa, diffs, 0);
	diffs.forEach(System.out::println);
    }

    public static void traverse(Object published, Object newObject, List<Diffs> diffs, int depth)
	throws IllegalAccessException, InvocationTargetException {
        List<Field> newFields = new ArrayList<>();
	Field[] fields = published.getClass().getDeclaredFields();
	Collections.addAll(newFields, newObject.getClass().getDeclaredFields());
	newFields.forEach(field -> field.setAccessible(true));
	for(Field field : fields) {
	    field.setAccessible(true);
	    if(field.get(published) instanceof List) {
		List newList = (List)field.get(newObject);
		List list = (List)field.get(published);
	        if(newList != null && !newList.isEmpty() && list != null && !list.isEmpty()) {
	            int min = newList.size() > list.size() ? list.size() : newList.size();
	            for(int i = 0; i < min; i++) {
			depth++;
	                traverse(list.get(i), newList.get(i), diffs, depth);
	                depth--;
		    }
		}
	    } else {
		Method[] methods = published.getClass().getDeclaredMethods();
		Method[] newMethods = newObject.getClass().getDeclaredMethods();
		for(Method method : methods) {
		    Method newMethodFound;
		    if(method.getName().equalsIgnoreCase("get"+ field.getName())) {
		        for(Method newMethod : newMethods) {
		            if(method.getName().equalsIgnoreCase(newMethod.getName())) {
		                newMethodFound = newMethod;
				Object existingValue = method.invoke(published);
				Object newValue = newMethodFound.invoke(newObject);
				if(existingValue.getClass().getName().startsWith("java.lang") && newValue.getClass().getName().startsWith("java.lang")) {
				    if(!existingValue.equals(newValue)) {
					diffs.add(new Diffs(existingValue, newValue, field.getName(), depth));
				    }
				} else {
				    depth++;
				    traverse(existingValue, newValue, diffs, depth);
				    depth--;
				}
			    }
			}
			break;
		    }
		}
	    }
	}
    }
}

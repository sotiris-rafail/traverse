package test.reflection;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.commons.beanutils.*;

public class Main {


	public static void main(String[] args) {
	
		List<MyObject> myOBjList = new ArrayList<>();
		myOBjList.add(new MyObject("SAME"));
		myOBjList.add(new MyObject("DIFFERENT"));
		
		List<MyObject> myOBjList2 = new ArrayList<>();
		myOBjList2.add(new MyObject("SAME"));
		myOBjList2.add(new MyObject("DIFFERENT 2"));
		

		
		AFA oldAfa = new AFA("OLD", myOBjList);
		AFA newAfa = new AFA("NEW", myOBjList2);
		
		try {
			traverse(oldAfa);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public static void traverse(AFA myAfa) throws Exception {
		
		Field[] fields = myAfa.getClass().getDeclaredFields();

		
		System.out.println(PropertyUtils.getProperty(myAfa, fields[0].getName()));
		
	}
}


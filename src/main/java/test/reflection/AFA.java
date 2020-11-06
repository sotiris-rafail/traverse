package test.reflection;
import java.util.List;

public class AFA {

	private String text;
	private List<MyObject> myList;
	
	public AFA(String text, List<MyObject> myList) {
		this.text = text;
		this.myList = myList;
	}
	
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public List<MyObject> getMyList() {
		return myList;
	}
	public void setMyList(List<MyObject> myList) {
		this.myList = myList;
	}

	@Override
	public String toString() {
		return "AFA [text=" + text + ", myList=" + myList + "]";
	}
	
	
	
}

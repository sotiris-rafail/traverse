package test.reflection.objects;

import java.util.List;

public class AFA {

	private String id;
	private List<Inner> myList;
	private boolean published;

	public AFA(String text, List<Inner> myList) {
		this.id = text;
		this.myList = myList;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<Inner> getMyList() {
		return myList;
	}

	public void setMyList(List<Inner> myList) {
		this.myList = myList;
	}

	public boolean isPublished() {
		return published;
	}

	public void setPublished(boolean published) {
		this.published = published;
	}
}

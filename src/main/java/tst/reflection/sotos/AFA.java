package tst.reflection.sotos;

import java.util.List;

public class AFA {

	private String text;
	private List<Inner> myList;
	private boolean published;

	public AFA(String text, List<Inner> myList) {
		this.text = text;
		this.myList = myList;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
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

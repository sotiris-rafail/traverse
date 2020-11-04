import java.util.List;

public class AFA {

    private String text;
    private List<Inner> myList;

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
}

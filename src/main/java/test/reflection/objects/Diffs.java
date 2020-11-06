package test.reflection.objects;

import java.util.ArrayList;
import java.util.List;

public class Diffs {

	public enum STATUS { NEW, DELETED, MODIFIED }  
	
	private static final String TO_STRING = "{ name = %s, currentValue = %s, newValue = %s, Status = %s }";

	private final String name;
	private final List<Diffs> children;
	private Object currentValue; 
	private Object newValue;
	private STATUS status;
	
	/**
	 * Constructor for 'parent' objects
	 * 
	 * @param name
	 */
	public Diffs(String name) { 
		this.name = name;
		this.children = new ArrayList<>();
	}

	/**
	 * Constructor for 'Children'
	 * 
	 * @param name
	 * @param currentValue
	 * @param newValue
	 * @param status
	 */
	public Diffs(String name, Object currentValue, Object newValue, STATUS status) {
		this(name,  currentValue,  newValue, status, new ArrayList<>());
	}
	
	/**
	 * Constructor for 'Children'
	 * 
	 * @param name
	 * @param currentValue
	 * @param newValue
	 * @param status
	 * @param children
	 */
	public Diffs(String name, Object currentValue, Object newValue, STATUS status, List<Diffs> children) {
		this.name = name;
		this.currentValue = currentValue;
		this.newValue = newValue;
		this.status = status;
		this.children = children;
	}

	public Object getCurrentValue() {
		return currentValue;
	}

	public void setCurrentValue(Object currentValue) {
		this.currentValue = currentValue;
	}

	public Object getNewValue() {
		return newValue;
	}

	public void setNewValue(Object newValue) {
		this.newValue = newValue;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(String.format(TO_STRING, name, currentValue, newValue, getStatus(status)));
		builder.append("\n");
		children.forEach(child -> {
			builder.append(child.toString());
			child.getChildren().forEach(innerChild -> innerChild.toString());
		});
		return builder.toString();
	}

	public List<Diffs> getChildren() {
		return children;
	}

	public void addChild(final Diffs child) {
		this.children.add(child);
	}

	public Diffs getNewChild(String name) { 
		Diffs newChild = new Diffs(name);
		this.children.add(newChild);
		return newChild;
	}
	
	private Object getStatus(STATUS status) {
		return status != null ? status.toString() : "";
	}
}

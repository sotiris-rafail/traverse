package test.reflection.result;

import java.util.ArrayList;
import java.util.List;

public class Diffs {

	public enum STATUS { NO_CHANGE, NEW, DELETED, MODIFIED, PARENT_DIFF }  
	
	private static final String TO_STRING_PARENT    = "[ %s ]";
	private static final String TO_STRING_NO_CHANGE = "[ %s, Old Value = %s, New Value = %s, NO_CHANGE ]";
	private static final String TO_STRING_MODIFIED  = "[ %s, Old Value = %s, New Value = %s, MODIFIED ]";
	private static final String TO_STRING_DELETED   = "[ %s, Old Value = %s, DELETED ]";
	private static final String TO_STRING_NEW       = "[ %s, newValue = %s, NEW ]";

	private final String name;
	private final List<Diffs> children = new ArrayList<>();
	private final Object currentValue; 
	private final Object newValue;
	private final STATUS status;
	
	/**
	 * Constructor for 'parent' objects
	 * 
	 * @param name
	 */
	public Diffs(String name) { 
		this (name, null, null, STATUS.PARENT_DIFF);
	}

	/**
	 * Constructor for a 'Child' object (that contains actual values)
	 * 
	 * @param name
	 * @param currentValue
	 * @param newValue
	 * @param status
	 */
	public Diffs(String name, Object currentValue, Object newValue, STATUS status) {
		this.name = name;
		this.currentValue = currentValue;
		this.newValue = newValue;
		this.status = status;
	}

	public String getCurrentValue() {
		return currentValue !=null ? currentValue.toString() : "";
	}

	public String getNewValue() {
		return newValue !=null ? newValue.toString() : "";
	}
	
	public String getStatus() {
		return status.toString();
	}
	
	public void addChild(final Diffs child) {
		this.children.add(child);
	}

	public Diffs getNewChild(String name) { 
		Diffs newChild = new Diffs(name);
		this.children.add(newChild);
		return newChild;
	}
	
	public void removeLastChild() {
		this.children.remove(children.size() - 1);
	}
	
	public boolean hasChanges() {
		return this.children.stream().anyMatch(child -> child.status != STATUS.NO_CHANGE);
	}
	
	@Override
	public String toString() {
		return this.toString("\t");
	}
	
	private String toString(String tab) {
		StringBuilder builder = new StringBuilder();
		switch (status) {
			case PARENT_DIFF:
				builder.append(String.format(TO_STRING_PARENT, name));
				break;
			case MODIFIED:
				builder.append(String.format(TO_STRING_MODIFIED, name, currentValue, newValue));
				break;
			case DELETED:
				builder.append(String.format(TO_STRING_DELETED, name, currentValue));
				break;
			case NEW:
				builder.append(String.format(TO_STRING_NEW, name,  newValue));
				break;
			default:
				builder.append(String.format(TO_STRING_NO_CHANGE, name, currentValue, newValue));
		}
		children.forEach(child -> builder.append("\n"+ tab + child.toString("\t"+tab)));
		return builder.toString();
	}
}

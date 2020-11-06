package tst.reflection.sotos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Diffs {
	
	
	private static final String TO_STRING = "Diffs { fields=%s, currentValue=%s, newValue=%s, depth=%s }";

	private List<String> fields;
	private Object currentValue;
	private Object newValue;
	private int depth;

	public Diffs(Object currentValue, Object newValue, int depth, String... fields) {
		this.currentValue = currentValue;
		this.newValue = newValue;
		this.fields = convertToList(fields);
	    	this.depth = depth;
	}

	public static List<String> convertToList(String... fields) {
	    List<String> tmpList = new ArrayList<>();
	    Collections.addAll(tmpList, fields);
	    return tmpList;
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

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	public void addNewField(String field, int depth) {
	    List<String> tmpField = new ArrayList<>(fields);
	    tmpField.add(field);
	    fields = tmpField;
	}

    public List<String> getFields() {
	return fields;
    }

    public void setFields(List<String> fields) {
	this.fields = fields;
    }

    @Override
	public String toString() {
		return String.format(TO_STRING, "[" + String.join(",", fields) + "]", currentValue, newValue, depth);
	}
}

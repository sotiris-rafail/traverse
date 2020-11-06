package tst.reflection.sotos;

import java.util.Arrays;
import java.util.List;

public class Diffs {
	
	
	private static final String TO_STRING = "Diffs { fields=%s, currentValue=%s, newValue=%s, depth=%s }";

	private List<String> fields;
	private Object currentValue;
	private Object newValue;
	private int depth;

	public Diffs(Object currentValue, Object newValue, int depth, String... fields) {
		this.currentValue = currentValue;
		this.newValue = newValue;
		this.fields = Arrays.asList(fields);
		this.depth = depth;
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

	@Override
	public String toString() {
		return String.format(TO_STRING, "[" + String.join(",", fields) + "]", currentValue, newValue, depth);
	}
}

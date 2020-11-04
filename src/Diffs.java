public class Diffs {

    private String field;
    private Object currentValue;
    private Object newValue;
    private int depth;

    public Diffs(Object currentValue, Object newValue, String field, int depth) {
	this.currentValue = currentValue;
	this.newValue = newValue;
	this.field = field;
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

    public String getField() {
	return field;
    }

    public void setField(String field) {
	this.field = field;
    }

    public int getDepth() {
	return depth;
    }

    public void setDepth(int depth) {
	this.depth = depth;
    }

    @Override
    public String toString() {
	return "Diffs{" +
	    "field='" + field +
	    ", currentValue=" + currentValue +
	    ", newValue=" + newValue +
	    ", depth=" + depth +
	    '}';
    }
}

package test.reflection;

public class MyObject {

	private String myObjectString;
	
	public MyObject(String myObjString) {
		this.myObjectString = myObjString;
	}

	public String getMyObjectString() {
		return myObjectString;
	}

	public void setMyObjectString(String myObjectString) {
		this.myObjectString = myObjectString;
	}

	@Override
	public String toString() {
		return "MyObject [myObjectString=" + myObjectString + "]";
	} 
	
	
}

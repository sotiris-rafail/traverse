public class Inner {

    private String innerText;
    private DepperInner depperInner;

    public Inner(String innerText, DepperInner depperInner) {
	this.innerText = innerText;
	this.depperInner = depperInner;
    }

    public String getInnerText() {
	return innerText;
    }

    public void setInnerText(String innerText) {
	this.innerText = innerText;
    }

    public DepperInner getDepperInner() {
	return depperInner;
    }

    public void setDepperInner(DepperInner depperInner) {
	this.depperInner = depperInner;
    }
}

package tst.reflection.sotos;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Inner implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private static final Log logger = LogFactory.getLog(Inner.class);
	
	private String innerText;
	private DepperInner depperInner;
	private List<String> innerStrings;

	public Inner(String innerText, DepperInner depperInner, String... strings) {
		this.innerText = innerText;
		this.depperInner = depperInner;
		this.innerStrings = Arrays.asList(strings);
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

	public List<String> getInnerStrings() {
		return innerStrings;
	}

	public void setInnerStrings(List<String> innerStrings) {
		this.innerStrings = innerStrings;
	}
}

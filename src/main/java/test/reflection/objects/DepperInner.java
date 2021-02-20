package test.reflection.objects;

public class DepperInner {

	private String depper;
	private int muchDepper;
	private boolean depperPublished;

	public DepperInner(String depper, int muchDepper, boolean depperPublished) {
		this.depper = depper;
		this.muchDepper = muchDepper;
		this.depperPublished = depperPublished;
	}

	public String getDepper() {
		return depper;
	}

	public void setDepper(String depper) {
		this.depper = depper;
	}

	public int getMuchDepper() {
		return muchDepper;
	}

	public void setMuchDepper(int muchDepper) {
		this.muchDepper = muchDepper;
	}

	public boolean isDepperPublished() {
		return depperPublished;
	}

	public void setDepperPublished(boolean depperPublished) {
		this.depperPublished = depperPublished;
	}

}

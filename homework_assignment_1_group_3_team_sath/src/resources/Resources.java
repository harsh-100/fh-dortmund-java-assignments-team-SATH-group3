package resources;

public abstract class Resources {

	private String id;
	private String name;

	public Resources(String id, String name) {
		this.id = id;
		this.setName(name);
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}

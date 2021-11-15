
public class User {
	private String name;
	private int id;
	/**
	 * @return
	 */
	public String getName() {
		return name;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return id + " " + name;
	}
	/**
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id
	 */
	public void setId(int id) {
		this.id = id;
	}
	/**
	 * @param name
	 * @param id
	 */
	public User(String name, int id) {
		super();
		this.name = name;
		this.id = id;
	}
}

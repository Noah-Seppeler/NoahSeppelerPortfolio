
public class CustomerCount {
	private int count;
	private int id;
	
	/**
	 * @param count
	 * @param id
	 */
	public CustomerCount(int count, int id) {
		super();
		this.count = count;
		this.id = id;
	}
	/**
	 * @return
	 */
	public int getCount() {
		return count;
	}
	/**
	 * @param count
	 */
	public void setCount(int count) {
		this.count = count;
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
}

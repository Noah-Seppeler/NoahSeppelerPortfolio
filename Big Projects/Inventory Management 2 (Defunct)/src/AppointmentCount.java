
public class AppointmentCount {
	private int count;
	private String type;
	private String month;
	
	/**
	 * @param count
	 * @param type
	 * @param month
	 */
	public AppointmentCount(int count, String type, String month) {
		super();
		this.count = count;
		this.type = type;
		this.month = month;
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
	public String getType() {
		return type;
	}
	/**
	 * @param type
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * @return
	 */
	public String getMonth() {
		return month;
	}
	/**
	 * @param month
	 */
	public void setMonth(String month) {
		this.month = month;
	}
	
	
}

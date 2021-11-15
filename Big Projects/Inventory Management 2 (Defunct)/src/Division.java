/** this class defines a Division object so that it is easier for the Customer object to store information about a customer's division
 * 
 * @author noahs
 *
 */

public class Division {
	private int id;
	private String name;
	
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
	 * @return
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @param id
	 * @param name
	 */
	public Division(int id, String name) {
		super();
		this.id = id;
		this.name = name;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return name;
	}
	
	
}

/**This class is used to define an object named Country so that the customers have a easier time storing county information
 * 
 * @author Noah Seppeler
 *
 */

public class Country {
	private String name;
	private int id;
	
	
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
	public Country(String name, int id) {
		this.name = name;
		this.id = id;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return name;
	}
	
	
}

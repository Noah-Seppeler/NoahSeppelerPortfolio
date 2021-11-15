import java.time.LocalDateTime;

/**This class defines a Customer object. this is so that all customer information is organized and easily accessible for tableviews
 * 
 * @author Noah Seppeler
 *
 */

public class Customer {
	private int id;
	private String name;
	private String address;
	private String postalCode;
	private String phone;
	private LocalDateTime createDate;
	private String createdBy;
	private LocalDateTime lastUpdate;
	private String lastUpdatedBy;
	private int divisionID;
	
	/**
	 * @param name
	 * @param address
	 * @param postalCode
	 * @param phone
	 * @param createdBy
	 * @param lastUpdatedBy
	 * @param divisionID
	 */
	public Customer(String name, String address, String postalCode, String phone, String createdBy,
			String lastUpdatedBy, int divisionID) {
		
		this.name = name;
		this.address = address;
		this.postalCode = postalCode;
		this.phone = phone;
		this.createDate = LocalDateTime.now();
		this.createdBy = createdBy;
		this.lastUpdate = LocalDateTime.now();
		this.lastUpdatedBy = lastUpdatedBy;
		this.divisionID = divisionID;
	}
	
	/**pushes customer into database
	 * 
	 */
	public void pushCustomer() {
		QueryDriver.createCustomer(this.name, this.address, this.postalCode, this.phone, this.createdBy, this.lastUpdatedBy, this.divisionID);
		id = QueryDriver.getCustomerID(name, phone);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return id+" "+name;
	}

	/**
	 * @param id
	 */
	public void setID(int id) {
		this.id = id;
	}

	/**returns createDate
	 * @return
	 */
	public LocalDateTime getCreateDate() {
		return createDate;
	}

	/**returns id
	 * @return
	 */
	public int getId() {
		return id;
	}

	/**returns name
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**returns address
	 * @return
	 */
	public String getAddress() {
		return address;
	}

	/**returns postalCode
	 * @return
	 */
	public String getPostalCode() {
		return postalCode;
	}

	/**returns phone
	 * @return
	 */
	public String getPhone() {
		return phone;
	}

	/**returns createdBy
	 * @return
	 */
	public String getCreatedBy() {
		return createdBy;
	}

	/**returns lastUpdatedBy
	 * @return
	 */
	public String getLastUpdatedBy() {
		return lastUpdatedBy;
	}

	/**returns divisionID
	 * @return
	 */
	public int getDivisionID() {
		return divisionID;
	}

	/**
	 * @param createDate
	 */
	public void setCreateDate(LocalDateTime createDate) {
		this.createDate = createDate;
	}

	/**returns lastUpdate
	 * @return
	 */
	public LocalDateTime getLastUpdate() {
		return lastUpdate;
	}

	/**
	 * @param lastUpdate
	 */
	public void setLastUpdate(LocalDateTime lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	/**gets customer id from the database using the name and phone number
	 * 
	 */
	public void getIDFromDatabase() {
		id = QueryDriver.getCustomerID(name, phone);
		
	}
	
}

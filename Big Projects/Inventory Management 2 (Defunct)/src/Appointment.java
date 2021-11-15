import java.time.LocalDateTime;

/**This class defines an Appointment object. this is so that all appointment information is organized and easily accessible for tableviews
 * 
 * @author Noah Seppeler
 *
 */

public class Appointment {
	private int id;
	private String title;
	private String description;
	private String location;
	private String type;
	private Contact contact;
	private LocalDateTime startTime;
	private LocalDateTime endTime;
	private LocalDateTime createDate;
	private String createdBy;
	private LocalDateTime lastUpdate;
	private String lastUpdatedBy;
	private User user;
	private Customer customer;
	
	
	
	/**
	 * @param title
	 * @param description
	 * @param location
	 * @param type
	 * @param contact
	 * @param startTime
	 * @param endTime
	 * @param createdBy
	 * @param lastUpdatedBy
	 * @param user
	 * @param customer
	 */
	public Appointment(String title, String description, String location, String type, Contact contact, LocalDateTime startTime,
			LocalDateTime endTime, String createdBy,
			String lastUpdatedBy, User user, Customer customer) {
		this.title = title;
		this.description = description;
		this.location = location;
		this.type = type;
		this.contact = contact;
		this.startTime = startTime;
		this.endTime = endTime;
		this.createdBy = createdBy;
		this.lastUpdatedBy = lastUpdatedBy;
		this.user = user;
		this.customer = customer;
		
		this.customer.getIDFromDatabase();
		
	}
	
	/**pushes appointment to database 
	 * 
	 */
	public void pushAppointment() {
		QueryDriver.createAppointment(this.title, this.description, this.location, this.type, this.contact, this.startTime, this.endTime,
				this.createdBy, this.lastUpdatedBy, this.user, this.customer);
		id = QueryDriver.getAppointmentID(title, description);
	}
	
	/**returns id
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
	/**returns startTime
	 * @return
	 */
	public LocalDateTime getStartTime() {
		return startTime;
	}
	/**
	 * @param startTime
	 */
	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}
	/**returns endTime
	 * @return
	 */
	public LocalDateTime getEndTime() {
		return endTime;
	}
	/**
	 * @param endTime
	 */
	public void setEndTime(LocalDateTime endTime) {
		this.endTime = endTime;
	}
	/**returns createDate
	 * @return
	 */
	public LocalDateTime getCreateDate() {
		return createDate;
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
	/**returns user
	 * @return
	 */
	public User getUser() {
		return user;
	}

	/**
	 * @param user
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**returns customerId
	 * @return
	 */
	public int getCustID() {
		return customer.getId();
	}
	/**
	 * @return
	 */
	public Customer getCustomer() {
		return customer;
	}
	/**returns contactID
	 * @return
	 */
	public int getContactID() {
		return this.contact.getId();
	}
	/**
	 * @return
	 */
	public Contact getContact() {
		return contact;
	}
	/**returns title
	 * @return
	 */
	public String getTitle() {
		return title;
	}
	/**returns description
	 * @return
	 */
	public String getDescription() {
		return description;
	}
	/**returns location
	 * @return
	 */
	public String getLocation() {
		return location;
	}
	/**returns type
	 * @return
	 */
	public String getType() {
		return type;
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
	
	
	
}

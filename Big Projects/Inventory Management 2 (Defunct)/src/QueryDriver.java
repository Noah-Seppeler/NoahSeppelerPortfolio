import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.TimeZone;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**this is the part of the application that interfaces with the sql database. There is no other class in application that is connected with the database.
 * It all must go through here. this way it is consolidated for easier debugging
 * 
 * @author noahs
 *
 */

public class QueryDriver {
	
	private static Connection myCon;
	
	/**connects to the database
	 * @throws SQLException
	 */
	public static void connect() throws SQLException {
		myCon = DriverManager.getConnection("jdbc:mysql://wgudb.ucertify.com:3306/WJ08uDV", "U08uDV", "53689398324");
	}
	
	/**validates the login username and password
	 * @param testUsername
	 * @param testPassword
	 * @return
	 */
	public static boolean validateLogin(String testUsername, String testPassword) {	
		String usernameString = "";
		String passwordString = "";
		try {
			Statement myStmt = myCon.createStatement();
			
			ResultSet myRs = myStmt.executeQuery("select * from users where User_Name = '"+testUsername+"'");
			
			while(myRs.next()){
				usernameString = myRs.getString("User_Name");
				passwordString = myRs.getString("Password");
			}
		}catch(Exception ex) {
			System.out.println("Statement error");
		}
		
		if(usernameString.equals(testUsername) && passwordString.equals(testPassword)) {
			return true;
		}else {
			return false;
		}
	}
	
	/**creates customer in database
	 * @param name
	 * @param address
	 * @param postalCode
	 * @param phone
	 * @param createdBy
	 * @param lastUpdatedBy
	 * @param divisionID
	 */
	public static void createCustomer(String name, String address, String postalCode, String phone, String createdBy, String lastUpdatedBy, int divisionID) {
		String queryString = "insert into customers(Customer_Name, Address, Postal_Code, Phone, Created_By, Last_Updated_By, Division_ID) VALUES "
				+ "(?, ?, ?, ?, ?, ?, ?);";
		
		try {
			PreparedStatement myStmt = myCon.prepareStatement(queryString);
			
			myStmt.setString(1, name);
			myStmt.setString(2, address);
			myStmt.setString(3, postalCode);
			myStmt.setString(4, phone);
			myStmt.setString(5, createdBy);
			myStmt.setString(6, lastUpdatedBy);
			myStmt.setInt(7, divisionID);
			
			myStmt.execute();
		}catch(Exception ex){
			System.out.println("statement error");
			ex.printStackTrace();
		}
	}

	/**gets all customers from data base
	 * @return
	 */
	public static ObservableList<Customer> getAllCustomersFromDatabase() {
		ObservableList<Customer> customerList = FXCollections.observableArrayList();
		try {
			Statement myStmt = myCon.createStatement();
			
			ResultSet myRs = myStmt.executeQuery("select * from customers");
			
			while(myRs.next()){
				Customer c = new Customer(myRs.getString("Customer_Name"), myRs.getString("Address"), 
						myRs.getString("Postal_Code"), myRs.getString("Phone"), myRs.getString("Created_By"), myRs.getString("Last_Updated_By"), myRs.getInt("Division_ID"));
				c.setID(myRs.getInt("Customer_ID"));
				LocalDateTime createDate = myRs.getTimestamp("Create_Date").toLocalDateTime().atZone(ZoneId.of("UTC"))
						.withZoneSameInstant(TimeZone.getDefault().toZoneId()).toLocalDateTime();
				LocalDateTime lastUpdate = myRs.getTimestamp("Last_Update").toLocalDateTime().atZone(ZoneId.of("UTC"))
						.withZoneSameInstant(TimeZone.getDefault().toZoneId()).toLocalDateTime();
				
				c.setCreateDate(createDate);
				c.setLastUpdate(lastUpdate);
				customerList.add(c);
			}
		}catch(Exception ex) {
			System.out.println("Statement Error");
			ex.printStackTrace();
		}
		return customerList;
	}
	
	/**gets customerID using their name and phone number
	 * @param name
	 * @param phone
	 * @return
	 */
	public static int getCustomerID(String name, String phone) {
		int id = -1;
		try {
			Statement myStmt = myCon.createStatement();
			
			ResultSet myRs = myStmt.executeQuery("select Customer_ID from customers where Customer_Name = '" + name + "' AND Phone = '" + phone + "'");
			
			while(myRs.next()){
				id = myRs.getInt("Customer_ID");
			}
		}catch(Exception ex) {
			System.out.println("Statement Error");
		}
		
		return id;
	}

	/**gets appointment ID using title and description
	 * @param title
	 * @param description
	 * @return
	 */
	public static int getAppointmentID(String title, String description) {
		int id = -1;
		try {
			Statement myStmt = myCon.createStatement();
			
			ResultSet myRs = myStmt.executeQuery("select Appointment_ID from appointments where Title = '"
			+ title + "' AND Description = '" + description + "'");
			
			while(myRs.next()){
				id = myRs.getInt("Appointment_ID");
			}
		}catch(Exception ex) {
			System.out.println("Statement Error");
			ex.printStackTrace();
		}
		
		return id;
	}

	/**creates appointment in database
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
	public static void createAppointment(String title, String description, String location, String type,
			Contact contact, LocalDateTime startTime, LocalDateTime endTime, String createdBy, String lastUpdatedBy,
			User user, Customer customer) {
		
		String queryString = "insert into appointments(Title, Description, Location, Type, Start, End, Created_By, Last_Updated_By, Customer_ID, User_ID, Contact_ID) VALUES "
				+ "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
		
		try {
			PreparedStatement myStmt = myCon.prepareStatement(queryString);
			
			myStmt.setString(1, title);
			myStmt.setString(2, description);
			myStmt.setString(3, location);
			myStmt.setString(4, type);
			myStmt.setTimestamp(5, Timestamp.valueOf(startTime));
			myStmt.setTimestamp(6, Timestamp.valueOf(endTime));
			myStmt.setString(7, createdBy);
			myStmt.setString(8, lastUpdatedBy);
			myStmt.setInt(9, customer.getId());
			myStmt.setInt(10, user.getId());
			myStmt.setInt(11, contact.getId());
			
			myStmt.execute();
		}catch(Exception ex){
			System.out.println("statement error");
			ex.printStackTrace();
		}
		
	}

	/**gets all countries from database
	 * @return
	 */
	public static ObservableList<Country> getAllCountries() {
		ObservableList<Country> list = FXCollections.observableArrayList();
		try {
			Statement myStmt = myCon.createStatement();
			
			ResultSet myRs = myStmt.executeQuery("select * from countries where Country = 'United States' OR Country = 'Canada' OR Country = 'United Kingdom'");
			
			while(myRs.next()){
				Country c = new Country(myRs.getString("Country"), myRs.getInt("Country_ID"));
				
				list.add(c);
			}
		}catch(Exception ex) {
			System.out.println("statement error");
		}
		
		return list;
	}

	/**gets all divisions for a specified country
	 * @param selectedItem
	 * @return
	 */
	public static ObservableList<Division> getDivisions(Country selectedItem) {
		ObservableList<Division> list = FXCollections.observableArrayList();
		try {
			Statement myStmt = myCon.createStatement();
			
			ResultSet myRs = myStmt.executeQuery("select * from first_level_divisions where Country_ID = '"+selectedItem.getId()+"'");
			
			while(myRs.next()){
				Division d = new Division(myRs.getInt("Division_ID"), myRs.getString("Division"));
				
				list.add(d);
			}
		}catch(Exception ex) {
			System.out.println("statement error");
			ex.printStackTrace();
		}
		
		return list;
	}

	/**deletes a customer in a database
	 * @param selectedItem
	 * @throws SQLException
	 */
	public static void deleteCustomer(Customer selectedItem) throws SQLException {
			Statement myStmt = myCon.createStatement();
			
			myStmt.execute("DELETE FROM customers WHERE Customer_ID = '"+selectedItem.getId()+"'");
	}

	/**modifies a customer in the database
	 * @param id
	 * @param customerName
	 * @param customerAddress
	 * @param customerPostCode
	 * @param customerPhone
	 * @param customerDivision
	 */
	public static void modifyCustomer(int id, String customerName, String customerAddress, String customerPostCode,
			String customerPhone, int customerDivision) {
		
		String queryString = "update customers set Customer_Name = ?, Address = ?, Postal_Code = ?, Phone = ?, Division_ID = ?, Last_Update = NOW() "
				+ "where Customer_ID = ?";
		
		
		
		try {
			PreparedStatement myStmt = myCon.prepareStatement(queryString);
			
			myStmt.setString(1, customerName);
			myStmt.setString(2, customerAddress);
			myStmt.setString(3, customerPostCode);
			myStmt.setString(4, customerPhone);
			myStmt.setInt(5, customerDivision);
			myStmt.setInt(6, id);
			
			myStmt.executeUpdate();
		}catch(Exception ex){
			System.out.println("statement error");
			ex.printStackTrace();
		}
		
	}

	/**deletes appointment from the data base
	 * @param selectedItem
	 */
	public static void deleteAppointment(Appointment selectedItem) {
		try {
			Statement myStmt = myCon.createStatement();
			
			myStmt.execute("DELETE FROM appointments WHERE Appointment_ID = '"+selectedItem.getId()+"'");
		}catch(Exception ex) {
			System.out.println("statement error");
			ex.printStackTrace();
		}
		
	}

	/**gets all appointments from a database given a string that says what to sort by (week or month)
	 * @param sortTime
	 * @return
	 */
	public static ObservableList<Appointment> getAllAppointmentsFromDatabase(String sortTime) {
		ObservableList<Appointment> appointmentList = FXCollections.observableArrayList();
		try {
			Statement myStmt = myCon.createStatement();
			ResultSet myRs;
			if(sortTime.equals("month")) {
				myRs = myStmt.executeQuery("select * from appointments WHERE NOW() > DATE_ADD(Start, INTERVAL -1 MONTH)");
			}else {
				myRs = myStmt.executeQuery("select * from appointments WHERE NOW() > DATE_ADD(Start, INTERVAL -1 WEEK)");
			}
			
			while(myRs.next()){
				String title = myRs.getString("Title");
				String description = myRs.getString("Description");
				String location = myRs.getString("Location");
				String type = myRs.getString("Type");
				Contact contact = QueryDriver.getContact(myRs.getInt("Contact_ID"));
				LocalDateTime start = myRs.getTimestamp("Start").toLocalDateTime().atZone(ZoneId.of("UTC"))
						.withZoneSameInstant(TimeZone.getDefault().toZoneId()).toLocalDateTime();
				LocalDateTime end = myRs.getTimestamp("End").toLocalDateTime().atZone(ZoneId.of("UTC"))
						.withZoneSameInstant(TimeZone.getDefault().toZoneId()).toLocalDateTime();
				String createdBy = myRs.getString("Created_By");
				String updatedBy = myRs.getString("Last_Updated_By");
				User user = QueryDriver.getUser(myRs.getInt("User_ID"));
				Customer customer = QueryDriver.getCustomer(myRs.getInt("Customer_ID"));
				Appointment a = new Appointment(title, description, location, type, contact, start, end, createdBy, updatedBy, user, customer);
				a.setId(myRs.getInt("Appointment_ID"));
				
				appointmentList.add(a);
			}
		}catch(Exception ex) {
			System.out.println("Statement Error");
			ex.printStackTrace();
		}
		return appointmentList;
	}

	private static User getUser(int userId) {
		User u = null;
		try {
			Statement myStmt = myCon.createStatement();
			
			ResultSet myRs = myStmt.executeQuery("select * from users where User_ID = '"+ userId+"'");
			
			while(myRs.next()){
				u = new User(myRs.getString("User_Name"), myRs.getInt("User_ID"));
			}
		}catch(Exception ex) {
			System.out.println("Statement Error");
			ex.printStackTrace();
		}
		
		return u;
	}

	private static Customer getCustomer(int custId) {
		Customer c = null;
		try {
			Statement myStmt = myCon.createStatement();
			
			ResultSet myRs = myStmt.executeQuery("select * from customers where Customer_ID = '"+ custId+"'");
			
			while(myRs.next()){
				c = new Customer(myRs.getString("Customer_Name"), myRs.getString("Address"),
						myRs.getString("Postal_Code"), myRs.getString("Phone"), myRs.getString("Created_By"), myRs.getString("Last_Updated_By"), myRs.getInt("Division_ID"));
			}
		}catch(Exception ex) {
			System.out.println("Statement Error");
		}
		
		return c;
	}

	private static Contact getContact(int conId) {
		Contact c = null;
		try {
			Statement myStmt = myCon.createStatement();
			
			ResultSet myRs = myStmt.executeQuery("select * from contacts where Contact_ID = '"+ conId+"'");
			
			while(myRs.next()){
				c = new Contact(conId, myRs.getString("Contact_Name"), myRs.getString("Email"));
			}
		}catch(Exception ex) {
			System.out.println("Statement Error");
		}
		
		return c;
	}

	/**gets all contacts from the data base
	 * @return
	 */
	public static ObservableList<Contact> getAllContacts() {
		ObservableList<Contact> contactList = FXCollections.observableArrayList();
		try {
			Statement myStmt = myCon.createStatement();
			
			ResultSet myRs = myStmt.executeQuery("select * from contacts");
			
			while(myRs.next()){
				Contact c = new Contact(myRs.getInt("Contact_ID"), myRs.getString("Contact_Name"), myRs.getString("Email"));

				contactList.add(c);
			}
		}catch(Exception ex) {
			System.out.println("Statement Error");
			ex.printStackTrace();
		}
		return contactList;
	}

	/**gets all appointment times from the data base
	 * @return
	 */
	public static ArrayList<LocalDateTime> getAllAppoinmentTimes() {
		ArrayList<LocalDateTime> times = new ArrayList<LocalDateTime>();
		
		try {
			Statement myStmt = myCon.createStatement();
			
			ResultSet myRs = myStmt.executeQuery("select * from appointments");
			
			while(myRs.next()){
				LocalDateTime s = myRs.getTimestamp("Start").toLocalDateTime();
				LocalDateTime e = myRs.getTimestamp("End").toLocalDateTime();
				
				times.add(s);
				times.add(e);
			}
		}catch(Exception ex) {
			System.out.println("Statement Error");
			ex.printStackTrace();
		}
		
		return times;
	}

	/**modifies an appointment in the data base
	 * @param id
	 * @param title
	 * @param type
	 * @param description
	 * @param location
	 * @param contact
	 * @param customer
	 * @param startTime
	 * @param endTime
	 * @param user
	 */
	public static void ModifyAppointment(int id, String title, String type, String description, String location,
			Contact contact, Customer customer, LocalDateTime startTime, LocalDateTime endTime, User user) {
		String queryString = "update appointments set Title = ?, Description = ?, Location = ?, Type = ?, "
				+ "Start = ?, End = ?, Customer_ID = ?, Contact_ID = ?, User_ID = ?, Last_Update = NOW() "
				+ "where Appointment_ID = ?";
		
		try {
			PreparedStatement myStmt = myCon.prepareStatement(queryString);
			
			myStmt.setString(1, title);
			myStmt.setString(2, description);
			myStmt.setString(3, location);
			myStmt.setString(4, type);
			myStmt.setTimestamp(5, Timestamp.valueOf(startTime));
			myStmt.setTimestamp(6, Timestamp.valueOf(endTime));
			myStmt.setInt(7, customer.getId());
			myStmt.setInt(8, contact.getId());
			myStmt.setInt(9, user.getId());
			myStmt.setInt(10, id);
			
			myStmt.execute();
		}catch(Exception ex){
			System.out.println("statement error");
			ex.printStackTrace();
		}
		
	}

	/**gets all appointments based on a given contact
	 * @param selectedContact
	 * @return
	 */
	public static ObservableList<Appointment> getAllAppointments(Contact selectedContact) {
		ObservableList<Appointment> appointmentList = FXCollections.observableArrayList();
		try {
			Statement myStmt = myCon.createStatement();
			ResultSet myRs;
			
			myRs = myStmt.executeQuery("select * from appointments WHERE Contact_ID = "+selectedContact.getId()+";");

			
			while(myRs.next()){
				String title = myRs.getString("Title");
				String description = myRs.getString("Description");
				String location = myRs.getString("Location");
				String type = myRs.getString("Type");
				Contact contact = QueryDriver.getContact(myRs.getInt("Contact_ID"));
				LocalDateTime start = myRs.getTimestamp("Start").toLocalDateTime().atZone(ZoneId.of("UTC"))
						.withZoneSameInstant(TimeZone.getDefault().toZoneId()).toLocalDateTime();
				LocalDateTime end = myRs.getTimestamp("End").toLocalDateTime().atZone(ZoneId.of("UTC"))
						.withZoneSameInstant(TimeZone.getDefault().toZoneId()).toLocalDateTime();
				String createdBy = myRs.getString("Created_By");
				String updatedBy = myRs.getString("Last_Updated_By");
				User user = QueryDriver.getUser(myRs.getInt("User_ID"));
				Customer customer = QueryDriver.getCustomer(myRs.getInt("Customer_ID"));
				Appointment a = new Appointment(title, description, location, type, contact, start, end, createdBy, updatedBy, user, customer);
				a.setId(myRs.getInt("Appointment_ID"));
				
				appointmentList.add(a);
			}
		}catch(Exception ex) {
			System.out.println("Statement Error");
			ex.printStackTrace();
		}
		return appointmentList;
	}
	
	/**gets number of appointments grouped by he type and the month
	 * @return
	 */
	public static ObservableList<AppointmentCount> getNumberAppointments(){
		ObservableList<AppointmentCount> appointmentList = FXCollections.observableArrayList();
		try {
			Statement myStmt = myCon.createStatement();
			ResultSet myRs;
			
			myRs = myStmt.executeQuery("select Type, MONTH(Start), COUNT(*) from appointments GROUP BY Type, MONTH(Start)");

			
			while(myRs.next()){
				String type = myRs.getString("Type");
				String month = myRs.getString("MONTH(Start)");
				int count = myRs.getInt("COUNT(*)");
				
				AppointmentCount a = new AppointmentCount(count, type, month);
				
				appointmentList.add(a);
			}
		}catch(Exception ex) {
			System.out.println("Statement Error");
			ex.printStackTrace();
		}
		return appointmentList;
	}

	/**gets number of appointments grouped by the customer id
	 * @return
	 */
	public static ObservableList<CustomerCount> getNumberAppointmentsPerCustomer() {
		ObservableList<CustomerCount> appointmentList = FXCollections.observableArrayList();
		try {
			Statement myStmt = myCon.createStatement();
			ResultSet myRs;
			
			myRs = myStmt.executeQuery("select Customer_ID, COUNT(*) from appointments GROUP BY Customer_ID");

			
			while(myRs.next()){
				int id = myRs.getInt("Customer_ID");
				int count = myRs.getInt("COUNT(*)");
				
				CustomerCount a = new CustomerCount(count, id);
				
				appointmentList.add(a);
			}
		}catch(Exception ex) {
			System.out.println("Statement Error");
			ex.printStackTrace();
		}
		return appointmentList;
	}
	
	/** gets all users from the database
	 * @return
	 */
	public static ObservableList<User> getAllUsers(){
		ObservableList<User> list = FXCollections.observableArrayList();
		try {
			Statement myStmt = myCon.createStatement();
			ResultSet myRs;
			
			myRs = myStmt.executeQuery("select * from users");

			
			while(myRs.next()){
				String name = myRs.getString("User_Name");
				int id = myRs.getInt("User_ID");
				
				User user = new User(name, id);
				
				list.add(user);
			}
		}catch(Exception ex) {
			System.out.println("Statement Error");
			ex.printStackTrace();
		}
		
		return list;
	}
}

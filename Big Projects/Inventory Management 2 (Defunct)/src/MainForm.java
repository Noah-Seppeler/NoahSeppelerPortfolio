import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.TimeZone;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/** this is the main class for this application. It is responsible for all things that have to display to the user.
 * this includes the tableviews, the create forms, and the login screen
 * 
 * @author Noah Seppeler
 *
 */

public class MainForm extends Application {
	
	ResourceBundle languageBundle = ResourceBundle.getBundle("Bundle", Locale.getDefault());

	public static void main(String[] args) {
		launch();
	}

	@Override
	public void start(Stage mainStage) throws Exception {
		mainStage.setTitle(languageBundle.getString("login"));
		mainStage.setScene(new Scene(getLoginPane(mainStage),500,500));
		
		mainStage.show();
	}

	/**this method uses a lambda expression for each of the buttons defined in this method.
	 * this is useful for the reduced amount of code in order to keep it more readable
	 * 
	 * @param mainStage
	 * @return
	 */
	private Parent getLoginPane(Stage mainStage) {
		VBox loginPane = new VBox(40);
		
		GridPane fields = new GridPane();
		
		Label usernameLabel = new Label(languageBundle.getString("username"));
		Label passwordLabel = new Label(languageBundle.getString("password"));
		
		TextField usernameField = new TextField();
		PasswordField passwordField = new PasswordField();
		
		fields.add(usernameLabel, 0, 0);
		fields.add(passwordLabel, 0, 1);
		fields.add(usernameField, 1, 0);
		fields.add(passwordField, 1, 1);
		
		fields.setAlignment(Pos.CENTER);
		fields.setVgap(10);
		fields.setHgap(10);
		
		HBox buttons = new HBox(20);
		
		Button confirm = new Button(languageBundle.getString("confirm"));
		Button exit = new Button(languageBundle.getString("exit"));
		
		confirm.setOnAction(e->{
			try {
				QueryDriver.connect();
			}catch(Exception ex) {
				Alert a = new Alert(AlertType.ERROR);
				a.setHeaderText(languageBundle.getString("cannotConnect"));
				a.setContentText(languageBundle.getString("cannotConnect2"));
				a.setTitle(languageBundle.getString("error"));
				a.showAndWait();
			}
			if(QueryDriver.validateLogin(usernameField.getText(), passwordField.getText())) {
				generateLoginReport(usernameField.getText(), passwordField.getText(), true);
				getMainPane(mainStage);
				System.out.println("login successful");
			}else {
				Alert a = new Alert(AlertType.INFORMATION);
				generateLoginReport(usernameField.getText(), passwordField.getText(), false);
				a.setHeaderText(languageBundle.getString("invalidLogin"));
				a.setTitle(languageBundle.getString("error"));
				a.showAndWait();
			}

		});
		
		exit.setOnAction(e->{
			System.exit(0);
		});
		
		buttons.getChildren().addAll(confirm, exit);
		buttons.setAlignment(Pos.CENTER);
		
		loginPane.getChildren().addAll(new Label(languageBundle.getString("location") + ": " + TimeZone.getDefault().toZoneId()), fields, buttons);
		loginPane.setAlignment(Pos.CENTER);
		
		return loginPane;
	}

	private void generateLoginReport(String username, String password, boolean success) {
		File file = new File("login_activity.txt");
		FileWriter fr = null;

		
		try {
			fr = new FileWriter(file, true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		BufferedWriter br = new BufferedWriter(fr);
		PrintWriter pr = new PrintWriter(br);
		
		pr.println("Username: " + username +" Password: " + password + " Success: "+ success + " " + LocalDateTime.now());
		
		pr.close();
		
		try {
			fr.close();
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private void getMainPane(Stage mainStage) {
		VBox main = new VBox();
		HBox tables = new HBox(50);
		
		tables.getChildren().addAll(getAppointmentPane(), getCustomersPane());
		
		Button schedule = new Button(languageBundle.getString("scheduleButton"));
		Button totalAppointments = new Button(languageBundle.getString("customerTotalButton"));
		Button appointmentToCustomer = new Button(languageBundle.getString("appointmentToCustomer"));
		
		schedule.setOnAction(e->getSchedleStage());
		totalAppointments.setOnAction(e->getTotalAppointmentsStage());
		appointmentToCustomer.setOnAction(e->getAppointmentToCustomerStage());
		
		main.getChildren().addAll(tables, schedule, totalAppointments, appointmentToCustomer);
		
		mainStage.setScene(new Scene(main));
		
	}

	private void getAppointmentToCustomerStage() {
		Stage totalAppointmentsStage = new Stage();
		
		TableView<CustomerCount> appointments = new TableView<CustomerCount>(QueryDriver.getNumberAppointmentsPerCustomer());
		
		TableColumn<CustomerCount, String> appointmentCount = new TableColumn<CustomerCount, String>("Count");
		appointmentCount.setCellValueFactory(new PropertyValueFactory("count"));
		TableColumn<CustomerCount, String> appointmentType = new TableColumn<CustomerCount, String>("Customer ID");
		appointmentType.setCellValueFactory(new PropertyValueFactory("id"));
		
		appointments.getColumns().addAll(appointmentType, appointmentCount);
		
		totalAppointmentsStage.setScene(new Scene(appointments));
		
		totalAppointmentsStage.show();
	}

	private void getTotalAppointmentsStage() {
		Stage totalAppointmentsStage = new Stage();
		
		TableView<AppointmentCount> appointments = new TableView<AppointmentCount>(QueryDriver.getNumberAppointments());
		
		TableColumn<AppointmentCount, String> appointmentCount = new TableColumn<AppointmentCount, String>(languageBundle.getString("count"));
		appointmentCount.setCellValueFactory(new PropertyValueFactory("count"));
		TableColumn<AppointmentCount, String> appointmentType = new TableColumn<AppointmentCount, String>(languageBundle.getString("type"));
		appointmentType.setCellValueFactory(new PropertyValueFactory("type"));
		TableColumn<AppointmentCount, String> appointmentMonth = new TableColumn<AppointmentCount, String>(languageBundle.getString("month"));
		appointmentMonth.setCellValueFactory(new PropertyValueFactory("month"));
		
		appointments.getColumns().addAll(appointmentCount, appointmentType, appointmentMonth);
		
		totalAppointmentsStage.setScene(new Scene(appointments));
		
		totalAppointmentsStage.show();
	}

	private void getSchedleStage() {
		Stage scheduleStage = new Stage();
		
		HBox contactBox = new HBox();
		
		Label contactsLabel = new Label(languageBundle.getString("contact"));
		
		ComboBox<Contact> contacts = new ComboBox<Contact>(QueryDriver.getAllContacts());
		contacts.getSelectionModel().select(0);
		
		contactBox.getChildren().addAll(contactsLabel, contacts);
		
		TableView<Appointment> appointments = new TableView<Appointment>(QueryDriver.getAllAppointments(contacts.getSelectionModel().getSelectedItem()));
		
		TableColumn<Appointment, String> appointmentContactCol = new TableColumn<Appointment, String>(languageBundle.getString("contact"));
		appointmentContactCol.setCellValueFactory(new PropertyValueFactory("contact"));
		TableColumn<Appointment, String> appointmentIdCol = new TableColumn<Appointment, String>(languageBundle.getString("id"));
		appointmentIdCol.setCellValueFactory(new PropertyValueFactory("id"));
		TableColumn<Appointment, String> appointmentTitleCol = new TableColumn<Appointment, String>(languageBundle.getString("title"));
		appointmentTitleCol.setCellValueFactory(new PropertyValueFactory("title"));
		TableColumn<Appointment, String> appointmentDescriptionCol = new TableColumn<Appointment, String>(languageBundle.getString("description"));
		appointmentDescriptionCol.setCellValueFactory(new PropertyValueFactory("description"));
		TableColumn<Appointment, String> appointmentLocationCol = new TableColumn<Appointment, String>(languageBundle.getString("location"));
		appointmentLocationCol.setCellValueFactory(new PropertyValueFactory("location"));
		TableColumn<Appointment, String> appointmentTypeCol = new TableColumn<Appointment, String>(languageBundle.getString("type"));
		appointmentTypeCol.setCellValueFactory(new PropertyValueFactory("type"));
		TableColumn<Appointment, String> appointmentStartCol = new TableColumn<Appointment, String>(languageBundle.getString("start"));
		appointmentStartCol.setCellValueFactory(new PropertyValueFactory("startTime"));
		TableColumn<Appointment, String> appointmentEndCol = new TableColumn<Appointment, String>(languageBundle.getString("end"));
		appointmentEndCol.setCellValueFactory(new PropertyValueFactory("endTime"));
		TableColumn<Appointment, String> appointmentCustomerCol = new TableColumn<Appointment, String>(languageBundle.getString("customer"));
		appointmentCustomerCol.setCellValueFactory(new PropertyValueFactory("customer"));
		
		appointments.getColumns().addAll(appointmentContactCol, appointmentIdCol, appointmentTitleCol, appointmentDescriptionCol,
				appointmentLocationCol, appointmentTypeCol, appointmentStartCol, 
				appointmentEndCol, appointmentCustomerCol);
		
		contacts.setOnAction(e->{
			appointments.setItems(QueryDriver.getAllAppointments(contacts.getSelectionModel().getSelectedItem()));
		});
		
		VBox main = new VBox();
		
		main.getChildren().addAll(contactBox, appointments);
		
		scheduleStage.setScene(new Scene(main));
		
		scheduleStage.show();
		
	}

	/**
	 * like all my other buttons, these button actions are defined with with lambda expressions. 
	 * As mentioned before these are useful for the reduced amount of code for readability
	 * @return
	 */
	private Node getCustomersPane() {
		//Customer c = new Customer("Ham Ball", "send street", "14545", "555-555-5555", "test", "test", 669);
		//c.pushCustomer();
		
		//Appointment a = new Appointment("title", "tattle", "mattle", "dattle", LocalDateTime.now(), LocalDateTime.now(), "help", "me", 1, 4, 1);
		//a.pushAppointment();
		
		TableView<Customer> customers = new TableView<Customer>(QueryDriver.getAllCustomersFromDatabase());
		
		TableColumn<Customer, String> customerIdCol = new TableColumn<Customer, String>(languageBundle.getString("id"));
		customerIdCol.setCellValueFactory(new PropertyValueFactory("id"));
		TableColumn<Customer, String> customerNameCol = new TableColumn<Customer, String>(languageBundle.getString("name"));
		customerNameCol.setCellValueFactory(new PropertyValueFactory("name"));
		TableColumn<Customer, String> customerAddressCol = new TableColumn<Customer, String>(languageBundle.getString("address"));
		customerAddressCol.setCellValueFactory(new PropertyValueFactory("address"));
		TableColumn<Customer, String> customerPostalCol = new TableColumn<Customer, String>(languageBundle.getString("postalCode"));
		customerPostalCol.setCellValueFactory(new PropertyValueFactory("postalCode"));
		TableColumn<Customer, String> customerPhoneCol = new TableColumn<Customer, String>(languageBundle.getString("phone"));
		customerPhoneCol.setCellValueFactory(new PropertyValueFactory("phone"));
		TableColumn<Customer, String> customerCreateDateCol = new TableColumn<Customer, String>(languageBundle.getString("dateCreated"));
		customerCreateDateCol.setCellValueFactory(new PropertyValueFactory("createDate"));
		TableColumn<Customer, String> customerCreatedByCol = new TableColumn<Customer, String>(languageBundle.getString("createdBy"));
		customerCreatedByCol.setCellValueFactory(new PropertyValueFactory("createdBy"));
		TableColumn<Customer, String> customerLastUpdateCol = new TableColumn<Customer, String>(languageBundle.getString("lastUpdated"));
		customerLastUpdateCol.setCellValueFactory(new PropertyValueFactory("lastUpdate"));
		TableColumn<Customer, String> customerUpdatedByCol = new TableColumn<Customer, String>(languageBundle.getString("lastUpdatedBy"));
		customerUpdatedByCol.setCellValueFactory(new PropertyValueFactory("lastUpdatedBy"));
		TableColumn<Customer, String> customerDivisionCol = new TableColumn<Customer, String>(languageBundle.getString("divisionID"));
		customerDivisionCol.setCellValueFactory(new PropertyValueFactory("divisionID"));
		
		customers.getColumns().addAll(customerIdCol, customerNameCol, customerAddressCol,
				customerPostalCol, customerPhoneCol, customerCreateDateCol, customerCreatedByCol, 
				customerLastUpdateCol, customerUpdatedByCol, customerDivisionCol);
		
		HBox main = new HBox();
		
		VBox buttons = new VBox(5);
		
		Button add = new Button(languageBundle.getString("add"));
		Button modify = new Button(languageBundle.getString("modify"));
		Button delete = new Button(languageBundle.getString("delete"));
		
		add.setOnAction(e->getAddCustomerStage(customers));
		
		modify.setOnAction(e->getModifyCustomerStage(customers));
		
		delete.setOnAction(e->{
			try {
				QueryDriver.deleteCustomer(customers.getSelectionModel().getSelectedItem());
			}catch(Exception ex) {
				Alert a = new Alert(AlertType.ERROR);
				a.setHeaderText(languageBundle.getString("noDeleteCustomer"));
				a.showAndWait();
			}
			customers.setItems(QueryDriver.getAllCustomersFromDatabase());
		});
		
		buttons.getChildren().addAll(add, modify, delete);
		
		main.getChildren().addAll(customers, buttons);
		
		return main;
	}

	private void getModifyCustomerStage(TableView<Customer> customers) {
		Stage modifyCustomerStage = new Stage();
		Customer selectedCustomer = customers.getSelectionModel().getSelectedItem();
		
		modifyCustomerStage.setTitle(languageBundle.getString("modifyCustomer"));
		
		VBox main = new VBox();
		
		Label idLabel = new Label(languageBundle.getString("id"));
		Label nameLabel = new Label(languageBundle.getString("name"));
		Label addressLabel = new Label(languageBundle.getString("address"));
		Label postCodeLabel = new Label(languageBundle.getString("postalCode"));
		Label phoneLabel = new Label(languageBundle.getString("phone"));
		Label countryLabel = new Label(languageBundle.getString("country"));
		Label divisionLabel = new Label(languageBundle.getString("division"));
		
		TextField idField = new TextField(selectedCustomer.getId()+"");
		idField.setDisable(true);
		TextField nameField = new TextField(selectedCustomer.getName());
		TextField addressField = new TextField(selectedCustomer.getAddress());
		TextField postCodeField = new TextField(selectedCustomer.getPostalCode());
		TextField phoneField = new TextField(selectedCustomer.getPhone());
		
		ComboBox<Country> country = new ComboBox<Country>(QueryDriver.getAllCountries());
		ComboBox<Division> division = new ComboBox<Division>();
		
		country.setOnAction(e->{
			division.setItems(QueryDriver.getDivisions(country.getSelectionModel().getSelectedItem()));
		});
		
		if(selectedCustomer.getDivisionID()<3000) {
			country.getSelectionModel().select(0);
			division.setItems(QueryDriver.getDivisions(country.getSelectionModel().getSelectedItem()));
		}else if(selectedCustomer.getDivisionID() < 3919) {
			country.getSelectionModel().select(1);
			division.setItems(QueryDriver.getDivisions(country.getSelectionModel().getSelectedItem()));
		}else {
			country.getSelectionModel().select(2);
			division.setItems(QueryDriver.getDivisions(country.getSelectionModel().getSelectedItem()));
		}
		
		for(int i=0;i<division.getItems().size();i++){
			if(selectedCustomer.getDivisionID() == division.getItems().get(i).getId()) {
				division.getSelectionModel().select(i);
				break;
			}
		}
		
		GridPane fields = new GridPane();
		
		fields.add(idLabel, 0, 0);
		fields.add(nameLabel, 0, 1);
		fields.add(addressLabel, 0, 2);
		fields.add(postCodeLabel, 0, 3);
		fields.add(phoneLabel, 0, 4);
		fields.add(countryLabel, 0, 5);
		fields.add(divisionLabel, 0, 6);
		
		fields.add(idField, 1, 0);
		fields.add(nameField, 1, 1);
		fields.add(addressField, 1, 2);
		fields.add(postCodeField, 1, 3);
		fields.add(phoneField, 1, 4);
		fields.add(country, 1, 5);
		fields.add(division, 1, 6);
		
		HBox buttons = new HBox();
		
		Button confirm = new Button(languageBundle.getString("confirm"));
		Button cancel = new Button(languageBundle.getString("cancel"));
		
		cancel.setOnAction(e->{
			modifyCustomerStage.close();
		});
		
		confirm.setOnAction(e->{
			int id = selectedCustomer.getId();
			String customerName = nameField.getText();
			String customerAddress = addressField.getText();
			String customerPostCode = postCodeField.getText();
			String customerPhone = phoneField.getText();
			int customerDivision = division.getSelectionModel().getSelectedItem().getId();
			
			QueryDriver.modifyCustomer(id, customerName, customerAddress, customerPostCode, customerPhone, customerDivision);
			
			customers.setItems(QueryDriver.getAllCustomersFromDatabase());
			
			modifyCustomerStage.close();
		});
		
		buttons.getChildren().addAll(confirm, cancel);
		
		main.getChildren().addAll(fields, buttons);
		
		modifyCustomerStage.setScene(new Scene(main));
		
		modifyCustomerStage.show();
	}

	private void getAddCustomerStage(TableView<Customer> customers) {
		Stage addCustomerStage = new Stage();
		
		addCustomerStage.setTitle(languageBundle.getString("addCustomer"));
		
		VBox main = new VBox();
		
		Label idLabel = new Label(languageBundle.getString("id"));
		Label nameLabel = new Label(languageBundle.getString("name"));
		Label addressLabel = new Label(languageBundle.getString("address"));
		Label postCodeLabel = new Label(languageBundle.getString("postalCode"));
		Label phoneLabel = new Label(languageBundle.getString("phone"));
		Label countryLabel = new Label(languageBundle.getString("country"));
		Label divisionLabel = new Label(languageBundle.getString("division"));
		
		TextField idField = new TextField(languageBundle.getString("autoGen"));
		idField.setDisable(true);
		TextField nameField = new TextField();
		TextField addressField = new TextField();
		TextField postCodeField = new TextField();
		TextField phoneField = new TextField();
		
		ComboBox<Country> country = new ComboBox<Country>(QueryDriver.getAllCountries());
		ComboBox<Division> division = new ComboBox<Division>();
		
		country.setOnAction(e->{
			division.setItems(QueryDriver.getDivisions(country.getSelectionModel().getSelectedItem()));
		});
		
		GridPane fields = new GridPane();
		
		fields.add(idLabel, 0, 0);
		fields.add(nameLabel, 0, 1);
		fields.add(addressLabel, 0, 2);
		fields.add(postCodeLabel, 0, 3);
		fields.add(phoneLabel, 0, 4);
		fields.add(countryLabel, 0, 5);
		fields.add(divisionLabel, 0, 6);
		
		fields.add(idField, 1, 0);
		fields.add(nameField, 1, 1);
		fields.add(addressField, 1, 2);
		fields.add(postCodeField, 1, 3);
		fields.add(phoneField, 1, 4);
		fields.add(country, 1, 5);
		fields.add(division, 1, 6);
		
		HBox buttons = new HBox();
		
		Button confirm = new Button(languageBundle.getString("confirm"));
		Button cancel = new Button(languageBundle.getString("cancel"));
		
		cancel.setOnAction(e->{
			addCustomerStage.close();
		});
		
		confirm.setOnAction(e->{
			String customerName = nameField.getText();
			String customerAddress = addressField.getText();
			String customerPostCode = postCodeField.getText();
			String customerPhone = phoneField.getText();
			int customerDivision = division.getSelectionModel().getSelectedItem().getId();
			
			Customer c = new Customer(customerName, customerAddress, customerPostCode, customerPhone, "test", "test", customerDivision);
			c.pushCustomer();
			
			customers.setItems(QueryDriver.getAllCustomersFromDatabase());
			
			addCustomerStage.close();
		});
		
		buttons.getChildren().addAll(confirm, cancel);
		
		main.getChildren().addAll(fields, buttons);
		
		addCustomerStage.setScene(new Scene(main));
		
		addCustomerStage.show();
		
	}

	private Node getAppointmentPane() {
		TableView<Appointment> appointments = getAppointmentsTable(QueryDriver.getAllAppointmentsFromDatabase("week"));
		
		VBox main = new VBox();
		
		HBox viewButtons = new HBox();
		
		VBox buttons = new VBox(5);
		
		Button add = new Button(languageBundle.getString("add"));
		Button modify = new Button(languageBundle.getString("modify"));
		Button delete = new Button(languageBundle.getString("delete"));
		
		buttons.getChildren().addAll(add, modify, delete);
		
		viewButtons.getChildren().addAll(appointments, buttons);
		
		HBox sortButtons = new HBox(5);
		
		RadioButton week = new RadioButton();
		RadioButton month = new RadioButton();
		
		week.setOnAction(e->{
			appointments.setItems(QueryDriver.getAllAppointmentsFromDatabase("week"));
		});
		
		month.setOnAction(e->{
			appointments.setItems(QueryDriver.getAllAppointmentsFromDatabase("month"));
		});
		
		ToggleGroup group = new ToggleGroup();
		
		week.setToggleGroup(group);
		month.setToggleGroup(group);
		
		week.setSelected(true);
		
		sortButtons.getChildren().addAll(new Label("Week"), week, new Label("Month"), month);
		
		add.setOnAction(e->{
			week.setSelected(true);
			getAddAppointmentStage(appointments);
		});
		
		modify.setOnAction(e->getModifyAppointmentStage(appointments));
		
		delete.setOnAction(e->{
			QueryDriver.deleteAppointment(appointments.getSelectionModel().getSelectedItem());
			Alert a = new Alert(AlertType.INFORMATION);
			a.setHeaderText(languageBundle.getString("appointmentDeleted"));
			a.setContentText(languageBundle.getString("id")+ ": " + appointments.getSelectionModel().getSelectedItem().getId()
					+ " " + languageBundle.getString("type") + ": " + appointments.getSelectionModel().getSelectedItem().getType());
			
			a.show();
			if(week.isSelected()) {
				appointments.setItems(QueryDriver.getAllAppointmentsFromDatabase("week"));
			}else {
				appointments.setItems(QueryDriver.getAllAppointmentsFromDatabase("month"));
			}
		});
		
		main.getChildren().addAll(viewButtons, sortButtons);
		
		checkAppointments(appointments);
		
		return main;
	}

	private void checkAppointments(TableView<Appointment> appointments) {
		Alert a = new Alert(AlertType.INFORMATION);;
		for(int i=0;i<appointments.getItems().size();i++) {
			long seconds = appointments.getItems().get(i).getStartTime().atZone(TimeZone.getDefault().toZoneId()).toEpochSecond()
					- LocalDateTime.now().atZone(TimeZone.getDefault().toZoneId()).toEpochSecond();
			System.out.println(seconds);
			if(seconds <= 900 && seconds >= 0) {
				a.setHeaderText(languageBundle.getString("appointmentIminant"));
				a.setContentText(languageBundle.getString("id") + ": " + appointments.getItems().get(i).getId() + " "
						+ languageBundle.getString("start") + ": " + appointments.getItems().get(i).getStartTime());
				a.show();
				break;
			}
			a.setHeaderText(languageBundle.getString("appointmentSafe"));
			a.show();
		}
		
	}

	private TableView<Appointment> getAppointmentsTable(ObservableList<Appointment> list) {
		TableView<Appointment> appointments = new TableView<Appointment>(list);
		
		TableColumn<Appointment, String> appointmentIdCol = new TableColumn<Appointment, String>(languageBundle.getString("id"));
		appointmentIdCol.setCellValueFactory(new PropertyValueFactory("id"));
		TableColumn<Appointment, String> appointmentTitleCol = new TableColumn<Appointment, String>(languageBundle.getString("title"));
		appointmentTitleCol.setCellValueFactory(new PropertyValueFactory("title"));
		TableColumn<Appointment, String> appointmentDescriptionCol = new TableColumn<Appointment, String>(languageBundle.getString("description"));
		appointmentDescriptionCol.setCellValueFactory(new PropertyValueFactory("description"));
		TableColumn<Appointment, String> appointmentLocationCol = new TableColumn<Appointment, String>(languageBundle.getString("location"));
		appointmentLocationCol.setCellValueFactory(new PropertyValueFactory("location"));
		TableColumn<Appointment, String> appointmentContactCol = new TableColumn<Appointment, String>(languageBundle.getString("contact"));
		appointmentContactCol.setCellValueFactory(new PropertyValueFactory("contact"));
		TableColumn<Appointment, String> appointmentTypeCol = new TableColumn<Appointment, String>(languageBundle.getString("type"));
		appointmentTypeCol.setCellValueFactory(new PropertyValueFactory("type"));
		TableColumn<Appointment, String> appointmentStartCol = new TableColumn<Appointment, String>(languageBundle.getString("start"));
		appointmentStartCol.setCellValueFactory(new PropertyValueFactory("startTime"));
		TableColumn<Appointment, String> appointmentEndCol = new TableColumn<Appointment, String>(languageBundle.getString("end"));
		appointmentEndCol.setCellValueFactory(new PropertyValueFactory("endTime"));
		TableColumn<Appointment, String> appointmentCustomerCol = new TableColumn<Appointment, String>(languageBundle.getString("customer"));
		appointmentCustomerCol.setCellValueFactory(new PropertyValueFactory("customer"));
		
		appointments.getColumns().addAll(appointmentIdCol, appointmentTitleCol, appointmentDescriptionCol,
				appointmentLocationCol, appointmentContactCol, appointmentTypeCol, appointmentStartCol, 
				appointmentEndCol, appointmentCustomerCol);
		
		return appointments;
	}

	private void getModifyAppointmentStage(TableView<Appointment> appointments) {
		Stage addAppointmentStage = new Stage();
		addAppointmentStage.setTitle(languageBundle.getString("addAppointment"));
		
		VBox main = new VBox();
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		
		Label idLabel = new Label(languageBundle.getString("id"));
		Label titleLabel = new Label(languageBundle.getString("title"));
		Label typeLabel = new Label(languageBundle.getString("type"));
		Label descriptionLabel = new Label(languageBundle.getString("description"));
		Label locationLabel = new Label(languageBundle.getString("location"));
		Label contactLabel = new Label(languageBundle.getString("contact"));
		Label customerLabel = new Label(languageBundle.getString("customer"));
		Label userLabel = new Label("User");
		Label startLabel = new Label(languageBundle.getString("start"));
		Label endLabel = new Label(languageBundle.getString("end"));
		
		TextField idField = new TextField(appointments.getSelectionModel().getSelectedItem().getId()+"");
		idField.setDisable(true);
		TextField titleField = new TextField(appointments.getSelectionModel().getSelectedItem().getTitle());
		TextField typeField = new TextField(appointments.getSelectionModel().getSelectedItem().getType());
		TextArea descriptionArea = new TextArea(appointments.getSelectionModel().getSelectedItem().getDescription());
		descriptionArea.setPrefWidth(100);
		TextField locationField = new TextField(appointments.getSelectionModel().getSelectedItem().getLocation());
		ComboBox<Contact> contactCombo = new ComboBox<Contact>(QueryDriver.getAllContacts());
		contactCombo.getSelectionModel().select(appointments.getSelectionModel().getSelectedItem().getContact());
		ComboBox<Customer> customerCombo = new ComboBox<Customer>(QueryDriver.getAllCustomersFromDatabase());
		customerCombo.getSelectionModel().select(appointments.getSelectionModel().getSelectedItem().getCustomer());
		ComboBox<User> userCombo = new ComboBox<User>(QueryDriver.getAllUsers());
		userCombo.getSelectionModel().select(appointments.getSelectionModel().getSelectedItem().getUser());
		System.out.println(appointments.getSelectionModel().getSelectedItem().getUser());
		TextField startField = new TextField(appointments.getSelectionModel().getSelectedItem().getStartTime().format(formatter));
		TextField endField = new TextField(appointments.getSelectionModel().getSelectedItem().getEndTime().format(formatter));
		
		VBox startEndLabels = new VBox(5);
		VBox startEndFields = new VBox();
		
		startEndLabels.getChildren().addAll(startLabel, endLabel);
		startEndFields.getChildren().addAll(startField,endField);
		
		GridPane fields = new GridPane();
		
		fields.add(idLabel, 0, 0);
		fields.add(titleLabel, 0, 1);
		fields.add(typeLabel, 0, 2);
		fields.add(descriptionLabel, 0, 4);
		fields.add(locationLabel, 2, 0);
		fields.add(contactLabel, 2, 1);
		fields.add(customerLabel, 2, 2);
		fields.add(userLabel, 2, 3);
		fields.add(startEndLabels, 2, 4);
		
		fields.add(idField, 1, 0);
		fields.add(titleField, 1, 1);
		fields.add(typeField, 1, 2);
		fields.add(descriptionArea, 1, 4);
		fields.add(locationField, 3, 0);
		fields.add(contactCombo, 3, 1);
		fields.add(customerCombo, 3, 2);
		fields.add(userCombo, 3, 3);
		fields.add(startEndFields, 3, 4);
		
		HBox buttons = new HBox();
		
		Button confirm = new Button(languageBundle.getString("confirm"));
		Button cancel = new Button(languageBundle.getString("cancel"));
		
		confirm.setOnAction(e->{
			String title = titleField.getText();
			String type = typeField.getText();
			String description = descriptionArea.getText();
			String location = locationField.getText();
			Contact contact = contactCombo.getSelectionModel().getSelectedItem();
			Customer customer = customerCombo.getSelectionModel().getSelectedItem();
			User user = userCombo.getSelectionModel().getSelectedItem();
			LocalDateTime startTime = LocalDateTime.parse(startField.getText(), formatter).atZone(TimeZone.getDefault().toZoneId())
					.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
			LocalDateTime endTime = LocalDateTime.parse(endField.getText(), formatter).atZone(TimeZone.getDefault().toZoneId())
					.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
			
			try {
				LocalDateTime testStart = startTime.atZone(ZoneId.of("UTC"))
						.withZoneSameInstant(ZoneId.of("US/Eastern")).toLocalDateTime();
				LocalDateTime testEnd = endTime.atZone(ZoneId.of("UTC"))
						.withZoneSameInstant(ZoneId.of("US/Eastern")).toLocalDateTime();
				
				if((testStart.getHour()*60+startTime.getMinute()<480 || testStart.getHour()*60+startTime.getMinute()>1320)
						|| (testEnd.getHour()*60+testEnd.getMinute()<480 || testEnd.getHour()*60+testEnd.getMinute()>1320)) {
					
					throw new Exception();
				}
				try {
					ArrayList<LocalDateTime> appointmentTimes = QueryDriver.getAllAppoinmentTimes();
					
					long actualStart = startTime.toEpochSecond(ZoneOffset.ofHours(0));
					long actualEnd = endTime.toEpochSecond(ZoneOffset.ofHours(0));
					
					for(int i=0; i<appointmentTimes.size();i+=2) {
						long start = appointmentTimes.get(i).atZone(ZoneId.of("UTC")).toEpochSecond();
						long end = appointmentTimes.get(i+1).atZone(ZoneId.of("UTC")).toEpochSecond();
						if(((actualStart > start && actualStart < end) || (actualEnd > start && actualEnd < end))
								|| ((start > actualStart && start < actualEnd) || (end > actualStart && end < actualEnd))
								|| (start == actualStart || end == actualEnd)) {
							throw new Exception();
						}
					}
					
					try {
						if(startTime.toEpochSecond(ZoneOffset.ofHours(0)) > endTime.toEpochSecond(ZoneOffset.ofHours(0))) {
							throw new Exception();
						}
						
						QueryDriver.ModifyAppointment(appointments.getSelectionModel().getSelectedItem().getId(), title, type, description, location, contact, customer, startTime, endTime, user);
						
						appointments.setItems(QueryDriver.getAllAppointmentsFromDatabase("week"));
						
						addAppointmentStage.close();
						addAppointmentStage.close();
					}catch(Exception ex) {
						Alert a = new Alert(AlertType.ERROR);
						a.setHeaderText(languageBundle.getString("startOverEnd"));
						a.showAndWait();
					}
				}catch(Exception ex){
					Alert a = new Alert(AlertType.ERROR);
					a.setHeaderText(languageBundle.getString("scheduleOverlap"));
					a.showAndWait();
				}
			}catch (Exception ex) {
				Alert a = new Alert(AlertType.ERROR);
				a.setHeaderText(languageBundle.getString("outsideBusinessHeader"));
				a.showAndWait();
			}
			
		});
		
		cancel.setOnAction(e->{
			addAppointmentStage.close();
		});
		
		buttons.getChildren().addAll(confirm, cancel);
		
		main.getChildren().addAll(fields, buttons);
		
		addAppointmentStage.setScene(new Scene(main));
		
		addAppointmentStage.show();
	}

	private void getAddAppointmentStage(TableView<Appointment> appointments) {
		Stage addAppointmentStage = new Stage();
		addAppointmentStage.setTitle(languageBundle.getString("addAppointment"));
		
		VBox main = new VBox();
		
		Label idLabel = new Label(languageBundle.getString("id"));
		Label titleLabel = new Label(languageBundle.getString("title"));
		Label typeLabel = new Label(languageBundle.getString("type"));
		Label descriptionLabel = new Label(languageBundle.getString("description"));
		Label locationLabel = new Label(languageBundle.getString("location"));
		Label contactLabel = new Label(languageBundle.getString("contact"));
		Label customerLabel = new Label(languageBundle.getString("customer"));
		Label userLabel = new Label("User");
		Label startLabel = new Label(languageBundle.getString("start"));
		Label endLabel = new Label(languageBundle.getString("end"));
		
		TextField idField = new TextField(languageBundle.getString("autoGen"));
		idField.setDisable(true);
		TextField titleField = new TextField();
		TextField typeField = new TextField();
		TextArea descriptionArea = new TextArea();
		descriptionArea.setPrefWidth(100);
		TextField locationField = new TextField();
		ComboBox<Contact> contactCombo = new ComboBox<Contact>(QueryDriver.getAllContacts());
		ComboBox<Customer> customerCombo = new ComboBox<Customer>(QueryDriver.getAllCustomersFromDatabase());
		ComboBox<User> userCombo = new ComboBox<User>(QueryDriver.getAllUsers());
		TextField startField = new TextField();
		TextField endField = new TextField();
		
		VBox startEndLabels = new VBox(5);
		VBox startEndFields = new VBox();
		
		startEndLabels.getChildren().addAll(startLabel, endLabel);
		startEndFields.getChildren().addAll(startField,endField);
		
		GridPane fields = new GridPane();
		
		fields.add(idLabel, 0, 0);
		fields.add(titleLabel, 0, 1);
		fields.add(typeLabel, 0, 2);
		fields.add(descriptionLabel, 0, 4);
		fields.add(locationLabel, 2, 0);
		fields.add(contactLabel, 2, 1);
		fields.add(customerLabel, 2, 2);
		fields.add(userLabel, 2, 3);
		fields.add(startEndLabels, 2, 4);
		
		fields.add(idField, 1, 0);
		fields.add(titleField, 1, 1);
		fields.add(typeField, 1, 2);
		fields.add(descriptionArea, 1, 4);
		fields.add(locationField, 3, 0);
		fields.add(contactCombo, 3, 1);
		fields.add(customerCombo, 3, 2);
		fields.add(userCombo, 3, 3);
		fields.add(startEndFields, 3, 4);
		
		HBox buttons = new HBox();
		
		Button confirm = new Button(languageBundle.getString("confirm"));
		Button cancel = new Button(languageBundle.getString("cancel"));
		
		confirm.setOnAction(e->{
			String title = titleField.getText();
			String type = typeField.getText();
			String description = descriptionArea.getText();
			String location = locationField.getText();
			Contact contact = contactCombo.getSelectionModel().getSelectedItem();
			Customer customer = customerCombo.getSelectionModel().getSelectedItem();
			User user = userCombo.getSelectionModel().getSelectedItem();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
			LocalDateTime startTime = LocalDateTime.parse(startField.getText(), formatter).atZone(TimeZone.getDefault().toZoneId())
					.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
			LocalDateTime endTime = LocalDateTime.parse(endField.getText(), formatter).atZone(TimeZone.getDefault().toZoneId())
					.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
			
			try {
				LocalDateTime testStart = startTime.atZone(ZoneId.of("UTC"))
						.withZoneSameInstant(ZoneId.of("US/Eastern")).toLocalDateTime();
				LocalDateTime testEnd = endTime.atZone(ZoneId.of("UTC"))
						.withZoneSameInstant(ZoneId.of("US/Eastern")).toLocalDateTime();
				
				if((testStart.getHour()*60+startTime.getMinute()<480 || testStart.getHour()*60+startTime.getMinute()>1320)
						|| (testEnd.getHour()*60+testEnd.getMinute()<480 || testEnd.getHour()*60+testEnd.getMinute()>1320)) {
					
					throw new Exception();
				}
				try {
					ArrayList<LocalDateTime> appointmentTimes = QueryDriver.getAllAppoinmentTimes();
					
					long actualStart = startTime.toEpochSecond(ZoneOffset.ofHours(0));
					long actualEnd = endTime.toEpochSecond(ZoneOffset.ofHours(0));
					
					for(int i=0; i<appointmentTimes.size();i+=2) {
						long start = appointmentTimes.get(i).atZone(ZoneId.of("UTC")).toEpochSecond();
						long end = appointmentTimes.get(i+1).atZone(ZoneId.of("UTC")).toEpochSecond();
						if(((actualStart > start && actualStart < end) || (actualEnd > start && actualEnd < end))
								|| ((start > actualStart && start < actualEnd) || (end > actualStart && end < actualEnd))
								|| (start == actualStart || end == actualEnd)) {
							throw new Exception();
						}
					}
					
					try {
						if(startTime.toEpochSecond(ZoneOffset.ofHours(0)) > endTime.toEpochSecond(ZoneOffset.ofHours(0))) {
							throw new Exception();
						}
						
						Appointment a = new Appointment(title, description, location, type, contact, startTime, endTime, "test", "test", user, customer);
						a.pushAppointment();
						
						appointments.setItems(QueryDriver.getAllAppointmentsFromDatabase("week"));
						addAppointmentStage.close();
					}catch(Exception ex) {
						Alert a = new Alert(AlertType.ERROR);
						a.setHeaderText(languageBundle.getString("startOverEnd"));
						a.showAndWait();
					}
					
					
				}catch(Exception ex){
					Alert a = new Alert(AlertType.ERROR);
					a.setHeaderText(languageBundle.getString("scheduleOverlap"));
					a.showAndWait();
				}
			}catch (Exception ex) {
				Alert a = new Alert(AlertType.ERROR);
				a.setHeaderText(languageBundle.getString("outsideBusinessHeader"));
				a.showAndWait();
			}
			
		});
		
		cancel.setOnAction(e->{
			addAppointmentStage.close();
		});
		
		buttons.getChildren().addAll(confirm, cancel);
		
		main.getChildren().addAll(fields, buttons);
		
		addAppointmentStage.setScene(new Scene(main));
		
		addAppointmentStage.show();
		
	}

}

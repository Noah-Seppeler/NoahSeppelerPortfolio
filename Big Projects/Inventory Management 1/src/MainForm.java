import java.util.Random;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * This is the main form for my application it controls what the form looks like. it also houses the add part and add product stages
 * 
 * One run time error that I faced while setting this up was in the getProductAdd method. In this method I set up the part inventory and sub inventory tables.
 * At first I kept getting a null pointer exception. This was because in the add part button action handler I was pointing to grab from the subPart array not the main part array.
 * 
 * One thing that could be implemented to make this system better is to put a system in to catch when you are about to delete a part that is assigned to a product.
 * This could make it so that products couldn't keep parts on them that were no longer listed in inventory.
 * 
 * @author Noah Seppeler
 * 
 * 
 */

public class MainForm extends Application {

    public static void main(String[] args) {
		launch();

	}
    
    @Override
	public void start(Stage mainStage) throws Exception {
		Scene mainScene = new Scene(getMainPane(), 900,500);
		mainStage.setScene(mainScene);
		mainStage.setTitle("Inventory Management");
		mainStage.show();
	}

	private Parent getMainPane() {
		VBox mainPane = new VBox(5);
		Button exit = new Button("Exit");
		exit.setOnAction(e -> {
			System.exit(0);
		});
		mainPane.getChildren().addAll(new Label("Inventory Managment System"), getInventoryViewers(), exit);
		return mainPane;
	}

	private Node getInventoryViewers() {
		HBox inventories = new HBox(50);
		inventories.getChildren().addAll(getPartsPane(), getProductsPane());
		return inventories;
	}

	private Node getProductsPane() {
		VBox productsPane = new VBox();
		
		TableView<Product> productsTable = new TableView<Product>(Inventory.getAllProducts());
		productsTable.setPrefWidth(400);
		
		TableColumn<Product, String> productIdCol = new TableColumn<Product, String>("Product ID");
		productIdCol.setCellValueFactory(new PropertyValueFactory("id"));
		TableColumn<Product, String> productNameCol = new TableColumn<Product, String>("Product Name");
		productNameCol.setCellValueFactory(new PropertyValueFactory("name"));
		TableColumn<Product, String> productStockCol = new TableColumn<Product, String>("Inventory Level");
		productStockCol.setCellValueFactory(new PropertyValueFactory("stock"));
		TableColumn<Product, String> productPriceCol = new TableColumn<Product, String>("Price / Cost Per Unit");
		productPriceCol.setCellValueFactory(new PropertyValueFactory("price"));
		
		productsTable.getColumns().addAll(productIdCol, productNameCol, productStockCol, productPriceCol);
		
		productsPane.getChildren().addAll(new Label("Products:"), getProductSearchBox(productsTable), productsTable, getProductButtonArray(productsTable));
		return productsPane;
	}
	
	private Node getProductSearchBox(TableView<Product> productsTable) {
		HBox searchBox = new HBox();
		TextField search = new TextField();
		search.textProperty().addListener((obs, oldText, newText) ->{
			if(!(search.getText().isEmpty())) {
				try {
					ObservableList<Product> temp = FXCollections.observableArrayList();
					Product product = Inventory.lookupProduct(Integer.parseInt(search.getText()));
					if(product != null) {
						temp.add(product);
						productsTable.setItems(temp);
					}else {
						productsTable.setItems(FXCollections.observableArrayList());
						productsTable.setPlaceholder(new Label("No Item found in search"));
					}
				}catch(Exception ex) {
					productsTable.setItems(Inventory.lookupProduct(search.getText()));
					if(!Inventory.lookupProduct(search.getText()).isEmpty()) {
						productsTable.setPlaceholder(new Label("No Item found in search"));
					}
				}
			}else {
				productsTable.setItems(Inventory.getAllProducts());
			}
		});
		
		searchBox.getChildren().addAll(search);
		
		return searchBox;
	}
	
	private Node getProductButtonArray(TableView productsTable) {
		HBox buttons = new HBox(10);
		
		Button add = new Button("Add");
		Button modify = new Button("Modify");
		Button delete = new Button("Delete");
		
		delete.setOnAction(e->{
			if(Inventory.getAllProducts().size() != 0) {
				if(!((Product)productsTable.getSelectionModel().getSelectedItem()).getAllAssociatedParts().isEmpty()) {
					Alert a = new Alert(AlertType.INFORMATION);
					a.setContentText("You cannot delete a Product when a Part is associated with it.");
					a.setHeaderText("Unable to complete action");
					a.setTitle("Unable to complete action");
					a.showAndWait();
				}else {
					Alert a = new Alert(AlertType.CONFIRMATION);
					a.setHeaderText("Are you sure?");
					a.setContentText("Are you sure you wish to delete this product?");
					if(a.showAndWait().get() == ButtonType.OK) {
						Inventory.deleteProduct((Product) productsTable.getSelectionModel().getSelectedItem());
					}
				}
			}
		});
		
		modify.setOnAction(e->{
			if(productsTable.getSelectionModel().getSelectedItem() != null) {
				Product selectedProduct = (Product) productsTable.getSelectionModel().getSelectedItem();
				int selectedProductIndex = productsTable.getSelectionModel().getSelectedIndex();
				productModifyAction(selectedProduct, selectedProductIndex);
			}
		});
		
		add.setOnAction(e->productAddAction());
		
		buttons.getChildren().addAll(add,modify,delete);
		return buttons;
	}

	private void productModifyAction(Product selectedProduct, int selectedProductIndex) {
		Stage modifyProductStage = new Stage();
		modifyProductStage.setScene(new Scene(getModifyProductPane(modifyProductStage, selectedProduct, selectedProductIndex),800,600));
		
		modifyProductStage.show();
		
	}

	private Parent getModifyProductPane(Stage modifyProductStage, Product selectedProduct, int selectedProductIndex) {
		Random rand = new Random();
		VBox main = new VBox(20);
		HBox addProductPane = new HBox(10);
		VBox textFieldsAndButtons = new VBox(5);
		VBox partsAdd = new VBox(10);
		
		GridPane textFields = new GridPane();
		textFields.setHgap(3);
		textFields.setVgap(5);
		
		textFields.add(new Label("ID"),0,0);
		textFields.add(new Label("Name"),0,1);
		textFields.add(new Label("Inv"),0,2);
		textFields.add(new Label("Price/Cost"),0,3);
		textFields.add(new Label("Max"),0,4);
		textFields.add(new Label("Min"),2,4);
		
		TextField idTextField = new TextField(selectedProduct.getId()+"");
		TextField nameTextField = new TextField(selectedProduct.getName());
		TextField invTextField = new TextField(selectedProduct.getStock()+"");
		TextField priceTextField = new TextField(selectedProduct.getPrice()+"");
		TextField maxTextField = new TextField(selectedProduct.getMax()+"");
		TextField minTextField = new TextField(selectedProduct.getMin()+"");
		
		idTextField.setDisable(true);
		
		textFields.add(idTextField,1,0);
		textFields.add(nameTextField,1,1);
		textFields.add(invTextField,1,2);
		textFields.add(priceTextField,1,3);
		textFields.add(maxTextField,1,4);
		textFields.add(minTextField,3,4);
		
		
		TableView<Part> mainPartView = getPartsTable();
		TableView<Part> subPartView = getPartsTable();
		
		Product newProduct = new Product(0, "", 0, 0, 0, 0);
		
		subPartView.setItems(newProduct.getAllAssociatedParts());
		
		for(int i=0; i<selectedProduct.getAllAssociatedParts().size(); i++) {
			newProduct.addAssociatedPart(selectedProduct.getAllAssociatedParts().get(i));
		}
		
		Button addPart = new Button("Add");
		Button removePart = new Button("Remove");
		
		addPart.setOnAction(e->{
			newProduct.addAssociatedPart(mainPartView.getSelectionModel().getSelectedItem());
		});
		
		removePart.setOnAction(e->{
			newProduct.deleteAssociatedPart(subPartView.getSelectionModel().getSelectedItem());
		});
		
		partsAdd.getChildren().addAll(getPartSearchBox(mainPartView),mainPartView,addPart,subPartView,removePart);
		
		HBox bottomButtons = new HBox(5);
		bottomButtons.setAlignment(Pos.CENTER);
		
		Button save = new Button("Save");
		Button cancel = new Button("Cancel");
		
		bottomButtons.getChildren().addAll(save, cancel);
		
		cancel.setOnAction(e->{
			modifyProductStage.close();
		});
		
		//don't look at this. I know it's bad but I didn't know that else to do :/
		save.setOnAction(e->{
			int inv = 0, min = 0, max = 0;
			double price = 0;
			try {
				inv = Integer.parseInt(invTextField.getText());
				try {
					price = Double.parseDouble(priceTextField.getText());
					try {
						max = Integer.parseInt(maxTextField.getText());
						try {
							min = Integer.parseInt(minTextField.getText());
							try {
								if(max<min) {
									throw new Exception();
								}
								try {
									if(inv<min||inv>max) {
										throw new Exception();
									}
									
									
									newProduct.setId(selectedProduct.getId());
									newProduct.setMax(max);
									newProduct.setMin(min);
									newProduct.setName(nameTextField.getText());
									newProduct.setPrice(price);
									newProduct.setStock(inv);
									
									Inventory.updateProduct(selectedProductIndex, newProduct);
									
									modifyProductStage.close();
								}catch(Exception ex) {
									Alert a = new Alert(AlertType.INFORMATION);
									a.setTitle("Invalid Data");
									a.setHeaderText("Invalid Data");
									a.setContentText("Stock needs to be between max and min");
									a.showAndWait();
								}
							}catch(Exception ex) {
								Alert a = new Alert(AlertType.INFORMATION);
								a.setTitle("Invalid Data");
								a.setHeaderText("Invalid Data");
								a.setContentText("Min needs to be less than Max");
								a.showAndWait();
							}
						}catch(Exception ex) {
							Alert a = new Alert(AlertType.INFORMATION);
							a.setTitle("Invalid Data");
							a.setHeaderText("Invalid Data");
							a.setContentText("Text Field \"Min\" needs to be a number");
							a.showAndWait();
						}
					}catch(Exception ex) {
						Alert a = new Alert(AlertType.INFORMATION);
						a.setTitle("Invalid Data");
						a.setHeaderText("Invalid Data");
						a.setContentText("Text Field \"Max\" needs to be a number");
						a.showAndWait();
					}
				}catch(Exception ex) {
					Alert a = new Alert(AlertType.INFORMATION);
					a.setTitle("Invalid Data");
					a.setHeaderText("Invalid Data");
					a.setContentText("Text Field \"Price\" needs to be a number");
					a.showAndWait();
				}
			}catch(Exception ex) {
				Alert a = new Alert(AlertType.INFORMATION);
				a.setTitle("Invalid Data");
				a.setHeaderText("Invalid Data");
				a.setContentText("Text Field \"Stock\" needs to be a number");
				a.showAndWait();
			}
		});
		
		textFieldsAndButtons.getChildren().addAll(textFields);
		
		addProductPane.getChildren().addAll(textFieldsAndButtons,partsAdd);
		addProductPane.setAlignment(Pos.CENTER);
		
		main.getChildren().addAll(new Label("Add Products: "), addProductPane, bottomButtons);
		main.setAlignment(Pos.CENTER);
		return main;
	}

	private void productAddAction() {
		Stage addProductStage = new Stage();
		addProductStage.setScene(new Scene(getAddProductPane(addProductStage),800,600));
		
		addProductStage.show();
	}

	private Parent getAddProductPane(Stage addProductStage) {
		Random rand = new Random();
		VBox main = new VBox(20);
		HBox addProductPane = new HBox(10);
		VBox textFieldsAndButtons = new VBox(5);
		VBox partsAdd = new VBox(10);
		
		GridPane textFields = new GridPane();
		textFields.setHgap(3);
		textFields.setVgap(5);
		
		textFields.add(new Label("ID"),0,0);
		textFields.add(new Label("Name"),0,1);
		textFields.add(new Label("Inv"),0,2);
		textFields.add(new Label("Price/Cost"),0,3);
		textFields.add(new Label("Max"),0,4);
		textFields.add(new Label("Min"),2,4);
		
		TextField idTextField = new TextField("Auto-Gen, Disabled");
		TextField nameTextField = new TextField();
		TextField invTextField = new TextField();
		TextField priceTextField = new TextField();
		TextField maxTextField = new TextField();
		TextField minTextField = new TextField();
		
		idTextField.setDisable(true);
		
		textFields.add(idTextField,1,0);
		textFields.add(nameTextField,1,1);
		textFields.add(invTextField,1,2);
		textFields.add(priceTextField,1,3);
		textFields.add(maxTextField,1,4);
		textFields.add(minTextField,3,4);
		
		
		TableView<Part> mainPartView = getPartsTable();
		TableView<Part> subPartView = getPartsTable();
		
		Product newProduct = new Product(0, null, 0, 0, 0, 0);
		
		subPartView.setItems(newProduct.getAllAssociatedParts());
		
		Button addPart = new Button("Add");
		Button removePart = new Button("Remove");
		
		addPart.setOnAction(e->{
			newProduct.addAssociatedPart(mainPartView.getSelectionModel().getSelectedItem());
		});
		
		removePart.setOnAction(e->{
			newProduct.deleteAssociatedPart(subPartView.getSelectionModel().getSelectedItem());
		});
		
		partsAdd.getChildren().addAll(getPartSearchBox(mainPartView),mainPartView,addPart,subPartView,removePart);
		
		HBox bottomButtons = new HBox(5);
		bottomButtons.setAlignment(Pos.CENTER);
		
		Button save = new Button("Save");
		Button cancel = new Button("Cancel");
		
		bottomButtons.getChildren().addAll(save, cancel);
		
		cancel.setOnAction(e->{
			addProductStage.close();
		});
		
		//don't look at this. I know it's bad but I didn't know that else to do :/
		save.setOnAction(e->{
			int inv = 0, min = 0, max = 0, maker = 0;
			double price = 0;
			try {
				inv = Integer.parseInt(invTextField.getText());
				try {
					price = Double.parseDouble(priceTextField.getText());
					try {
						max = Integer.parseInt(maxTextField.getText());
						try {
							min = Integer.parseInt(minTextField.getText());
							try {
								if(max<min) {
									throw new Exception();
								}
								try {
									if(inv<min||inv>max) {
										throw new Exception();
									}
									newProduct.setId(Math.abs(rand.nextInt()%10000));
									newProduct.setMax(max);
									newProduct.setMin(min);
									newProduct.setName(nameTextField.getText());
									newProduct.setPrice(price);
									newProduct.setStock(inv);
									
									
									Inventory.addProduct(newProduct);
									
									addProductStage.close();
								}catch(Exception ex) {
									Alert a = new Alert(AlertType.INFORMATION);
									a.setTitle("Invalid Data");
									a.setHeaderText("Invalid Data");
									a.setContentText("Stock needs to be between max and min");
									a.showAndWait();
								}
							}catch(Exception ex) {
								Alert a = new Alert(AlertType.INFORMATION);
								a.setTitle("Invalid Data");
								a.setHeaderText("Invalid Data");
								a.setContentText("Min needs to be less than Max");
								a.showAndWait();
							}
						}catch(Exception ex) {
							Alert a = new Alert(AlertType.INFORMATION);
							a.setTitle("Invalid Data");
							a.setHeaderText("Invalid Data");
							a.setContentText("Text Field \"Min\" needs to be a number");
							a.showAndWait();
						}
					}catch(Exception ex) {
						Alert a = new Alert(AlertType.INFORMATION);
						a.setTitle("Invalid Data");
						a.setHeaderText("Invalid Data");
						a.setContentText("Text Field \"Max\" needs to be a number");
						a.showAndWait();
					}
				}catch(Exception ex) {
					Alert a = new Alert(AlertType.INFORMATION);
					a.setTitle("Invalid Data");
					a.setHeaderText("Invalid Data");
					a.setContentText("Text Field \"Price\" needs to be a number");
					a.showAndWait();
				}
			}catch(Exception ex) {
				Alert a = new Alert(AlertType.INFORMATION);
				a.setTitle("Invalid Data");
				a.setHeaderText("Invalid Data");
				a.setContentText("Text Field \"Stock\" needs to be a number");
				a.showAndWait();
			}
		});
		
		textFieldsAndButtons.getChildren().addAll(textFields);
		
		addProductPane.getChildren().addAll(textFieldsAndButtons,partsAdd);
		addProductPane.setAlignment(Pos.CENTER);
		
		main.getChildren().addAll(new Label("Add Products: "), addProductPane, bottomButtons);
		main.setAlignment(Pos.CENTER);
		return main;
	}

	private Node getPartsPane() {
		VBox partsPane = new VBox();
		
		TableView<Part> partsTable = getPartsTable();
		
		partsPane.getChildren().addAll(new Label("Parts:"),getPartSearchBox(partsTable), partsTable, getPartButtonArray(partsTable));
		return partsPane;
	}
	
	private TableView<Part> getPartsTable() {
		TableView<Part> partsTable = new TableView<Part>(Inventory.getAllParts());
		partsTable.setPrefWidth(400);
		
		//Inventory.addPart(new InHouse(0, "help part", 5, 0, 0, 10, 0));
		
		TableColumn<Part, String> partIdCol = new TableColumn<Part, String>("Part ID");
		partIdCol.setCellValueFactory(new PropertyValueFactory("id"));
		TableColumn<Part, String> partNameCol = new TableColumn<Part, String>("Part Name");
		partNameCol.setCellValueFactory(new PropertyValueFactory("name"));
		TableColumn<Part, String> partStockCol = new TableColumn<Part, String>("Inventory Level");
		partStockCol.setCellValueFactory(new PropertyValueFactory("stock"));
		TableColumn<Part, String> partPriceCol = new TableColumn<Part, String>("Price / Cost Per Unit");
		partPriceCol.setCellValueFactory(new PropertyValueFactory("price"));
		
		partsTable.getColumns().addAll(partIdCol, partNameCol, partStockCol, partPriceCol);
		
		return partsTable;
	}

	private Node getPartSearchBox(TableView<Part> partsTable) {
		HBox searchBox = new HBox();
		TextField search = new TextField();
		search.textProperty().addListener((obs, oldText, newText) ->{
			if(!(search.getText() == "")) {
				if(!(search.getText().isEmpty())) {
					try {
						ObservableList<Part> temp = FXCollections.observableArrayList();
						Part part = Inventory.lookupPart(Integer.parseInt(search.getText()));
						if(part != null) {
							temp.add(part);
							partsTable.setItems(temp);
						}else {
							partsTable.setItems(FXCollections.observableArrayList());
							partsTable.setPlaceholder(new Label("No Item found in search"));
						}
					}catch(Exception ex) {
						partsTable.setItems(Inventory.lookupPart(search.getText()));
						if(!Inventory.lookupProduct(search.getText()).isEmpty()) {
							partsTable.setPlaceholder(new Label("No Item found in search"));
						}
					}
				}else {
					partsTable.setItems(Inventory.getAllParts());
				}
			}
		});
		
		searchBox.getChildren().addAll(search);
		
		return searchBox;
	}

	private Node getPartButtonArray(TableView partsTable) {
		HBox buttons = new HBox(10);
		
		Button add = new Button("Add");
		Button modify = new Button("Modify");
		Button delete = new Button("Delete");
		
		delete.setOnAction(e->{
			if(Inventory.getAllParts().size() != 0) {
				Alert a = new Alert(AlertType.CONFIRMATION);
				a.setHeaderText("Are you sure?");
				a.setContentText("Are you sure you wish to delete this product?");
				if(a.showAndWait().get() == ButtonType.OK) {
					Inventory.deletePart((Part) partsTable.getSelectionModel().getSelectedItem());
				}
			}
		});
		
		add.setOnAction(e->partAddAction());
		
		modify.setOnAction(e->{
			if(partsTable.getSelectionModel().getSelectedItem() != null) {
				Part selectedPart = (Part) partsTable.getSelectionModel().getSelectedItem();
				int selectedPartIndex = partsTable.getSelectionModel().getSelectedIndex();
				partModifyAction(selectedPart, selectedPartIndex);
			}
		});
		
		buttons.getChildren().addAll(add,modify,delete);
		return buttons;
	}

	private void partModifyAction(Part selectedPart, int selectedPartIndex) {
		Stage modifyPartStage = new Stage();
		modifyPartStage.setScene(new Scene(getModifyPartPane(modifyPartStage, selectedPart, selectedPartIndex),500,250));
		
		modifyPartStage.show();
	}

	private Parent getModifyPartPane(Stage modifyPartStage, Part selectedPart, int selectedPartIndex) {
		VBox modifyPartPane = new VBox(5);
		
		HBox radioButtons = new HBox(5);
		Label makerLabel = new Label("Machine ID");
		
		ToggleGroup group = new ToggleGroup();
		RadioButton inHouse = new RadioButton("In House");
		RadioButton outSourced = new RadioButton("Out Sourced");
		inHouse.setToggleGroup(group);
		outSourced.setToggleGroup(group);
		
		radioButtons.getChildren().addAll(inHouse, outSourced);
		
		inHouse.setOnAction(e->{
			makerLabel.setText("Machine ID");
		});
		outSourced.setOnAction(e->{
			makerLabel.setText("Company");
		});
		
		GridPane textFields = new GridPane();
		textFields.setHgap(3);
		textFields.setVgap(5);
		
		textFields.add(new Label("ID"),0,0);
		textFields.add(new Label("Name"),0,1);
		textFields.add(new Label("Inv"),0,2);
		textFields.add(new Label("Price/Cost"),0,3);
		textFields.add(new Label("Max"),0,4);
		textFields.add(makerLabel,0,5);
		textFields.add(new Label("Min"),2,4);
		
		TextField idTextField = new TextField(selectedPart.getId()+"");
		TextField nameTextField = new TextField();
		TextField invTextField = new TextField();
		TextField priceTextField = new TextField();
		TextField maxTextField = new TextField();
		TextField minTextField = new TextField();
		TextField makerTextField = new TextField();
		
		idTextField.setDisable(true);
		
		idTextField.setText(selectedPart.getId()+"");
		nameTextField.setText(selectedPart.getName());
		invTextField.setText(selectedPart.getStock()+"");
		priceTextField.setText(selectedPart.getPrice()+"");
		maxTextField.setText(selectedPart.getMax()+"");
		minTextField.setText(selectedPart.getMin()+"");
		
		if(selectedPart instanceof InHouse ) {
			inHouse.setSelected(true);
			makerLabel.setText("Machine ID");
			makerTextField.setText(((InHouse) selectedPart).getMachineId()+"");
		}else {
			outSourced.setSelected(true);
			makerLabel.setText("Company");
			makerTextField.setText(((Outsourced)selectedPart).getCompanyName());
		}
		
		
		textFields.add(idTextField,1,0);
		textFields.add(nameTextField,1,1);
		textFields.add(invTextField,1,2);
		textFields.add(priceTextField,1,3);
		textFields.add(maxTextField,1,4);
		textFields.add(makerTextField,1,5);
		textFields.add(minTextField,3,4);
		
		HBox bottomButtons = new HBox();
		bottomButtons.setAlignment(Pos.CENTER);
		
		Button save = new Button("Save");
		Button cancel = new Button("Cancel");
		
		bottomButtons.getChildren().addAll(save, cancel);
		
		cancel.setOnAction(e->{
			modifyPartStage.close();
		});
		
		//don't look at this. I know it's bad but I didn't know that else to do :/
		save.setOnAction(e->{
			int inv = 0, min = 0, max = 0, maker = 0;
			double price = 0;
			try {
				inv = Integer.parseInt(invTextField.getText());
				try {
					price = Double.parseDouble(priceTextField.getText());
					try {
						max = Integer.parseInt(maxTextField.getText());
						try {
							min = Integer.parseInt(minTextField.getText());
							try {
								if (max<min) {
									throw new Exception();
								}
								try {
									if(inv<min||inv>max) {
										throw new Exception();
									}
									Part newPart = null;
									if(inHouse.isSelected()) {
										
										try {
											maker = Integer.parseInt(makerTextField.getText());
											
											newPart = new InHouse(selectedPart.getId(), nameTextField.getText(), price, inv, min, max, maker);
											
											Inventory.updatePart(selectedPartIndex, newPart);
												
											modifyPartStage.close();
										}catch(Exception ex) {
											Alert a = new Alert(AlertType.INFORMATION);
											a.setTitle("Invalid Data");
											a.setHeaderText("Invalid Data");
											a.setContentText("Text Field \"Machine ID\" needs to be a number");
											a.showAndWait();
										}
									}else {
										newPart = new Outsourced(0, nameTextField.getText(), price, inv, min, max, makerTextField.getText());
										Inventory.updatePart(selectedPartIndex, newPart);
										
										modifyPartStage.close();
									}
								}catch(Exception ex) {
									Alert a = new Alert(AlertType.INFORMATION);
									a.setTitle("Invalid Data");
									a.setHeaderText("Invalid Data");
									a.setContentText("Stock needs to be between max and min");
									a.showAndWait();
								}
							}catch (Exception ex) {
								Alert a = new Alert(AlertType.INFORMATION);
								a.setTitle("Invalid Data");
								a.setHeaderText("Invalid Data");
								a.setContentText("Min needs to be less than Max");
								a.showAndWait();
							}
						}catch(Exception ex) {
							Alert a = new Alert(AlertType.INFORMATION);
							a.setTitle("Invalid Data");
							a.setHeaderText("Invalid Data");
							a.setContentText("Text Field \"Min\" needs to be a number");
							a.showAndWait();
						}
					}catch(Exception ex) {
						Alert a = new Alert(AlertType.INFORMATION);
						a.setTitle("Invalid Data");
						a.setHeaderText("Invalid Data");
						a.setContentText("Text Field \"Max\" needs to be a number");
						a.showAndWait();
					}
				}catch(Exception ex) {
					Alert a = new Alert(AlertType.INFORMATION);
					a.setTitle("Invalid Data");
					a.setHeaderText("Invalid Data");
					a.setContentText("Text Field \"Price\" needs to be a number");
					a.showAndWait();
				}
			}catch(Exception ex) {
				Alert a = new Alert(AlertType.INFORMATION);
				a.setTitle("Invalid Data");
				a.setHeaderText("Invalid Data");
				a.setContentText("Text Field \"Stock\" needs to be a number");
				a.showAndWait();
			}
		});
		
		modifyPartPane.getChildren().addAll(new Label("Modify Part: "),radioButtons, textFields, bottomButtons);
		modifyPartPane.setAlignment(Pos.CENTER);
		
		return modifyPartPane;
	}

	private void partAddAction() {
		Stage addPartStage = new Stage();
		addPartStage.setScene(new Scene(getAddPartPane(addPartStage),500,250));
		
		addPartStage.show();
	}

	private Parent getAddPartPane(Stage addPartStage) {
		Random rand = new Random();
		VBox addPartPane = new VBox(5);
		
		HBox radioButtons = new HBox(5);
		Label makerLabel = new Label("Machine ID");
		
		ToggleGroup group = new ToggleGroup();
		RadioButton inHouse = new RadioButton("In House");
		RadioButton outSourced = new RadioButton("Out Sourced");
		inHouse.setToggleGroup(group);
		outSourced.setToggleGroup(group);
		inHouse.setSelected(true);
		radioButtons.getChildren().addAll(inHouse, outSourced);
		
		inHouse.setOnAction(e->{
			makerLabel.setText("Machine ID");
		});
		outSourced.setOnAction(e->{
			makerLabel.setText("Company");
		});
		
		GridPane textFields = new GridPane();
		textFields.setHgap(3);
		textFields.setVgap(5);
		
		textFields.add(new Label("ID"),0,0);
		textFields.add(new Label("Name"),0,1);
		textFields.add(new Label("Inv"),0,2);
		textFields.add(new Label("Price/Cost"),0,3);
		textFields.add(new Label("Max"),0,4);
		textFields.add(makerLabel,0,5);
		textFields.add(new Label("Min"),2,4);
		
		TextField idTextField = new TextField("Auto-Gen, Disabled");
		TextField nameTextField = new TextField();
		TextField invTextField = new TextField();
		TextField priceTextField = new TextField();
		TextField maxTextField = new TextField();
		TextField minTextField = new TextField();
		TextField makerTextField = new TextField();
		
		idTextField.setDisable(true);
		
		textFields.add(idTextField,1,0);
		textFields.add(nameTextField,1,1);
		textFields.add(invTextField,1,2);
		textFields.add(priceTextField,1,3);
		textFields.add(maxTextField,1,4);
		textFields.add(makerTextField,1,5);
		textFields.add(minTextField,3,4);
		
		HBox bottomButtons = new HBox();
		bottomButtons.setAlignment(Pos.CENTER);
		
		Button save = new Button("Save");
		Button cancel = new Button("Cancel");
		
		bottomButtons.getChildren().addAll(save, cancel);
		
		cancel.setOnAction(e->{
			addPartStage.close();
		});
		
		//don't look at this. I know it's bad but it works
		save.setOnAction(e->{
			int inv = 0, min = 0, max = 0, maker = 0;
			double price = 0;
			try {
				inv = Integer.parseInt(invTextField.getText());
				try {
					price = Double.parseDouble(priceTextField.getText());
					try {
						max = Integer.parseInt(maxTextField.getText());
						try {
							min = Integer.parseInt(minTextField.getText());
							try {
								if(max<min) {
									throw new Exception();
								}
								try {
									if(inv<min||inv>max) {
										throw new Exception();
									}
									Part newPart = null;
									if(inHouse.isSelected()) {
										
										try {
											maker = Integer.parseInt(makerTextField.getText());
											
											newPart = new InHouse(Math.abs(rand.nextInt()%10000), nameTextField.getText(), price, inv, min, max, maker);
											
											Inventory.addPart(newPart);
											
											addPartStage.close();
										}catch(Exception ex) {
											Alert a = new Alert(AlertType.INFORMATION);
											a.setTitle("Invalid Data");
											a.setHeaderText("Invalid Data");
											a.setContentText("Text Field \"Machine ID\" needs to be a number");
											a.showAndWait();
										}
									}else {
										newPart = new Outsourced(Math.abs(rand.nextInt()%10000), nameTextField.getText(), price, inv, min, max, makerTextField.getText());
										Inventory.addPart(newPart);
										
										addPartStage.close();
									}
								}catch(Exception ex) {
									Alert a = new Alert(AlertType.INFORMATION);
									a.setTitle("Invalid Data");
									a.setHeaderText("Invalid Data");
									a.setContentText("Stock needs to be between max and min");
									a.showAndWait();
								}
							}catch (Exception ex) {
								Alert a = new Alert(AlertType.INFORMATION);
								a.setTitle("Invalid Data");
								a.setHeaderText("Invalid Data");
								a.setContentText("Min needs to be less than Max");
								a.showAndWait();
							}
						}catch(Exception ex) {
							Alert a = new Alert(AlertType.INFORMATION);
							a.setTitle("Invalid Data");
							a.setHeaderText("Invalid Data");
							a.setContentText("Text Field \"Min\" needs to be a number");
							a.showAndWait();
						}
					}catch(Exception ex) {
						Alert a = new Alert(AlertType.INFORMATION);
						a.setTitle("Invalid Data");
						a.setHeaderText("Invalid Data");
						a.setContentText("Text Field \"Max\" needs to be a number");
						a.showAndWait();
					}
				}catch(Exception ex) {
					Alert a = new Alert(AlertType.INFORMATION);
					a.setTitle("Invalid Data");
					a.setHeaderText("Invalid Data");
					a.setContentText("Text Field \"Price\" needs to be a number");
					a.showAndWait();
				}
			}catch(Exception ex) {
				Alert a = new Alert(AlertType.INFORMATION);
				a.setTitle("Invalid Data");
				a.setHeaderText("Invalid Data");
				a.setContentText("Text Field \"Stock\" needs to be a number");
				a.showAndWait();
			}
		});
		
		addPartPane.getChildren().addAll(new Label("Add Part: "),radioButtons, textFields, bottomButtons);
		addPartPane.setAlignment(Pos.CENTER);
		
		return addPartPane;
	}

}

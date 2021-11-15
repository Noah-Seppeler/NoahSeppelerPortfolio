import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * This is the inventory class of the application. It controls all data for the inventory and controls searching the inventory
 * 
 * One runtime error that I faced while coding this section was in the lookupProdcuts method 
 * When I copied the code from the lookupParts method I forgot to change the if statement inside of for loop to look in the allProducts array, not the allParts array.
 * So I kept on getting the index out of bounds error when there was no parts and I tried to search in the products table
 * 
 * One thing that could be added to this to improve functionality is to have a way to set the allParts and allProducts lists to already made lists instead of just adding parts to it.
 * 
 * @author Noah Seppeler
 */

public class Inventory {
	private static ObservableList<Part> allParts = FXCollections.observableArrayList();
	private static ObservableList<Product> allProducts = FXCollections.observableArrayList();
	
	/**
	 * Adds a part to the allParts list
	 * @param newPart the new part to add to the inventory
	 */
	public static void addPart(Part newPart) {
		allParts.add(newPart);
	}
	
	/**
	 * Adds a product to the allProducts list
	 * @param newProduct the new product to add to the inventory
	 */
	public static void addProduct(Product newProduct) {
		allProducts.add(newProduct);
	}
	
	/**
	 * looks up and returns a part based on partId
	 * @param partId the partId to look up
	 * @return the part with matching partId
	 */
	public static Part lookupPart(int partId) {
		for(int i=0;i<allParts.size();i++) {
			if(allParts.get(i).getId() == partId) {
				return allParts.get(i);
			}
		}
		
		return null;
	}
	
	/**
	 * looks up and returns part based on string given
	 * @param partName part name to look up
	 * @return the part with name containing the given string
	 */
	public static ObservableList<Part> lookupPart(String partName) {
		ObservableList<Part> tempList = FXCollections.observableArrayList();
		for(int i=0;i<allParts.size();i++) {
			if(allParts.get(i).getName().contains(partName)) {
				tempList.add(allParts.get(i));
			}
		}
		
		return tempList;
		
	}
	
	/**
	 * looks up and returns a product based on productId
	 * @param productId the productId to look up
	 * @return the product with matching productId
	 */
	public static Product lookupProduct(int productId) {
		for(int i=0;i<allProducts.size();i++) {
			if(allProducts.get(i).getId() == productId) {
				return allProducts.get(i);
			}
		}
		
		return null;
		
	}
	
	/**
	 * looks up and returns product based on string given
	 * @param productName product name to look up
	 * @return the product with name containing the given string
	 */
	public static ObservableList<Product> lookupProduct(String productName) {
		ObservableList<Product> tempList = FXCollections.observableArrayList();
		for(int i=0;i<allProducts.size();i++) {
			if(allProducts.get(i).getName().contains(productName)) {
				tempList.add(allProducts.get(i));
			}
		}
		
		return tempList;
		
	}
	
	/**
	 * takes index and part and replaces part at given index with new part
	 * @param index index of part to update
	 * @param selectedPart part to replace with
	 */
	public static void updatePart(int index, Part selectedPart) {
		allParts.remove(index);
		allParts.add(selectedPart);
	}
	
	/**
	 * takes index and product and replaces product at given index with new product
	 * @param index index of product to update
	 * @param selectedProduct product to replace with
	 */
	public static void updateProduct(int index, Product selectedProduct) {
		allProducts.remove(index);
		allProducts.add(selectedProduct);
	}
	
	/**
	 * delete specific part from allParts list
	 * @param selectedPart part to delete
	 * @return boolean based on if part was deleted
	 */
	public static boolean deletePart(Part selectedPart) {
		return allParts.remove(selectedPart);
	}
	
	/**
	 * delete specific product from allProducts list
	 * @param selectedProduct product to delete
	 * @return boolean based on if product was deleted or not
	 */
	public static boolean deleteProduct(Product selectedProduct) {
		return allProducts.remove(selectedProduct);
	}

	/**
	 * 
	 * @return returns the allParts ObservableList
	 */
	public static ObservableList<Part> getAllParts() {
		return allParts;
	}

	/**
	 * 
	 * @return returns the allProducts ObservableList
	 */
	public static ObservableList<Product> getAllProducts() {
		return allProducts;
	}
	
	
}

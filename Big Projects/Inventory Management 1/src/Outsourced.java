/**
 * This class is an extension of the Parts class and handles the creation of Outsourced Parts
 * I did not have any trouble with this class and therefore no errors were generated
 * I do not have any suggestions to improve this class. It is very basic
 * 
 * @author Noah Seppeler
 *
 */

public class Outsourced extends Part {

	private String companyName;
	
	public Outsourced(int id, String name, double price, int stock, int min, int max, String companyName) {
		super(id, name, price, stock, min, max);
		this.setCompanyName(companyName);
	}

	/**
	 * 
	 * @return return the companyName parameter
	 */
	public String getCompanyName() {
		return companyName;
	}

	/**
	 * Change the companyName parameter
	 * @param companyName company name to change to
	 */
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

}

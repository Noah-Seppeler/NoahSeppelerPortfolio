/**
 * This class is an extension of the Parts class and handles the creation of InHouse Parts
 * I did not have any trouble with this class and therefore no errors were generated
 * I do not have any suggestions to improve this class. It is very basic
 * 
 * @author Noah Seppeler
 *
 */

public class InHouse extends Part {

	private int machineId;
	
	public InHouse(int id, String name, double price, int stock, int min, int max, int machineId) {
		super(id, name, price, stock, min, max);
		this.setMachineId(machineId);
	}
	
	/**
	 * 
	 * @return return the machineId parameter
	 */
	public int getMachineId() {
		return machineId;
	}
	
	/**
	 * Change the machineId parameter
	 * @param machineId machine ID to change to
	 */
	public void setMachineId(int machineId) {
		this.machineId = machineId;
	}
	
}

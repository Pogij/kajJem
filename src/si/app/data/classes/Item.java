package si.app.data.classes;

/**
 * Class used to parse the data from string into a list.
 * @author Matevž Pogačar
 *
 */
public class Item {
	private String itemName;
	private String description;
	private int healthImpact;
	
	
	
	public Item(String itemName, String description, int healthImpact) {
		this.itemName = itemName;
		this.description = description;
		this.healthImpact = healthImpact;
	}
	
	public String getItemName() {
		return itemName;
	}
	
	public String getDescription() {
		return description;
	}
	
	public int getHealthImpact() {
		return healthImpact;
	}
}
package Calculator;

/*  CLASS Item
 *  @Zanice
 * 
 * 	This class represents an Item object, which is instantiated
 * 	for each item with item level in the application. The Item
 * 	object stores an item's label/name, the current item level
 * 	value, the Edit Mode item level value, and its status of
 * 	being enabled and considered in the item level sum.
 */

public class Item {
	//Label/Name variable
	private String label;
	
	//Item level value variables
	private int current;
	private int edit;
	
	//Enabled variable, to determine if level values are part of the sum.
	private boolean enabled;
	
	public Item(String itemLabel, boolean itemEnabeled) {
		//Initialize variables.
		label = itemLabel;
		current = 0;
		edit = 0;
		enabled = itemEnabeled;
	}
	
	//Get/Set methods for 'label'.
	public String getLabel() { return label; }
	
	//Get/Set methods for 'current'.
	public int getCurrent() { return current; }
	public void setCurrent(int c) { current = c; }
	
	//Get/Set methods for 'edit'.
	public int getEdit() { return edit; }
	public void setEdit(int e) { edit = e; }
	
	//Get/Set methods for 'enabled'.
	public boolean getEnabled() { return enabled; }
	public void setEnabled(boolean e) { enabled = e; }
}

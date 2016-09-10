
public class NetworkOutput {
	
	private int id;
	private String name;
	
	private boolean active;
	private boolean forceInactive;
	private boolean forceActive;

	protected NetworkOutput(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	protected void resetOutput() {
		active = false;
		forceInactive = false;
		forceActive = false;
	}
	
	protected void activateOutput() {
		active = true;
	}
	
	protected void activateForceInactive() {
		forceInactive = true;
	}
	
	protected void activateForceActive() {
		forceActive = true;
	}
	
	protected boolean getOutput() {
		boolean output = active;
		
		if (forceInactive) {
			output = false;
		}
		
		if (forceActive) {
			output = true;
		}
		return output;
	}
	
	protected int getId() {
		return id;
	}
	
	protected String getName() {
		return name;
	}

	public String getSaveString() {
		return id  + ":" + name;
	}
}


public class NetworkInput {
	
	private int id;
	
	private String name;
	
	private boolean value[];
	
	private int channels;
	
	protected NetworkInput(int id, int channels) {
		this.id = id;
		this.channels = channels;
	}
	
	protected NetworkInput(int id, int channels, String name) {
		this.id = id;
		this.channels = channels;
		this.name = name;
	}
	
	protected void setName(String name) {
		this.name = name;
	}
	
	protected void setValue(boolean value[]) {
		
		if (value.length != channels) {
			throw new IllegalArgumentException("Wrong number of channels");
		}
		
		this.value = value;
	}
	
	protected boolean[] getValue() {
		return value;
	}
	
	protected int getId() {
		return id;
	}
	
	protected String getName() {
		return name;
	}
	
	protected String getSaveString() {
		return id + ":" + name;
	}
}

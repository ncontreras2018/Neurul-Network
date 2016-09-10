
public class Neuron {

	public enum NeuronType {
		NORMAL, FORCE_INACTIVE, FORCE_ACTIVE;
	}

	private NeuronType type;
	private NetworkInput input;
	private NetworkOutput output;
	
	private int channel;

	protected Neuron(NeuronType type, NetworkInput input, NetworkOutput output, int channel) {
		this.type = type;
		this.input = input;
		this.output = output;
		this.channel = channel;
	}

	protected void updateOutput() {

		if (input.getValue()[channel]) {

			if (type == NeuronType.NORMAL) {
				output.activateOutput();
			} else if (type == NeuronType.FORCE_INACTIVE) {
				output.activateForceInactive();
			} else {
				output.activateForceActive();
			}
		}
	}
	
	public NetworkInput getInput() {
		return input;
	}
	
	public NetworkOutput getOutput() {
		return output;
	}
	
	protected String getSaveString() {
		return type + ":" + input.getId() + ":" + output.getId() + ":" + channel;
	}
}

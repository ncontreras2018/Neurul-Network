import java.util.ArrayList;

public class NeuralNetwork {
	
	public enum NetworkType {
		NORMAL, INACTIVE_OVERRIDE, DUAL_WAY_OVERRIDE;
	}
	
	private boolean training;
	private boolean running;

	private final ArrayList<Neuron> neurons;
	private final ArrayList<NetworkInput> networkInputs;
	private final ArrayList<NetworkOutput> networkOutputs;
	
	private NetworkType type;
	
	private int inputSize;
	
	private int channels;

	public NeuralNetwork(NetworkType type, int channels) {
		neurons = new ArrayList<Neuron>();
		networkInputs = new ArrayList<NetworkInput>();
		networkOutputs = new ArrayList<NetworkOutput>();
		
		this.type = type;
		this.channels = channels;
	}
	
	public NeuralNetwork(NetworkState state) {
		neurons = (ArrayList<Neuron>) state.neurons.clone();
		networkInputs = (ArrayList<NetworkInput>) state.networkInputs.clone();
		networkOutputs = (ArrayList<NetworkOutput>) state.networkOutputs.clone();
		inputSize = state.inputSize;
		this.type = state.type;
		this.channels = state.channels;
	}

	public void setupInputs(int amountOfData) {
		
		inputSize = amountOfData;

		networkInputs.clear();

		for (int i = 0; i < amountOfData; i++) {
			networkInputs.add(new NetworkInput(i, channels));
		}
	}
	
	public void setupFriendlyInputNames(String[] inputNames) {
		
		if (inputNames.length != inputSize) {
			throw new IllegalArgumentException("The array of names is the wrong length");
		}
		
		for (int i = 0; i < inputSize; i++) {
			networkInputs.get(i).setName(inputNames[i]);
		}
		
	}
	
	public void beginTrainingSession() {
		failIfTraining();
		failIfRunning();
		training = true;
	}
	
	/** Pass a 2d array of booleans in order to update the network and calculate new outputs.<br>
	 * <br><b>WARNING!!!</b><br>
	 * This is not an 2d array of the input, but rather a 1d array, with the second dimension representing the different channels
	 */
	public void updateData(boolean[][] trainingData) {
		
		if (trainingData.length != inputSize) {
			throw new IllegalStateException("The data is the wrong size");
		}
		
		for (int i = 0; i < networkOutputs.size(); i++) {
			networkOutputs.get(i).resetOutput();
		}
		
		for (int i = 0; i < inputSize; i++) {
			networkInputs.get(i).setValue(trainingData[i]);
		}
		
		for (int i = 0; i < neurons.size(); i++) {
			neurons.get(i).updateOutput();
		}
	}
	
	public void endTrainingSession() {
		failIfNotTraining();
		failIfRunning();
		training = false;
	}
	
	public void beginRunning() {
		failIfTraining();
		failIfRunning();
		running = true;
	}
	
	public void registerOutput(String name) {
		networkOutputs.add(new NetworkOutput(networkOutputs.size(), name));
	}
	
	public void registerOutputs(String[] names) {
		for (int i = 0; i < names.length; i++) {
			networkOutputs.add(new NetworkOutput(networkOutputs.size(), names[i]));
		}
	}
	
	public boolean getOutputValue(String name) {
		
		for (int i = 0; i < networkOutputs.size(); i++) {
			if (networkOutputs.get(i).getName().equals(name)) {
				return networkOutputs.get(i).getOutput();
			}
		}
		
		throw new IllegalAccessError("This data does not exist");
	}
	
	public ArrayList<NetworkOutput> getNetworkOutputs() {
		return networkOutputs;
	}
	
	public ArrayList<NetworkInput> getNetworkInputs() {
		return networkInputs;
	}
	
	public int getNumNetworkOutputs() {
		return networkOutputs.size();
	}
	
	public int getNetworkInputSize() {
		return inputSize;
	}
	
	public ArrayList<Neuron> getNeurons() {
		return neurons;
	}
	
	protected void addNeuron(Neuron neuron) {
		neurons.add(neuron);
	}
	
	private void failIfTraining() {
		if (training) {
			throw new IllegalStateException("The network is not training");	
		}
	}
	
	private void failIfNotTraining() {
		if (!training) {
			throw new IllegalStateException("The network is not training");	
		}
	}
	
	private void failIfRunning() {
		if (running) {
			throw new IllegalStateException("The network is running");	
		}
	}
	
	private void failIfNotRunning() {
		if (!running) {
			throw new IllegalStateException("The network is not running");	
		}
	}
	
	public boolean isRunning() {
		return running;
	}
	
	public boolean isTraining() {
		return training;
	}
	
	public NetworkState getState() {
		NetworkState state = new NetworkState();
		
		state.neurons = neurons;
		state.networkInputs = networkInputs;
		state.networkOutputs = networkOutputs;
		
		state.inputSize = inputSize;
		
		state.type = type;
		
		state.channels = channels;
		
		return state;
	}
	
	public NetworkType getNetworkType() {
		return type;
	}
	
	public int getNumChannels() {
		return channels;
	}
}

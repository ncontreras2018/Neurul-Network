public class NetworkTrainer {

	private NeuralNetwork curNetwork;
	private NetworkState curParentState;

	private NetworkState bestChildState;
	private double bestChildScore = Double.MIN_VALUE;

	public NetworkTrainer(NeuralNetwork network) {
		this.curNetwork = network;
		curParentState = network.getState();
	}

	/**
	 * Forwards the training data to the network<br>
	 * See the important warning in NeuralNetwork's
	 * {@link NeuralNetwork#updateData(boolean[][]) updateData()} method.
	 */
	public void trainNetwork(boolean data[][]) {

		if (!curNetwork.isTraining()) {
			curNetwork = new NeuralNetwork(curParentState);
			generateRandomNeuron();
			curNetwork.beginTrainingSession();
		}

		curNetwork.updateData(data);
	}

	public void endAttempt(double previousScore) {
		curNetwork.endTrainingSession();

		if (previousScore > bestChildScore) {
			bestChildState = curNetwork.getState();
			bestChildScore = previousScore;
		}
	}

	public void evolveNetwork() {

		if (curParentState.equals(bestChildState)) {
			System.out.println("NO BETTER CHILDREN FOUND, NOT EVOLVING!");
		}
		curNetwork = new NeuralNetwork(bestChildState);
		curParentState = curNetwork.getState();
		bestChildState = curParentState;
	}

	public NeuralNetwork getNetwork() {
		return curNetwork;
	}

	private void generateRandomNeuron() {

		double rand = Math.random();

		if (curNetwork.getNetworkType() == NeuralNetwork.NetworkType.NORMAL) {
			rand = 1;
		}
		
		if (curNetwork.getNetworkType() == NeuralNetwork.NetworkType.INACTIVE_OVERRIDE) {
			while (rand < 0.2) {
				rand = Math.random();
			}
		}

		Neuron.NeuronType type;

		if (rand < 0.2) {
			type = Neuron.NeuronType.FORCE_ACTIVE;
		} else if (rand < 0.4) {
			type = Neuron.NeuronType.FORCE_INACTIVE;
		} else {
			type = Neuron.NeuronType.NORMAL;
		}

		rand = Math.random();

		int randInt = (int) (rand * curNetwork.getNetworkInputSize());

		NetworkInput input = curNetwork.getNetworkInputs().get(randInt);

		rand = Math.random();

		randInt = (int) (rand * curNetwork.getNumNetworkOutputs());

		NetworkOutput output = curNetwork.getNetworkOutputs().get(randInt);

		rand = Math.random();

		randInt = (int) (rand * curNetwork.getNumChannels());

		curNetwork.addNeuron(new Neuron(type, input, output, randInt));

	}

	public NetworkState getParentState() {
		return curParentState;
	}
}

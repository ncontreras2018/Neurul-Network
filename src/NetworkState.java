import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class NetworkState {

	protected ArrayList<Neuron> neurons;
	protected ArrayList<NetworkInput> networkInputs;
	protected ArrayList<NetworkOutput> networkOutputs;

	protected int inputSize;

	protected NeuralNetwork.NetworkType type;

	protected int channels;

	protected NetworkState() {

	}

	public NetworkState(String filePath) throws IOException {

		networkInputs = new ArrayList<NetworkInput>();
		networkOutputs = new ArrayList<NetworkOutput>();
		neurons = new ArrayList<Neuron>();

		FileReader fileReader = new FileReader(filePath);

		BufferedReader bufferedReader = new BufferedReader(fileReader);

		String stringForm = bufferedReader.readLine();

		bufferedReader.close();

		type = NeuralNetwork.NetworkType.valueOf(stringForm.substring(0, stringForm.indexOf("$")));

		stringForm = stringForm.substring(stringForm.indexOf("$") + 1);
		
		channels = Integer.parseInt(stringForm.substring(0, stringForm.indexOf("%")));

		stringForm = stringForm.substring(stringForm.indexOf("%") + 1);

		inputSize = Integer.parseInt(stringForm.substring(0, stringForm.indexOf("&")));

		stringForm = stringForm.substring(stringForm.indexOf("&") + 1);

		while (stringForm.charAt(0) != '#') {

			String curInput = stringForm.substring(0, stringForm.indexOf("|"));

			stringForm = stringForm.substring(stringForm.indexOf("|") + 1);

			networkInputs.add(new NetworkInput(Integer.parseInt(curInput.substring(0, curInput.indexOf(":"))), channels,
					curInput.substring(curInput.indexOf(":") + 1)));
		}

		stringForm = stringForm.substring(1);

		while (stringForm.charAt(0) != '#') {

			String curOutput = stringForm.substring(0, stringForm.indexOf("|"));

			stringForm = stringForm.substring(stringForm.indexOf("|") + 1);

			networkOutputs.add(new NetworkOutput(Integer.parseInt(curOutput.substring(0, curOutput.indexOf(":"))),
					curOutput.substring(curOutput.indexOf(":") + 1)));
		}

		stringForm = stringForm.substring(1);

		while (stringForm.charAt(0) != '#') {

			String curNeuron = stringForm.substring(0, stringForm.indexOf("|"));

			stringForm = stringForm.substring(stringForm.indexOf("|") + 1);

			String type = curNeuron.substring(0, curNeuron.indexOf(':'));

			curNeuron = curNeuron.substring(curNeuron.indexOf(':') + 1);

			int inputId = Integer.parseInt(curNeuron.substring(0, curNeuron.indexOf(':')));

			curNeuron = curNeuron.substring(curNeuron.indexOf(':') + 1);

			int outputId = Integer.parseInt(curNeuron.substring(0, curNeuron.indexOf(':')));
			
			curNeuron = curNeuron.substring(curNeuron.indexOf(':') + 1);
			
			int channel = Integer.parseInt(curNeuron);

			neurons.add(new Neuron(Neuron.NeuronType.valueOf(type), networkInputs.get(inputId),
					networkOutputs.get(outputId), channel));
		}
	}

	public String getStringForm() {
		String saveString = type + "$" + channels + "%" + inputSize + "&";
		
		System.out.println("Writing inputs...");

		for (NetworkInput input : networkInputs) {
			saveString += input.getSaveString() + "|";
		}

		saveString += "#";
		
		System.out.println("Writing outputs...");

		for (NetworkOutput output : networkOutputs) {
			saveString += output.getSaveString() + "|";
		}

		saveString += "#";
		
		System.out.println("Writing neurons...");

		for (Neuron neuron : neurons) {
			saveString += neuron.getSaveString() + "|";
		}

		saveString += "#";

		return saveString;
	}

	public void saveToFile(String filePath) throws IOException {

		File target = new File(filePath);

		FileWriter fileWriter = new FileWriter(target);

		BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
		
		System.out.println("Writing file...");

		bufferedWriter.write(getStringForm());

		bufferedWriter.close();
		
		System.out.println("File writen");
	}
}

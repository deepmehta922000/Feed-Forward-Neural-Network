package network;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import data.Instance;

import util.Log;

import static java.lang.Math.sqrt;


public class NeuralNetwork {
    //this is the loss function for the output of the neural 
    //network, you will use this in PA1-3
    LossFunction lossFunction;
    Momentum momentum;
    

    //this is the total number of weights in the neural network
    int numberWeights;
    
    //layers contains all the nodes in the neural network
    Node[][] layers;

    public NeuralNetwork(int inputLayerSize, int[] hiddenLayerSizes, int outputLayerSize, LossFunction lossFunction) {
        this.lossFunction = lossFunction;
        //this.momentum = momentum;

        //the number of layers in the neural network is 2 plus the number of hidden layers,
        //one additional for the input, and one additional for the output.

        //create the outer array of the 2-dimensional array of nodes
        layers = new Node[hiddenLayerSizes.length + 2][];
        
        //we will progressively calculate the number of weights as we create the network. the
        //number of edges will be equal to the number of hidden nodes (each has a bias weight, but
        //the input and output nodes do not) plus the number of edges
        numberWeights = 0;

        Log.info("creating a neural network with " + hiddenLayerSizes.length + " hidden layers.");
        for (int layer = 0; layer < layers.length; layer++) {
            
            //determine the layer size depending on the layer number, 0 is the
            //input layer, and the last layer is the output layer, all others
            //are hidden layers
            int layerSize;
            NodeType nodeType;
            ActivationType activationType;
            if (layer == 0) {
                //this is the input layer
                layerSize = inputLayerSize;
                nodeType = NodeType.INPUT;
                activationType = ActivationType.LINEAR;
                Log.info("input layer " + layer + " has " + layerSize + " nodes.");

            } else if (layer < layers.length - 1) {
                //this is a hidden layer
                layerSize = hiddenLayerSizes[layer - 1];
                nodeType = NodeType.HIDDEN;
                activationType = ActivationType.TANH;
                Log.info("hidden layer " + layer + " has " + layerSize + " nodes.");

                //increment the number of weights by the number of nodes in
                //this hidden layer
                numberWeights += layerSize; 
            } else {
                //this is the output layer
                layerSize = outputLayerSize;
                nodeType = NodeType.OUTPUT;
                activationType = ActivationType.SIGMOID;
                Log.info("output layer " + layer + " has " + layerSize + " nodes.");
            }

            //create the layer with the right length and right node types
            layers[layer] = new Node[layerSize];
            for (int j = 0; j < layers[layer].length; j++) {
                layers[layer][j] = new Node(layer, j /*i is the node number*/, nodeType, activationType);
            }
        }
    }

    /**
     * This gets the number of weights in the NeuralNetwork, which should
     * be equal to the number of hidden nodes (1 bias per hidden node) plus 
     * the number of edges (1 bias per edge). It is updated whenever an edge 
     * is added to the neural network.
     *
     * @return the number of weights in the neural network.
     */
    public int getNumberWeights() {
        return numberWeights;
    }

    /**
     * This resets all the values that are modified in the forward pass and 
     * backward pass and need to be reset to 0 before doing another
     * forward and backward pass (i.e., all the non-weights/biases).
     */
    public void reset() {
        for (int layer = 0; layer < layers.length; layer++) {
            for (int number = 0; number < layers[layer].length; number++) {
                //call reset on each node in the network
                layers[layer][number].reset();
            }
        }
    }

    /**
     * This returns an array of every weight (including biases) in the NeuralNetwork.
     * This will be very useful in backpropagation and sanity checking.
     *
     * @throws NeuralNetworkException if numberWeights was not calculated correctly. This shouldn't happen.
     */
    public double[] getWeights() throws NeuralNetworkException {
        double[] weights = new double[numberWeights];

        //What we're going to do here is fill in the weights array
        //we just created by having each node set the weights starting
        //at the position variable we're creating. The Node.getWeights
        //method will set the weights variable passed as a parameter,
        //and then return the number of weights it set. We can then
        //use this to increment position so the next node gets weights
        //and puts them in the right position in the weights array.
        int position = 0;
        for (int layer = 0; layer < layers.length; layer++) {
            for (int nodeNumber = 0; nodeNumber < layers[layer].length; nodeNumber++) {
                int nWeights = layers[layer][nodeNumber].getWeights(position, weights);
                position += nWeights;

                if (position > numberWeights) {
                    throw new NeuralNetworkException("The numberWeights field of the NeuralNetwork was (" + numberWeights + ") but when getting the weights there were more hidden nodes and edges than numberWeights. This should not happen unless numberWeights is not being updated correctly.");
                }
            }
        }

        return weights;
    }


    /**
     * This sets every weight (including biases) in the NeuralNetwork, it sets them in
     * the same order that they are retreived by the getWeights method.
     * This will be very useful in backpropagation and sanity checking. 
     *
     * @throws NeuralNetworkException if numberWeights was not calculated correctly. This shouldn't happen.
     */
    public void setWeights(double[] newWeights) throws NeuralNetworkException {
        if (numberWeights != newWeights.length) {
            throw new NeuralNetworkException("Could not setWeights because the number of new weights: " + newWeights.length + " was not equal to the number of weights in the NeuralNetwork: " + numberWeights);
        }

        int position = 0;
        for (int layer = 0; layer < layers.length; layer++) {
            for (int nodeNumber = 0; nodeNumber < layers[layer].length; nodeNumber++) {
                int nWeights = layers[layer][nodeNumber].setWeights(position, newWeights);
                position += nWeights;

                if (position > numberWeights) {
                    throw new NeuralNetworkException("The numberWeights field of the NeuralNetwork was (" + numberWeights + ") but when setting the weights there were more hidden nodes and edges than numberWeights. This should not happen unless numberWeights is not being updated correctly.");
                }
            }
        }
    }

    /**
     * This returns an array of every weight (including biases) in the NeuralNetwork.
     * This will be very useful in backpropagation and sanity checking.
     *
     * @throws NeuralNetworkException if numberWeights was not calculated correctly. This shouldn't happen.
     */
    public double[] getDeltas() throws NeuralNetworkException {
        double[] deltas = new double[numberWeights];

        //What we're going to do here is fill in the deltas array
        //we just created by having each node set the deltas starting
        //at the position variable we're creating. The Node.getDeltas
        //method will set the deltas variable passed as a parameter,
        //and then return the number of deltas it set. We can then
        //use this to increment position so the next node gets deltas
        //and puts them in the right position in the deltas array.
        int position = 0;
        for (int layer = 0; layer < layers.length; layer++) {
            for (int nodeNumber = 0; nodeNumber < layers[layer].length; nodeNumber++) {
                int nDeltas = layers[layer][nodeNumber].getDeltas(position, deltas);
                position += nDeltas;

                if (position > numberWeights) {
                    throw new NeuralNetworkException("The numberWeights field of the NeuralNetwork was (" + numberWeights + ") but when getting the deltas there were more hidden nodes and edges than numberWeights. This should not happen unless numberWeights is not being updated correctly.");
                }
            }
        }

        return deltas;
    }


    /**
     * This adds edges to the NeuralNetwork, connecting each node
     * in a layer to each node in the subsequent layer
     */
    public void connectFully() throws NeuralNetworkException {
        //create outgoing edges from the input layer to the last hidden layer,
        //the output layer will not have outgoing edges
        for (int layer = 0; layer < layers.length - 1; layer++) {

            //iterate over the nodes in the current layer
            for (int inputNodeNumber = 0; inputNodeNumber < layers[layer].length; inputNodeNumber++) {

                //iterate over the nodes in the next layer
                for (int outputNodeNumber = 0; outputNodeNumber < layers[layer + 1].length; outputNodeNumber++) {
                    Node inputNode = layers[layer][inputNodeNumber];
                    Node outputNode = layers[layer + 1][outputNodeNumber];
                    new Edge(inputNode, outputNode);

                    //as we added an edge, the number of weights should increase by 1
                    numberWeights++;
                    Log.trace("numberWeights now: " + numberWeights);
                }
            }
        }
    }

    /**
     * This will create an Edge between the node with number inputNumber on the inputLayer to the
     * node with the outputNumber on the outputLayer.
     *
     * @param inputLayer the layer of the input node
     * @param inputNumber the number of the input node on layer inputLayer
     * @param outputLayer the layer of the output node
     * @param outputNumber the number of the output node on layer outputLayer
     */
    public void connectNodes(int inputLayer, int inputNumber, int outputLayer, int outputNumber) throws NeuralNetworkException {
        if (inputLayer >= outputLayer) {
            throw new NeuralNetworkException("Cannot create an Edge between input layer " + inputLayer + " and output layer " + outputLayer + " because the layer of the input node must be less than the layer of the output node.");
        }//  else if (outputLayer != inputLayer + 1) {
//            throw new NeuralNetworkException("Cannot create an Edge between input layer " + inputLayer + " and output layer " + outputLayer + " because the layer of the output node must be the next layer in the network.");
//        }
        new Edge(layers[inputLayer][inputNumber],layers[outputLayer][outputNumber]);
        numberWeights++;
        //Complete this function for Programming Assignment 1 - Part 1. BONUS: allow it to it create edges that can skip layers
    }

    /**
     * This initializes the weights properly by setting the incoming
     * weights for each edge using a random normal distribution (i.e.,
     * a gaussian distribution) and dividing the randomly generated
     * weight by sqrt(n) where n is the fan-in of the node. It also
     * sets the bias for each node to the given parameter.
     *
     * For example, if we have a node N which has 5 input edges,
     * the weights of each of those edges will be generated by
     * Random.nextGaussian()/sqrt(5). The best way to do this is
     * to iterate over each node and have it use the 
     * Node.initializeWeightsAndBias(double bias) method.
     *
     * @param bias is the value to set the bias of each node to.
     */
    public void initializeRandomly(double bias) {
        //  You need to implement this for PA1-3
        for (int i = 0; i < layers.length; i++) {
            for (int j = 0; j < layers[i].length; j++) {
                layers[i][j].initializeWeightsAndBias(bias);
            }
        }
    }



    /**
     * This performs a forward pass through the neural network given
     * inputs from the input instance.
     *
     * @param instance is the data set instance to pass through the network
     *
     * @return the sum of the output of all output nodes
     */
    public double forwardPass(Instance instance) throws NeuralNetworkException {
        //be sure to reset before doing a forward pass
        reset();
        for (int i = 0; i < layers.length; i++) {
            for (int j = 0; j < layers[i].length; j++) {
                if(i==0){
                    layers[i][j].postActivationValue = instance.inputs[j];
                }
                layers[i][j].propagateForward();
            }
        }


        //You need to implement this for Programming Assignment 1 - Part 1

        //The following is needed for PA1-3 and PA1-4
        int outputLayer = layers.length - 1;
        int nOutputs = layers[outputLayer].length;

        double outputSum = 0;
        if (lossFunction == LossFunction.NONE) {
            //just sum up the outputs
            for (int number = 0; number < nOutputs; number++) {
                Node outputNode = layers[outputLayer][number];
                outputSum += outputNode.postActivationValue;

                outputNode.delta = 1;
            }

        } else if (lossFunction == LossFunction.L1_NORM) {
            // Implement this for PA1-3
            for (int number = 0; number < nOutputs; number++) {
                Node outputNode = layers[outputLayer][number];
                outputSum += Math.abs(outputNode.postActivationValue - instance.expectedOutputs[number]);
                //outputNode.delta = Math.abs(outputNode.postActivationValue - instance.expectedOutputs[number])/(outputNode.postActivationValue - instance.expectedOutputs[number]);
                outputNode.delta = (outputNode.postActivationValue - instance.expectedOutputs[number] >= 0) ? 1 : -1;
            }

        } else if (lossFunction == LossFunction.L2_NORM) {
            // Implement this for PA1-3
            for (int number = 0; number < nOutputs; number++) {
                Node outputNode = layers[outputLayer][number];
                outputSum += Math.pow(outputNode.postActivationValue - instance.expectedOutputs[number], 2);
            }
            outputSum = sqrt(outputSum);

            for (int number = 0; number < nOutputs; number++) {
                Node outputNode = layers[outputLayer][number];
                outputNode.delta = (outputNode.postActivationValue - instance.expectedOutputs[number])/outputSum;
            }


        } else if (lossFunction == LossFunction.SVM) {
            double loss;
            double expectedOutputPostActivationValue;
            double zOutput ;
            int expectedOutput = (int) instance.expectedOutputs[0];
            expectedOutputPostActivationValue = layers[outputLayer][expectedOutput].postActivationValue;
            for(int j = 0; j < nOutputs; j++){
                Node outputNode = layers[outputLayer][j];
                zOutput  = outputNode.postActivationValue;
                if (j!= expectedOutput){
                    loss = Math.max(0,zOutput  - expectedOutputPostActivationValue +1);
                    outputSum += loss;
                    if(loss > 0){
                        outputNode.delta = 1;
                    } else {
                        outputNode.delta = 0;
                    }
                    layers[outputLayer][expectedOutput].delta -= outputNode.delta;
                }
            }
        }

        else if (lossFunction == LossFunction.SOFTMAX) {
            int solutionIndex = (int) instance.expectedOutputs[0];
            double expSum = 0;

            // Calculate the sum of the exponential values of the output nodes
            for (int i = 0; i < nOutputs; i++) {
                Node outputNode = layers[outputLayer][i];
                expSum += Math.exp(outputNode.postActivationValue);
            }

            // Calculate delta for each output node
            for (int i = 0; i < nOutputs; i++) {
                Node outputNode = layers[outputLayer][i];
                outputNode.delta = Math.exp(outputNode.postActivationValue) / expSum;

                if (i == solutionIndex) {
                    outputNode.delta -= 1;
                }
            }
            // Calculate the total loss
            outputSum = -1 * Math.log(Math.exp(layers[outputLayer][solutionIndex].postActivationValue) / expSum);
        }
        else {
            throw new NeuralNetworkException("Could not do forward pass on NeuralNetwork because lossFunction was unknown: " + lossFunction);
        }

        return outputSum;
    }

    /**
     * This performs multiple forward passes through the neural network
     * by multiple instances are returns the output sum.
     *
     * @param instances is the set of instances to pass through the network
     *
     * @return the sum of their outputs
     */
    public double forwardPass(List<Instance> instances) throws NeuralNetworkException {
        double sum = 0.0;

        for (Instance instance : instances) {
            sum += forwardPass(instance);
        }

        return sum;
    }

    /**
     * This performs multiple forward passes through the neural network
     * and calculates how many of the instances were classified correctly.
     *
     * @param instances is the set of instances to pass through the network
     *
     * @return a percentage (between 0 and 1) of how many instances were
     * correctly classified
     */
//    public double calculateAccuracy(List<Instance> instances) throws NeuralNetworkException {
//        // TODO: need to implement this for PA1-4
//        //the output node with the maximum value is the predicted class
//        //you need to sum up how many of these match the actual class
//        //from the instance, and then calculate: num correct / total
//        //to get a percentage accuracy
//
//        throw new NeuralNetworkException("calculateAccuracy(List<Instance> instances) was not yet implemented!");
//    }
    public double calculateAccuracy(List<Instance> instances) throws NeuralNetworkException {
        int numCorrectPredictions = 0;
        for (Instance instance : instances) {
            forwardPass(instance);
            double[] predictedOutputs = getOutputValues();
            int maxIndex = 0;
            for(int i =0;i<predictedOutputs.length;i++){
                if(predictedOutputs[i]>predictedOutputs[maxIndex]){
                    maxIndex = i;
                }
            }
            if(maxIndex == instance.expectedOutputs[0]){
                numCorrectPredictions++;

            }
        }
        //System.out.println(numCorrectPredictions);
        //System.out.println(instances.size());
        return (double) numCorrectPredictions /instances.size();
    }


    /**
     * This gets the output values of the neural network 
     * after a forward pass.
     *
     * @return an array of the output values from this neural network
     */
    public double[] getOutputValues() {
        //the number of output values is the number of output nodes
        int outputLayer = layers.length - 1;
        int nOutputs = layers[outputLayer].length;

        double[] outputValues = new double[nOutputs];

        for (int number = 0; number < nOutputs; number++) {
            outputValues[number] = layers[outputLayer][number].postActivationValue;
        }

        return outputValues;
    }

    /**
     * The step size used to calculate the gradient numerically using the finite
     * difference method.
     */
    private static final double H = 0.0000001;

    /**
     * This calculates the gradient of the neural network with it's current
     * weights for a given DataSet Instance using the finite difference method:
     * gradient[i] = (f(x where x[i] = x[i] + H) - f(x where x[i] = x[i] - H)) / 2h
     */
    public double[] getNumericGradient(Instance instance) throws NeuralNetworkException {
        //You need to implement this for Programming Assignment 1 - Part 2

        double[] w = getWeights();
        double[] gradient = new double[w.length];
        double[] w_test = w.clone();
        for (int i = 0; i < w.length; i++) {
            w_test[i] = w[i] + H;
            setWeights(w_test);
            double output1 = forwardPass(instance);
            w_test[i] = w[i] - H;
            setWeights(w_test);
            double output2 = forwardPass(instance);
            gradient[i] = (output1 - output2) / (2.0 * H);
            w_test[i] = w[i];
        }
        return gradient;
        //throw new NeuralNetworkException("getNumericGradient(Instance instance) was not yet implemented!");

    }

    /**
     * This calculates the gradient of the neural network with it's current
     * weights for a given DataSet Instance using the finite difference method:
     * gradient[i] = (f(x where x[i] = x[i] + H) - f(x where x[i] = x[i] - H)) / 2h
     */
    public double[] getNumericGradient(List<Instance> instances) throws NeuralNetworkException {
        // You need to implement this for Programming Assignment 1 - Part 2

        double[] w = getWeights();
        double[] gradient = new double[w.length];
        double[] w_test = w.clone();

        for (int i = 0; i < w.length; i++) {
            w_test[i] = w[i] + H;
            setWeights(w_test);
            double output1 = 0;
            for (Instance instance: instances)
            {
                output1 += forwardPass(instance);
            }

            w_test[i] = w[i] - H;
            setWeights(w_test);
            double output2 = 0;
            for (Instance instance: instances)
            {
                output2 += forwardPass(instance);
            }
            gradient[i] = (output1 - output2) / (2.0 * H);
            w_test[i] = w[i];
        }
        return gradient;

        //throw new NeuralNetworkException("getNumericGradient(List<Instance>instances) was not yet implemented!");
    }


    /**
     * This performs a backward pass through the neural network given 
     * outputs from the given instance. This will set the deltas in
     * all the edges and nodes which will be used to calculate the 
     * gradient and perform backpropagation.
     *
     */
    public void backwardPass(Instance instance) throws NeuralNetworkException {
        // You need to implement this for Programming Assignment 1 - Part 2
        for(int layer = layers.length-1; layer>=0; layer--){ // traverse layers
            for(int neuron = 0; neuron < layers[layer].length ;neuron++){ // traverse nodes in individual
                layers[layer][neuron].propagateBackward();
            }
        }

        //throw new NeuralNetworkException("backwardPass(Instance instance) was not yet implemented!");
    }

    /**
     * This gets the gradient of the neural network at its current
     * weights and the given instance using backpropagation (e.g., 
     * the NeuralNetwork.backwardPass(Instance))* Method.
     *
     * Helpful tip: use getDeltas after doing the propagateBackwards through
     * the networks to get the gradients/deltas in the same order as the
     * weights (which will be the same order as they're calculated for
     * the numeric gradient).
     *
     * @param instance is the training instance/sample for the forward and 
     * backward pass.
     */
    public double[] getGradient(Instance instance) throws NeuralNetworkException {
        forwardPass(instance);
        backwardPass(instance);

        return getDeltas();
    }

    /**
     * This gets the gradient of the neural network at its current
     * weights and the given instance using backpropagation (e.g.,
     * the NeuralNetwork.backwardPass(Instance))* Method.
     *
     * Helpful tip: use getDeltas after doing the propagateBackwards through
     * the networks to get the gradients/deltas in the same order as the
     * weights (which will be the same order as they're calculated for
     * the numeric gradient). The resulting gradient should be the sum of
     * each delta for each instance.
     *
     * @param instances are the training instances/samples for the forward and
     * backward passes.
     */
    public double[] getGradient(List<Instance> instances) throws NeuralNetworkException {
        // You need to implement this for Programming Assignment 1 - Part 2

        List<double[]> individualArraysList = new ArrayList<>();

        for (Instance instance : instances) {
            double[] individualArray = getGradient(instance);
            individualArraysList.add(individualArray);
        }

        int numArrays = individualArraysList.size();
        int arrayLength = individualArraysList.get(0).length;
        double[] resultArray = new double[arrayLength];

        for (int i = 0; i < arrayLength; i++) {
            double sum = 0;
            for (int j = 0; j < numArrays; j++) {
                sum += individualArraysList.get(j)[i];
            }
            resultArray[i] = sum;
        }

        return resultArray;

        //throw new NeuralNetworkException("backwardPass(List<Instance> instances) was not yet implemented!");
    }
}

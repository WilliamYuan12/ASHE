package ashe;

import java.util.Stack;
import java.util.Vector;

import agent.ActionBase;
import agent.AllIn;
import agent.Fold;
import agent.Params;
import agent.Raise;

public class WinRateEstimator_LSTM extends EstimatorBase {

	WinRateEstimator_LSTM(Ashe ashe) {
		super(ashe);
		lstm = new LSTMLayer(inputDim, cellCnt);
		fNet = new FFNetwork(cellCnt, hiddenNodeCnt, outputDim);
	}

	WinRateEstimator_LSTM(Ashe ashe, double[] genome) throws Exception {
		super(ashe);
		lstm = new LSTMLayer(inputDim, cellCnt,
				Util.head(genome, LSTMLayer.getGenomeLength(inputDim, cellCnt)));
		fNet = new FFNetwork(cellCnt, hiddenNodeCnt, outputDim,
				Util.tail(genome, FFNetwork.getGenomeLength(cellCnt, hiddenNodeCnt,  outputDim)));
	}

	double[] getGenome() {
		return Util.concat(lstm.getGenome(), fNet.getGenome());
	}

	static int getGenomeLength() {
		return LSTMLayer.getGenomeLength(inputDim, cellCnt)
				+ FFNetwork.getGenomeLength(cellCnt, hiddenNodeCnt, outputDim);
	}

	double estimate(Intel intel, double handStrength, ActionBase action) throws Exception {
		if (action instanceof Fold)
			return 0.0;
		Stack<NodeBase> nodeStk = new Stack<NodeBase>();
		Vector<NodeBase> trace = new Vector<NodeBase>();
		trace.addAll(intel.record);
		NodeBase node = intel.next(action);
		if (node != null)
			trace.add(node);
		else 
			trace.add(intel.current);
		double adjustment = 0;
		lstm.reset();
		double[] features = null;
		for (int i = 0; i < trace.size(); i++) {
			for (node = trace.get(i); node != null; node = node.parent)
				nodeStk.push(node);		
			for (; !nodeStk.isEmpty();) {
				node = nodeStk.pop();
				features = lstm.activate(getLSTMInput(node));
			}
		}
		adjustment = (1 + fNet.activate(features)[0]) / 2;
		return Math.pow(adjustHandStrength(handStrength, action), 1 / adjustment);
	}

	private double[] getLSTMInput(NodeBase node) {
		double[] input = new double[inputDim];
		input[0] = 1.0 * node.stats.showdown / node.stats.frequency;
		input[1] = 1.0 * node.stats.myFold / node.stats.frequency;
		input[2] = node.stats.oSDH_M;
		input[3] = node.stats.showdown < 2 ? 0
				: Math.sqrt(node.stats.oSDH_SoS / (node.stats.showdown - 1)
						- node.stats.showdown * Math.pow(input[2], 2) / (node.stats.showdown - 1)) / 0.5;
		input[4] = Util.tanh(0.1 * node.stats.frequency);
		input[5] = Util.tanh(0.2 * node.stats.showdown);
		for (int i = 0; i < input.length; i++)
			input[i] = 2 * input[i] - 1;
		return input;
	}

	private double adjustHandStrength(double handStrength, ActionBase action) {
		if (action instanceof Raise) {
			Raise raise = (Raise)action;
			double potOdds = 1.0 * (raise.raiserTotalBet - raise.opponentTotalBet)
					/ 2 / raise.raiserTotalBet;
			if (handStrength < Math.sqrt(potOdds))
				return 0;
			handStrength = 1.0 - (1.0 - handStrength) / (1.0 - potOdds);
		}
		if (action instanceof AllIn) {
			AllIn allIn = (AllIn)action;
			double potOdds = 0;
			if (allIn.opponentTotalBet < Params.stackSize) 		
				potOdds = 1.0 * (Params.stackSize - allIn.opponentTotalBet) / Params.stackSize / 2;
			else 
				potOdds = 1.0 * (Params.stackSize - ashe.myTotalBet()) / Params.stackSize / 2;
			if (handStrength < Math.sqrt(potOdds))
				return 0;
			handStrength = 1.0 - (1.0 - handStrength) / (1.0 - Math.sqrt(potOdds));
		}
		return handStrength;
	}

	static int inputDim = 6;
	static int cellCnt = 10;
	static int hiddenNodeCnt = 7;
	static int outputDim = 1;
	
	LSTMLayer lstm;
	FFNetwork fNet;
}

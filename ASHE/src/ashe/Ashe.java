package ashe;

import java.io.IOException;
import java.util.Random;
import java.util.Vector;

import agent.ActionBase;
import agent.AgentBase;
import agent.Call;
import agent.Check;
import agent.Result;
import agent.AllIn;
import agent.Fold;
import agent.Params;
import agent.Raise;

public class Ashe extends AgentBase {

	public Ashe(int id) throws Exception {
		super(id);
		forest = null;
		forestFile = null;
		rand = new Random();
		constructByGenome(new AsheGenome(AsheParams.GenomeFile));
	}

	public Ashe(int id, AsheGenome genome) throws Exception {
		super(id);
		forest = null;
		forestFile = null;
		rand = new Random();
		constructByGenome(genome);
	}

	public Ashe(int id, String genomeFile) throws Exception {
		super(id);
		forest = null;
		this.forestFile = null;
		rand = new Random();
		constructByGenome(new AsheGenome(genomeFile));
	}

	public Ashe(int id, String genomeFile, String forestFile) throws Exception {
		super(id);
		forest = null;
		this.forestFile = forestFile;
		rand = new Random();
		constructByGenome(new AsheGenome(genomeFile));
	}

	private void constructByGenome(AsheGenome genome) throws Exception {
		double[] genes = genome.getGenes();
		WRE = new WinRateEstimator_LSTM(this, Util.head(genes, WinRateEstimator_LSTM.getGenomeLength()));
		FRE = new FoldRateEstimator_LSTM(this, Util.tail(genes, FoldRateEstimator_LSTM.getGenomeLength()));
	}

	public NumericGenome getGenome() {
		double[] genome = null;
		genome = Util.concat(genome, ((WinRateEstimator_LSTM) WRE).getGenome());
		genome = Util.concat(genome, ((FoldRateEstimator_LSTM) FRE).getGenome());
		return new AsheGenome(genome);
	}

	public static int getGenomeLength() {
		int length = 0;
		length += WinRateEstimator_LSTM.getGenomeLength();
		length += FoldRateEstimator_LSTM.getGenomeLength();
		return length;
	}

	public String getName() {
		return "Ashe (ID = " + myID + ")";
	}

	public void newMatch() {
		super.init();
		try {
			forest = (forestFile == null ? new GameForest(myID) : new GameForest(myID, forestFile));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void saveForest(String path) throws IOException {
		forest.save(path);
		System.out.println("Game forest saved at " + path + " (" + forest.getTotalNodeCnt() + " nodes).");
	}

	protected void newGame() {
		forest.prepare(button() ? 0 : 1);
	}

	protected void observeAction(ActionBase action) {
		forest.updateAction(action, board());
	}

	protected void observeResult(Result result) {
		try {
			forest.updateResult(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected ActionBase getAction() throws Exception {
		Intel intel = forest.getIntel();
		Vector<ActionBase> actions = getAvailableActions(intel.getBetCnt());
		if (board().length() == 0)
			return preflop(actions, intel);
		return postflop(actions, intel);
	}

	int myTotalBet() {
		return myTotalBet;
	}

	int oppTotalBet() {
		return oppTotalBet;
	}

	String board() {
		return getBoard();
	}

	private ActionBase postflop(Vector<ActionBase> actions, Intel intel) throws Exception {
		double handStrength = GameForest.evaluator.getHandStength(peek(), board());
		int best = 0;
		double bestEquity = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < actions.size(); i++) {
			double equity = evaluate(handStrength, actions.get(i), intel);
			if (equity > bestEquity) {
				bestEquity = equity;
				best = i;
			}
		}
		return actions.get(best);
	}

	private double evaluate(double handStrength, ActionBase action, Intel intel) throws Exception {
		if (action instanceof Fold)
			return -myTotalBet;
		double winRate = WRE.estimate(intel, handStrength, action);
		if (action instanceof Check && !(intel.current instanceof Root))
			return (2 * winRate - 1) * potSize() / 2;
		if (action instanceof Check) {
			double expectedRaise = 0;
			double raiseProb = 0;
			NodeBase checkNode = intel.next(action);
			if (checkNode != null) {
				for (int i = 0; i < checkNode.children.size(); i++) {
					if (checkNode.children.get(i).conditionCode > 3) {
						raiseProb += checkNode.children.get(i).stats.frequency;
						expectedRaise += checkNode.children.get(i).stats.frequency
								* Tools.getRaiseAmtToPot(checkNode.children.get(i).conditionCode, potSize());
					}
				}
				raiseProb /= checkNode.stats.frequency;
				expectedRaise /= checkNode.stats.frequency;
			}
			return (1 - raiseProb) * (2 * winRate - 1) * potSize() / 2
					+ raiseProb * (winRate > expectedRaise / (2 * expectedRaise + 1.0)
							? (2 * winRate - 1) * potSize() * (0.5 + expectedRaise) : -potSize() / 2);
		}
		if (action instanceof Call || (action instanceof AllIn && oppTotalBet == Params.stackSize))
			return (2 * winRate - 1) * oppTotalBet;
		double fp = FRE.estimate(intel, handStrength, action);
		if (action instanceof Raise)
			return fp * ((Raise) action).opponentTotalBet
					+ (1 - fp) * (2 * winRate - 1) * ((Raise) action).raiserTotalBet;
		if (action instanceof AllIn)
			return fp * ((AllIn) action).opponentTotalBet + (1 - fp) * (2 * winRate - 1) * Params.stackSize;
		return 0;
	}

	private ActionBase preflop(Vector<ActionBase> actions, Intel intel) throws Exception {
		double handStrength = GameForest.evaluator.getHandStength(peek(), board());
		// ON BUTTION
		if (intel.button()) {
			if (intel.getBetCnt() == 1) {
				if (handStrength < 0.40) {
					if (getFoldEquity(handStrength, (Raise) actions.get(3), intel) > 0)
						return actions.get(3);
					return actions.get(0);
				}
				if (handStrength > 0.66)
					return actions.get(3);
				if (rand.nextDouble() < handStrength)
					return actions.get(3);
				return actions.get(2);
			}
			if (intel.getBetCnt() == 3) {
				if (handStrength < 0.40)
					return actions.get(0);
				if (handStrength > 0.80)
					return actions.size() > 2 ? actions.get(2) : actions.get(1);
				return shouldFold(handStrength, intel.getStateFreq(intel.current), getPotOdds()) ? actions.get(0)
						: actions.get(1);
			}
			return actions.get(1);
		}
		if (oppTotalBet == myTotalBet) {
			if (handStrength < 0.50) {
				if (getFoldEquity(handStrength, (Raise) actions.get(2), intel) > 0)
					return actions.get(2);
				return actions.get(0);
			}
			if (rand.nextDouble() < handStrength)
				return actions.get(2);
			return actions.get(1);
		}
		if (intel.getBetCnt() == 2) {
			if (handStrength < 0.40)
				return actions.get(0);
			if (handStrength > 0.66)
				return actions.get(1 + rand.nextInt(actions.size() - 1));
			return shouldFold(handStrength, intel.getStateFreq(intel.current), getPotOdds()) ? actions.get(0)
					: actions.get(1);
		}
		return shouldFold(handStrength, intel.getStateFreq(intel.current), getPotOdds()) ? actions.get(0)
				: actions.get(1);
	}

	private double getFoldEquity(double handStrength, Raise raise, Intel intel) throws Exception {
		double fp = ruleBasedEstimate(intel, raise);
		double winRate = Math.pow(handStrength, 1.0 + fp);
		return fp * raise.opponentTotalBet + (1 - fp) * (2 * winRate - 1) * raise.raiserTotalBet;
	}

	private double ruleBasedEstimate(Intel intel, ActionBase action) throws Exception {
		NodeBase raiseNode = intel.next(action);
		double potOdds = action instanceof Raise
				? 1.0 * (((Raise) action).raiserTotalBet - ((Raise) action).opponentTotalBet)
						/ 2 / ((Raise) action).raiserTotalBet
				: 1.0 * (Params.stackSize - ((AllIn)action).opponentTotalBet) / 2 / Params.stackSize;
		double smooth = potOdds * (1.0 + 0.7 * board().length() / 10.0);
		if (raiseNode == null)
			return smooth;
		double fr = 1.0 * raiseNode.stats.oppFold / raiseNode.stats.frequency;
		if (raiseNode.stats.frequency < 10)
			return smooth * (1.0 - raiseNode.stats.frequency / 10.0) + fr * raiseNode.stats.frequency / 10.0;
		return fr;
	}
	
	private boolean shouldFold(double handStrength, double frequency, double potOdds) {
		return Math.pow(handStrength, 2 - frequency) < potOdds;
	}

	private double getPotOdds() {
		return 1.0 * (oppTotalBet - myTotalBet) / 2 / oppTotalBet;
	}
	
	private Vector<ActionBase> getAvailableActions(int betCnt) {
		int potSizeBet = 2 * oppTotalBet;
		Vector<ActionBase> actions = new Vector<ActionBase>();
		if (oppTotalBet > myTotalBet)
			actions.add(new Fold(myID));
		if (oppTotalBet < Params.stackSize) {
			if (oppTotalBet == myTotalBet)
				actions.add(new Check(myID));
			else
				actions.add(new Call(myID, oppTotalBet));
		}
		if (betCnt < 4 || board().length() == 10) {
			if (oppTotalBet + potSizeBet / 2 < Params.stackSize)
				actions.add(new Raise(myID, oppTotalBet + potSizeBet / 2, oppTotalBet));
			if (oppTotalBet + potSizeBet < Params.stackSize)
				actions.add(new Raise(myID, oppTotalBet + potSizeBet, oppTotalBet));
		}
		if (board().length() == 10 || oppTotalBet + potSizeBet >= Params.stackSize)
			actions.add(new AllIn(myID, oppTotalBet));
		return actions;
	}

	GameForest forest;
	String forestFile;
	Random rand;
	EstimatorBase WRE;
	EstimatorBase FRE;
}

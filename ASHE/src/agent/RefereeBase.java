package agent;

import java.util.Collections;

abstract class RefereeBase {
	RefereeBase(Board board, HoleCards holeCards) throws Exception {
		sorted = new Hand();
		sorted.add(holeCards.getHighCard());
		sorted.add(holeCards.getKicker());
		sorted.addAll(board);
		Collections.sort(sorted);
		if (sorted.size() < 5 || sorted.size() > 7)
			throw new Exception("JudgeBase Constructor: Invalid sorted (size < 5).");
		hand = null;
	}
	
	abstract Hand getBestHand();

	protected Hand sorted;
	protected Hand hand;
}

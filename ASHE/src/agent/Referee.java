package agent;

public class Referee {
	
	public static Hand getBestHand(Board board, HoleCards holeCards) throws Exception {
		Hand hand = computeBestHand(board, holeCards);
		hand.setHoleCards(holeCards);
		return hand;
	}

	private static Hand computeBestHand(Board board, HoleCards holeCards) throws Exception {
		Hand hand = null;
		Hand temp = null;
		RefereeStraightFlushAndFlush SF_FLJudge = new RefereeStraightFlushAndFlush(board, holeCards);
		hand = SF_FLJudge.getBestHand();
		if (hand != null && hand.getRank() > 7) 
			return hand;
		RefereeFourOfAKind FK_Judge = new RefereeFourOfAKind(board, holeCards);
		temp = FK_Judge.getBestHand();
		if (temp != null)
			return temp;
		RefereeFullHouseAndThreeOfAKind FH_TKJudge = new RefereeFullHouseAndThreeOfAKind(board, holeCards);
		temp = FH_TKJudge.getBestHand();
		if (temp != null && temp.getRank() == 6)
			return temp;
		if (hand != null) 
			return hand;
		hand = temp;
		RefereeStraight STJudge = new RefereeStraight(board, holeCards);
		temp = STJudge.getBestHand();
		if (temp != null)
			return temp;
		if (hand != null)
			return hand;
		RefereeOneAndTwoPair OPJudge = new RefereeOneAndTwoPair(board, holeCards);
		hand = OPJudge.getBestHand();
		if (hand != null)
			return hand;
		RefereeHighCard HCJudge = new RefereeHighCard(board, holeCards);
		hand = HCJudge.getBestHand();
		return hand;
	}

}

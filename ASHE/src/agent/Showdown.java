package agent;

public class Showdown extends Result {

	Showdown(Board board, HoleCards hc1, HoleCards hc2, int p1Bet, int p2Bet) throws Exception {
		this.board = board.display();
		potSize = p1Bet + p2Bet;
		Hand h1 = Referee.getBestHand(board, hc1);
		Hand h2 = Referee.getBestHand(board, hc2);
		hands = new String[2];
		holeCards = new String[2];
		rankedID = new int[2];
		if (h1.compareTo(h2) <= 0) {
			rankedID[0] = hc1.ownerID;
			hands[0] = h1.toString();
			holeCards[0] = h1.getHoleCards().toString();
			rankedID[1] = hc2.ownerID;
			hands[1] = h2.toString();
			holeCards[1] = h2.getHoleCards().toString();
			winnerCnt = (h1.compareTo(h2) == 0) ? 2 : 1;
		} else {
			rankedID[0] = hc2.ownerID;
			hands[0] = h2.toString();
			holeCards[0] = h2.getHoleCards().toString();
			rankedID[1] = hc1.ownerID;
			hands[1] = h1.toString();
			holeCards[1] = h1.getHoleCards().toString();
			winnerCnt = 1;
		}
	}

	public String toString() {
		String report = "<BEGIN: SHOWDOWN>\nBoard: " + board + "\n";
		int prize = potSize / winnerCnt;
		report += "Winner ID = " + rankedID[0] + " (" + holeCards[0] + "): " + hands[0] + " ($"
				+ prize + ")\n";
		if (winnerCnt == 2) 
			report += "Winner ID = " + rankedID[1] + " (" + holeCards[1] + "): " + hands[1] + " ($"
					+ prize + ")\n";
		else 
			report += "Player ID = " + rankedID[1] + " (" + holeCards[1] + "): " + hands[1] + "\n";
		report += "<END: SHOWDOWN>\n";
		return report;
	}

	public final int potSize;
	public final String[] hands;
	public final String[] holeCards;
	public final int[] rankedID;
	public final int winnerCnt;
}

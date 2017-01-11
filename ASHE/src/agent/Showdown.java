package agent;

import java.util.Vector;

public class Showdown extends Result {

	Showdown(Board board, HoleCards hc1, HoleCards hc2, int p1Bet, int p2Bet) throws Exception {
		this.board = board.display();
		potSize = p1Bet + p2Bet;
		Hand h1 = Referee.getBestHand(board, hc1);
		Hand h2 = Referee.getBestHand(board, hc2);
		hands = new Hand[2];
		rankedID = new Vector<Integer>();
		if (h1.compareTo(h2) <= 0) {
			rankedID.add(hc1.ownerID);
			rankedID.add(hc2.ownerID);
			hands[0] = h1;
			hands[1] = h2;
			winnerCnt = (h1.compareTo(h2) == 0) ? 2 : 1;
		} else {
			rankedID.add(hc2.ownerID);
			rankedID.add(hc1.ownerID);
			hands[0] = h2;
			hands[1] = h1;
			winnerCnt = 1;
		}
	}

	public String toString() {
		String report = "<BEGIN: SHOWDOWN>\nBoard: " + board + "\n";
		int prize = potSize / winnerCnt;
		report += "Winner ID = " + rankedID.get(0) + " (" + hands[0].getHoleCards() + "): " + hands[0] + " ($"
				+ prize + ")\n";
		if (winnerCnt == 2) 
			report += "Winner ID = " + rankedID.get(1) + " (" + hands[1].getHoleCards() + "): " + hands[1] + " ($"
					+ prize + ")\n";
		else 
			report += "Player ID = " + rankedID.get(1) + " (" + hands[1].getHoleCards() + "): " + hands[1] + "\n";
		report += "<END: SHOWDOWN>\n";
		return report;
	}

	public final int potSize;
	public final Hand[] hands;
	public final Vector<Integer> rankedID;
	public final int winnerCnt;
}

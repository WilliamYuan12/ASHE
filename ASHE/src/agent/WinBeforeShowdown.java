package agent;

public class WinBeforeShowdown extends Result {

	WinBeforeShowdown(int winnerID, int potSize, int loserTotal, Board board) {
		this.winnerID = winnerID;
		this.potSize = potSize;
		this.loserTotal = loserTotal;
		this.board = board.display();
	}

	public String toString() {
		String report = "<BEGIN: WIN BEFORE SHOWDOWN>\nBoard: " + board + "\n";
		report += "Winner ID = " + winnerID + ", Pot Size = " + potSize + ", Net Gain = " + loserTotal + "\n";
		report += "<END: WIN BEFORE SHOWDOWN>\n";
		return report;
	}

	public final int winnerID;
	public final int potSize;
	public final int loserTotal;
}

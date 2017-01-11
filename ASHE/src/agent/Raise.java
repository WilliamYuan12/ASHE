package agent;

public class Raise extends ActionBase {

	public Raise(int playerID, int raiseToAmt, int opponentTotalBet) {
		super(playerID);
		this.raiserTotalBet = raiseToAmt;
		this.opponentTotalBet = opponentTotalBet;
	}

	public String toString() {
		return "Player[" + playerID + "]: Raise($" + raiserTotalBet + ")";
	}
	
	String compress() {
		return "r" + raiserTotalBet;
	}
	
	public final int raiserTotalBet;
	public final int opponentTotalBet;
}

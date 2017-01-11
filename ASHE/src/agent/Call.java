package agent;

public class Call extends ActionBase {

	public Call(int playerID, int callerTotalBet) {
		super(playerID);
		this.callerTotalBet = callerTotalBet;
	}

	public String toString() {
		return "Player[" + playerID + "]: Call($" + callerTotalBet + ")";
	}

	String compress() {
		return "c";
	}
	
	public final int callerTotalBet;
}

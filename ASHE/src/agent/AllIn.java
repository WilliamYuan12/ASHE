package agent;

public class AllIn extends ActionBase {

	public AllIn(int playerID, int opponentTotalBet) {
		super(playerID);
		this.opponentTotalBet = opponentTotalBet;
	}

	public String toString() {
		return "Player[" + playerID + "]: AllIn($" + Params.stackSize + ")";
	}
	
	String compress() {
		return opponentTotalBet == Params.stackSize ? "c" : "r" + Params.stackSize;
	}

	public int opponentTotalBet;
}

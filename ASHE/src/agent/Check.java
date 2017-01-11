package agent;

public class Check extends ActionBase {

	public Check(int playerID) {
		super(playerID);
	}

	public String toString() {
		return "Player[" + playerID + "]: Check";
	}

	String compress() {
		return "c";
	}

	
}

package agent;

public abstract class ActionBase {
	
	public ActionBase(int playerID) {
		this.playerID = playerID;
	}
	
	abstract public String toString();
	
	abstract String compress();
	
	int playerID;
}

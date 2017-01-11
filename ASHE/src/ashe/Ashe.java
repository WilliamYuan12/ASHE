package ashe;

import agent.ActionBase;
import agent.AgentBase;
import agent.Call;
import agent.Check;
import agent.Result;

public class Ashe extends AgentBase {

	public Ashe(int myID) {
		super(myID);
		
	}

	public String getName() {
		return "Ashe (ID = " + myID + ")";
	}
	
	public void newMatch() {
		super.init();
		
	}
	
	protected void newGame() {
		
	}
	
	protected void observeAction(ActionBase action) {
		
	}
	
	protected void observeResult(Result result) {
		System.out.print(result);
	}
	
	protected ActionBase getAction() {
		if (myTotalBet == oppTotalBet)
			return new Check(myID);
		return new Call(myID, oppTotalBet);
	}

}

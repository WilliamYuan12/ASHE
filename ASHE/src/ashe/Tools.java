package ashe;

import agent.ActionBase;
import agent.AllIn;
import agent.Call;
import agent.Check;
import agent.Fold;
import agent.Params;
import agent.Raise;

class Tools {
	
	static String getStageName(int stage) {
		if (stage == 0) return "PREFLOP";
		if (stage == 1) return "FLOP";
		if (stage == 2) return "TURN";
		if (stage == 3) return "RIVER";
		return "ERROR";
	}

	static String describeActionCode(int actionCode) {
		if (actionCode == 1)
			return "FD";
		if (actionCode == 2)
			return "CK";
		if (actionCode == 3)
			return "CL";
		if (actionCode == 4)
			return "R.25";
		if (actionCode == 5)
			return "R.5";
		if (actionCode == 6)
			return "R1.0";
		if (actionCode == 7)
			return "R1.5";
		if (actionCode == 8)
			return "R2.5";
		if (actionCode == 9)
			return "R4.0";
		if (actionCode == 10)
			return "AI";
		return "ERROR";
	}

	static String describeRootCode(int rootCode) {
		if (rootCode == 0)
			return "TN-PRE";
		if (rootCode == 1)
			return "BB-PRE";
		if (rootCode == 2)
			return "TN-FLP";
		if (rootCode == 3)
			return "BB-FLP";
		if (rootCode == 4)
			return "TN-TRN";
		if (rootCode == 5)
			return "BB-TRN";
		if (rootCode == 6)
			return "TN-RVR";
		if (rootCode == 7)
			return "BB-RVR";
		return "R-ERROR";
	}

	static int getActionCode(ActionBase action) {
		if (action.getClass() == Fold.class)
			return 1;
		if (action.getClass() == Check.class)
			return 2;
		if (action.getClass() == Call.class)
			return 3;
		if (action.getClass() == Raise.class) {
			Raise raise = (Raise)action;
			double aggression = 1.0 * (raise.raiserTotalBet - raise.opponentTotalBet)
					/ (raise.raiserTotalBet + raise.opponentTotalBet);
			return getRaiseCode(aggression);
		}
		if (action.getClass() == AllIn.class)
			return 10;
		return 0;
	}
	
	static double getRaiseAmtToPot(int code, int potSize) {
		double maxRaise = (Params.stackSize - potSize / 2) / potSize;
		if (code == 4)
			return Math.max(maxRaise, 0.125);
		if (code == 5)
			return Math.max(maxRaise, 0.5);
		if (code == 6)
			return Math.max(maxRaise, 1.0);
		if (code == 7)
			return Math.max(maxRaise, 1.5);
		if (code == 8)
			return Math.max(maxRaise, 2.5);
		if (code == 9)
			return Math.max(maxRaise, 4.0);
		if (code == 10)
			return maxRaise;
		return 0;
	}

	private static int getRaiseCode(double aggression) {
		if (aggression <= 0.25)
			return 4;
		if (aggression <= 0.40)
			return 5;
		if (aggression <= 0.56)
			return 6;
		if (aggression <= 0.66)
			return 7;
		if (aggression <= 0.75)
			return 8;
		return 9;
	}
}

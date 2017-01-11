package ashe;

import agent.ActionBase;

class Root extends NodeBase {

	Root(int conditionCode) {
		super(conditionCode, null);
	}
	
	Root(int conditionCode, String stats) {
		super(conditionCode, null, stats);
	}
	
	String describeNode() {
		return Tools.describeRootCode(conditionCode);
	}

	NodeBase getNewChild(ActionBase action) {
		if ((conditionCode == 0) || (conditionCode % 2 == 1 && conditionCode > 1))
			return new MyAction(-Tools.getActionCode(action), this);
		return new OpponentAction(Tools.getActionCode(action), this);
	}

	boolean match(ActionBase action) {
		// SHOULD "NEVER" BE CALLED!!
		return false;
	}
}

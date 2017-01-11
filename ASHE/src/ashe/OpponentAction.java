package ashe;

import agent.ActionBase;

class OpponentAction extends NodeBase {

	OpponentAction(int conditionCode, NodeBase parent) {
		super(conditionCode, parent);
	}
	
	OpponentAction(int conditionCode, NodeBase parent, String statsStr) {
		super(conditionCode, parent, statsStr);
	}
		
	String describeNode() {
		return "O-" + Tools.describeActionCode(conditionCode);
	}
	
	NodeBase getNewChild(ActionBase action) {
		return new MyAction(-Tools.getActionCode(action), this);
	}
	
	boolean match(ActionBase action) {
		return conditionCode == Tools.getActionCode(action);
	}
		
}

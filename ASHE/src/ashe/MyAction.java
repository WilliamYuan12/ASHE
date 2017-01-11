package ashe;

import agent.ActionBase;

class MyAction extends NodeBase {
	
	MyAction(int conditionCode, NodeBase parent) {
		super(conditionCode, parent);
	}
	
	MyAction(int conditionCode, NodeBase parent, String statsStr) {
		super(conditionCode, parent, statsStr);
	}
		
	String describeNode() {
		return "A-" + Tools.describeActionCode(-conditionCode);
	}

	NodeBase getNewChild(ActionBase Action) {
		return new OpponentAction(Tools.getActionCode(Action), this);
	}

	boolean match(ActionBase Action) {
		return conditionCode == -Tools.getActionCode(Action);
	}

}

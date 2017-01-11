package ashe;

import java.util.Vector;

import agent.ActionBase;

abstract class NodeBase implements Comparable<NodeBase> {
	
	NodeBase(int conditionCode, NodeBase parent) {
		this.conditionCode = conditionCode;
		stats = new NodeStats();
		this.parent = parent;
		children = new Vector<NodeBase>();
	}	
	
	NodeBase(int conditionCode, NodeBase parent, String statsStr) {
		this.conditionCode = conditionCode;
		stats = new NodeStats(statsStr);
		this.parent = parent;
		children = new Vector<NodeBase>();
	}
	
	public int compareTo(NodeBase other) {
		if (this.stats.frequency == other.stats.frequency) return 0;
		return this.stats.frequency > other.stats.frequency ? -1 : 1;
	}
	
	public String toString() {
		return conditionCode + "(" + stats + ")";
	}
	
	String display() {
		String res = describeNode();
		res += "(" + stats + ")";
		return res;
	}
	
	abstract String describeNode();
	
	NodeBase next(ActionBase action) {
		for (int i = 0; i < children.size(); i++)
			if (children.get(i).match(action))
				return children.get(i);
		NodeBase nextNode = getNewChild(action);
		children.add(nextNode);
		return nextNode;
	}
			
	abstract NodeBase getNewChild(ActionBase action);
	abstract boolean match(ActionBase action);
	
	final int conditionCode;
	NodeStats stats;
	NodeBase parent;
	Vector<NodeBase> children;
}

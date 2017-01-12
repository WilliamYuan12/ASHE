package ashe;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import agent.ActionBase;
import agent.Result;
import agent.Showdown;
import agent.WinBeforeShowdown;

class GameForest implements Statistician {

	GameForest(int myID) {
		trees = new GameTree[gameTreeCnt];
		intel = new Intel();
		this.myID = myID;
		boards = new String[boardCnt];
		reset();
	}

	GameForest(int myID, String file) throws IOException {
		trees = new GameTree[gameTreeCnt];
		intel = new Intel();
		this.myID = myID;
		boards = new String[boardCnt];
		BufferedReader reader = new BufferedReader(new FileReader(file));
		for (int i = 0; i < trees.length; i++)
			trees[i] = new GameTree(reader.readLine());
		reader.close();
		prepare(-1);
	}
	
	public String toString() {
		String res = "";
		for (int i = 0; i < trees.length; i++)
			res += trees[i] + "\n";
		return res;
	}

	public void save(String path) throws IOException {
		PrintWriter writer = new PrintWriter(path);
		for (int i = 0; i < trees.length; i++)
			writer.println(trees[i]);
		writer.close();
	}
	
	public int getTotalNodeCnt() {
		int cnt = 0;
		for (int i = 0; i < trees.length; i++)
			 cnt += trees[i].nodeCnt();
		return cnt;
	}

	void reset() {
		for (int i = 0; i < trees.length; i++)
			trees[i] = new GameTree(i);
		prepare(-1);
	}

	void prepare(int position) {
		this.position = position;
		stage = 0;
		position = -1;
		intel.reset();
		boards[0] = "";
		for (int i = 1; i < boards.length; i++)
			boards[i] = "UNKNOWN";
	}

	Intel getIntel() {
		NodeBase current = trees[index()].getCurrent();
		if (current instanceof Root)
			current.stats.frequency++;
		intel.updateCurrent(current);
		return intel;
	}

	void updateAction(ActionBase actionInfo, String board) {
		if (actionInfo.playerID != myID && trees[index()].getCurrent() instanceof Root)
			trees[index()].getCurrent().stats.frequency++;
		if (boards[stage].equals("UNKNOWN"))
			boards[stage] = board;
		if (trees[index()].updateAction(actionInfo) && stage < 3) {
			intel.updateRecord(trees[index()].getCurrent());
			stage++;
		}
	}

	void updateResult(Result result) throws Exception {
		for (; stage >= 0; stage--) {
			if (result instanceof WinBeforeShowdown)
				trees[index()].backtrackWBS(((WinBeforeShowdown) result).winnerID == myID);
			else if (result instanceof Showdown) {
				trees[index()].backtrackSD(
						evaluator.getHandStength(getOpponentHoleCards((Showdown) result), boards[stage]));
			}
		}
		for (int i = 0; i < 4; i++) {
			trees[position + 2 * i].refresh();
		}
	}

	String display() {
		String res = "";
		for (int i = 0; i < trees.length; i++)
			res += trees[i].display() + "\n";
		return res;
	}

	private String getOpponentHoleCards(Showdown result) {
		String[] holeCards = result.holeCards;
		int[] ids = result.rankedID;
		for (int i = 0; i < ids.length; i++) 
			if (ids[i] != myID)
				return holeCards[i];
		return null;
	}

	private int index() {
		return position + 2 * stage;
	}

	private GameTree[] trees;
	private Intel intel;
	private int myID;
	private int stage;
	private int position; 
	private String[] boards;
	
	private static final int gameTreeCnt = 8;
	private static final int boardCnt = 4;
}

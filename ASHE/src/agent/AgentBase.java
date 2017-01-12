package agent;

public abstract class AgentBase {

	public AgentBase(int myID) {
		this.myID = myID;
		init();
	}

	public String stateReport() {
		String report = "<BEGIN: STATE REPORT>\n";
		report += "GID = " + gameID + ", pos = " + position + ", me: " + myHoleCards + ", opp: " + oppHoleCards + "\n";
		report += "stg = " + stage + ", board: " + board + "\n";
		report += "last move: " + lastMove + "\n";
		report += "pot = " + potSize() + "\n";
		report += "<END: STATE REPORT>" + "\n";
		return report;
	}
	
	public void ACPCMatch(String[] args) throws Exception {
		if (args.length != 2)
			throw new Exception("Invalid args (args.length != 2).");
		System.out.println("Dealer IP: " + args[0] + "\nPort: " + args[1]);
		NetComm comm = new NetComm(args);
		if (comm.connected())
			System.out.println("Connection established.");
		else 
			throw new Exception("Failed to connect to dealer server.");
		String msg = null;
		newMatch();
		for (msg = comm.receive(); msg != null; msg = comm.receive()) {
			String response = parse(msg);
			if (response != null)
				comm.send(response);
		}
		System.out.println("Match completed!");
		comm.close();
	}

	public abstract String getName();

	public abstract void newMatch();

	protected abstract void newGame();

	protected abstract void observeAction(ActionBase action);

	protected abstract void observeResult(Result result);

	protected abstract ActionBase getAction() throws Exception;

	protected void init() {
		gameID = -1;
		position = -1;
		stage = -1;
		myTotalBet = 0;
		oppTotalBet = 0;
		noMoreAction = false;
		prevMoves = null;
		lastMove = null;
		board = null;
		myHoleCards = null;
		oppHoleCards = null;
	}

	protected int potSize() {
		return myTotalBet + oppTotalBet;
	}
	
	protected String getBoard() {
		return board.toString();
	}
	
	protected String peek() {
		return myHoleCards.toString();
	}
	
	protected boolean button() {
		return position == 1;
	}

	String parse(String msg) throws Exception {
		if (msg == null || !msg.startsWith("MATCHSTATE"))
			return null;
		String response = null;
		String[] sessions = msg.split(":");
		int gameID = Integer.parseInt(sessions[2]);
		if (gameID != this.gameID) {
			this.gameID = gameID;
			position = Integer.parseInt(sessions[1]);
			stage = 0;
			myTotalBet = position == 0 ? Params.BB : Params.SB;
			oppTotalBet = position == 0 ? Params.SB : Params.BB;
			noMoreAction = false;
			prevMoves = null;
			lastMove = null;
			int barIdx = sessions[4].indexOf('|');
			myHoleCards = new HoleCards(
					position == 0 ? sessions[4].substring(0, barIdx) : sessions[4].substring(barIdx + 1), myID);
			oppHoleCards = null;
			newGame();
		}
		Result result = null;
		if (!noMoreAction) {
			parseMoves(sessions[3]);
			result = processLastMove();
		}
		parseCards(sessions[4]);
		if (oppHoleCards != null && board.size() == 5) {
			result = new Showdown(board, myHoleCards, oppHoleCards, myTotalBet, oppTotalBet);
			noMoreAction = true;
		}
		if (result != null)
			observeResult(result);
		if (myTurn())
			response = getResponse(msg, getAction());
		return response;
	}
	
	private void parseMoves(String moves) {
		if (prevMoves != null) {
			String newMove = moves.substring(prevMoves.length(),
					moves.endsWith("/") ? moves.length() - 1 : moves.length());
			int moveCnt = 0;
			for (int i = prevMoves.lastIndexOf('/') + 1; i < prevMoves.length(); i++)
				if (Character.isLetter(prevMoves.charAt(i)))
					moveCnt++;
			int id = stage == 0 ? ((moveCnt + position) % 2 == 1 ? myID : oppID)
					: ((moveCnt + position) % 2 == 0 ? myID : oppID);
			int otherTotalBet = id == myID ? oppTotalBet : myTotalBet;
			if (newMove.equals("f"))
				lastMove = new Fold(id);
			if (newMove.equals("c")) {
				if (lastMove instanceof Check || lastMove instanceof Call)
					lastMove = new Check(id);
				else
					lastMove = lastMove instanceof AllIn ? new AllIn(id, otherTotalBet) : new Call(id, otherTotalBet);
			}
			if (newMove.startsWith("r")) {
				int raiseToAmt = Integer.parseInt(newMove.substring(1));
				lastMove = raiseToAmt == Params.stackSize ? new AllIn(id, otherTotalBet)
						: new Raise(id, raiseToAmt, otherTotalBet);
			}
		}
		prevMoves = moves;
		if (moves.endsWith("/"))
			stage++;
	}

	private Result processLastMove() {
		if (lastMove == null)
			return null;
		if (lastMove instanceof Fold) {
			noMoreAction = true;
			return new WinBeforeShowdown(lastMove.playerID == myID ? oppID : myID, potSize(),
					lastMove.playerID == myID ? myTotalBet : oppTotalBet, board);
		}
		if (lastMove instanceof Call) {
			if (myTotalBet < oppTotalBet)
				myTotalBet = oppTotalBet;
			else
				oppTotalBet = myTotalBet;
		}
		if (lastMove instanceof Raise) {
			Raise raise = (Raise) lastMove;
			if (raise.playerID == myID)
				myTotalBet = raise.raiserTotalBet;
			else
				oppTotalBet = raise.raiserTotalBet;
		}
		if (lastMove instanceof AllIn) {
			if (lastMove.playerID == myID)
				myTotalBet = Params.stackSize;
			else
				oppTotalBet = Params.stackSize;
			if (myTotalBet == Params.stackSize && oppTotalBet == Params.stackSize)
				noMoreAction = true;
		}
		observeAction(lastMove);
		return null;
	}

	private void parseCards(String cards) {
		String[] sessions = cards.split("/");
		board = new Board();
		for (int i = 1; i < sessions.length; i++) {
			for (int j = 0; 2 * j < sessions[i].length(); j++)
				board.add(new Card(sessions[i].substring(2 * j, 2 * j + 2)));
		}
		int barIdx = sessions[0].indexOf('|');
		if (sessions[0].length() == 9)
			oppHoleCards = new HoleCards(
					position == 1 ? sessions[0].substring(0, barIdx) : sessions[0].substring(barIdx + 1), oppID);
	}

	private boolean myTurn() {
		if (noMoreAction)
			return false;
		if (lastMove == null) {
			return position == 1;
		}
		if (prevMoves.endsWith("/"))
			return position == 0;
		return lastMove.playerID == oppID;
	}

	private String getResponse(String state, ActionBase action) {
		return state + ":" + action.compress();
	}

	protected final int oppID = 0;
	protected final int myID;
	protected int myTotalBet;
	protected int oppTotalBet;
	protected HoleCards myHoleCards;
	protected HoleCards oppHoleCards;

	private int gameID;
	private int position;
	private int stage;
	private boolean noMoreAction;
	private String prevMoves;
	private ActionBase lastMove;
	private Board board;
}

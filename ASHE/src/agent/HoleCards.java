package agent;

class HoleCards {
	
	HoleCards(String holecards, int ownerID) {
		Card first = new Card(holecards.substring(0, 2));
		Card second = new Card(holecards.substring(2, 4));
		if (first.compareTo(second) < 0) {
			this.first = first;
			this.second = second;
		}
		else {
			this.first = second;
			this.second = first;
		}
		this.ownerID = ownerID;
	}
	
	Card getHighCard() {
		return first;
	}
	
	Card getKicker() {
		return second;
	}
	
	boolean contains(Card card) {
		return first.equals(card) || second.equals(card);
	}
	
	boolean paired() {
		return first.getRank() == second.getRank();
	}
	
	boolean suited() {
		return first.getSuit() == second.getSuit();
	}
	
	public String toString() {
		return first.toString() + second.toString();
	}
	
	private Card first;
	private Card second;
	int ownerID;
}

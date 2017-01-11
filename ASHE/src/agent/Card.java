package agent;

class Card implements Comparable<Card> {

	Card(int code) {
		this.code = code;
	}

	Card(String card) {
		char rankChar = card.charAt(0);
		char suitChar = card.charAt(1);
		int suit = 0;
		switch (suitChar) {
		case 's':
			suit = 0;
			break;
		case 'h':
			suit = 1;
			break;
		case 'd':
			suit = 2;
			break;
		case 'c':
			suit = 3;
			break;
		}
		int rank = 0;
		switch (rankChar) {
		case 'A':
			rank = 0;
			break;
		case 'K':
			rank = 1;
			break;
		case 'Q':
			rank = 2;
			break;
		case 'J':
			rank = 3;
			break;
		case 'T':
			rank = 4;
			break;
		case '9':
			rank = 5;
			break;
		case '8':
			rank = 6;
			break;
		case '7':
			rank = 7;
			break;
		case '6':
			rank = 8;
			break;
		case '5':
			rank = 9;
			break;
		case '4':
			rank = 10;
			break;
		case '3':
			rank = 11;
			break;
		case '2':
			rank = 12;
			break;
		}
		code = rank * 4 + suit;
	}

	public int compareTo(Card other) {
		return this.code - other.code;
	}

	int compareRank(Card other) {
		return this.getNumericRank() - other.getNumericRank();
	}

	public boolean equals(Object other) {
		if (!(other instanceof Card))
			return false;
		return ((Card) other).code == this.code;
	}

	public String toString() {
		return getSuit() + "" + getRank();
	}

	char getSuit() {
		int suit = code % 4;
		if (suit == 0)
			return 's';
		if (suit == 1)
			return 'h';
		if (suit == 2)
			return 'd';
		return 'c';
	}

	char getRank() {
		int rank = getNumericRank();
		if (rank == 13)
			return 'A';
		if (rank == 12)
			return 'K';
		if (rank == 11)
			return 'Q';
		if (rank == 10)
			return 'J';
		if (rank == 9)
			return 'T';
		if (rank == 8)
			return '9';
		if (rank == 7)
			return '8';
		if (rank == 6)
			return '7';
		if (rank == 5)
			return '6';
		if (rank == 4)
			return '5';
		if (rank == 3)
			return '4';
		if (rank == 2)
			return '3';
		return '2';
	}

	int getNumericRank() {
		return 13 - (code / 4);
	}

	private int code;

}

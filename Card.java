package 숙제7;

public class Card implements Comparable<Card>{
	
	private final Suit suit;
	private final Num num;
	
	public enum Suit {
	    SPADE("♠"), DIAMOND("♦"), HEART("♥"), CLUB("♣"); 
	    
	    private final String symbol;
	    Suit(String symbol) { this.symbol = symbol; }
	    
	    @Override
	    public String toString() { return this.symbol; }
	}
	
//	public enum Suit{
//		SPADE, HEART, DIAMOND, CLUB
//	}
	
//	public enum Num{
//		TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING, ACE
//	}
	
	public enum Num {
	    TWO("2"), THREE("3"), FOUR("4"), FIVE("5"), SIX("6"), SEVEN("7"), EIGHT("8"), NINE("9"),
	    TEN("10"), JACK("J"), QUEEN("Q"), KING("K"), ACE("A");
	    
	    private final String text;
	    Num(String text) { this.text = text; }
	    
	    @Override
	    public String toString() { return this.text; }
	}
	
	
	public Card(Suit suit, Num num) {
		this.suit = suit;
		this.num = num;
	}
	
	public Suit getSuit() { return suit; }
    public Num getNum() { return num; }

    @Override
    public String toString() {
        return num + " " + suit ;
    }

	@Override
	public int compareTo(Card o) {
		// TODO Auto-generated method stub
		return o.num.ordinal() - this.num.ordinal();
	}

	
    
    
}

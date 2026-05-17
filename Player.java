package 숙제7;

import java.util.ArrayList;
import java.util.List;

public class Player {

    // 이름, 소유칩, 카드(2장)
    private String name;
    private int account;
    private final List<Card> hands;
    private int currentBet;
    private boolean isFold;
    
    public void win(int amount) {
        this.account += amount;
    }
    
    public void fold() {
    	this.isFold = true;
    }
    
    public void bet(int amount) {
    	account -= amount;
    	currentBet += amount;
    }
    
    public void initRound() {
    	currentBet = 0;
    }
    
    public Player(String name, int account) {
        this.name = name;
        this.account = account;
        hands = new ArrayList<>();
        
    }
    
    public void receiveCard(Card card) {
        hands.add(card);
    }
    
    public void showHands() {
    	System.out.println();
        System.out.print(name + "님의 핸드: ");
        System.out.println(hands);
        
    }
    
    public List<Card> getHands() {
		return hands;
	}

	public String getName() { return name; }
    public int getAccount() { return account; }
    
    public int getCurrentBet() { return currentBet; }
    public boolean isFold() { return isFold; }
}
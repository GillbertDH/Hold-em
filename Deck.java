package 숙제7;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {
    
    private final List<Card> cards;

    public Deck() {
        cards = new ArrayList<>(); 
        
        for (Card.Suit suit : Card.Suit.values()) {
            for (Card.Num num : Card.Num.values()) {
                
                Card newCard = new Card(suit, num);
                
                cards.add(newCard); 
            }
        }
    }
    
    public void shuffle() {
    	System.out.println("카드를 섞습니다... ");
    	Collections.shuffle(cards);
    }
    
    public int cardCnt() {
    	return cards.size();
    }
    
    // 카드 뽑기
    public Card draw() {
    	
    	if(cards.isEmpty()) {
    		System.out.println("카드가 없습니다...");
    		return null;
    	}
    	
    	int lastIdx = cards.size() - 1;
    	return cards.remove(lastIdx);
    }
    
}
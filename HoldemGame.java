package 숙제7;

import java.util.*;

import 숙제7.HandRank.Rank;

public class HoldemGame {
	private Scanner sc = new Scanner(System.in);
	private int pot;
    private Deck deck;
    private List<Player> players;
    private List<Card> communityCards;
    
    public void init() {
    	pot = 0;
        System.out.println("===홀덤 게임을 세팅합니다...===");
        deck = new Deck();
        deck.shuffle();
        System.out.println("덱 세팅 완료...");
        
        players = new ArrayList<>();
        players.add(new Player("Hero", 100));
        players.add(new Player("Villan", 100));
        System.out.println("플레이어(2명) 설정 완료...");
        
        communityCards = new ArrayList<>();
    }
    
    public void run() {
        System.out.println("\n===홀덤 게임을 시작합니다...===");
        System.out.println("카드를 딜링합니다...");
        
        for(int i=0; i<2; i++) { 
            for (Player p : players) p.receiveCard(deck.draw()); 
        }
        
        Player hero = players.get(0);
        hero.showHands();
        Player villan = players.get(1);
        
        hero.bet(10);
        villan.bet(10);
        pot += 20;
        
        for(int i=0; i<3; i++) communityCards.add(deck.draw());
        System.out.println("\n 보드(플랍): " + communityCards);
        
        communityCards.add(deck.draw());
        System.out.println(" 보드(플랍+턴): " + communityCards);
        
        communityCards.add(deck.draw());
        System.out.println(" 보드(플랍+턴+리버): " + communityCards);
        
        // 쇼다운
        List<Card> heroTotalCards = new ArrayList<>();
        List<Card> villanTotalCards = new ArrayList<>();
        
        heroTotalCards.addAll(hero.getHands());
        villanTotalCards.addAll(villan.getHands());
        heroTotalCards.addAll(communityCards);
        villanTotalCards.addAll(communityCards);
        
        // 플러시 및 하이카드 점수판을 위해 내림차순 정렬 필수
        Collections.sort(heroTotalCards);
        Collections.sort(villanTotalCards);
        
        HandResult heroResult = evaluatehands(heroTotalCards);
        HandResult villanResult = evaluatehands(villanTotalCards);
        
        System.out.println("\nHero의 족보: " + heroResult);
        System.out.println("Villain의 족보: " + villanResult);

        System.out.println("\n=== 결 과 ===");
        if (heroResult.compareTo(villanResult) > 0) {
            System.out.println("Hero 승리!");
            hero.win(pot);
        } else if (heroResult.compareTo(villanResult) < 0) {
            System.out.println("Villain 승리!");
            villan.win(pot);
        } else {
            System.out.println("무승부 (Chop)!");
        }
        
        System.out.println(hero.getAccount());
    }
    
    public HandResult evaluatehands(List<Card> totalCards) {
        
        int[] suitCnt = new int[4];
        int[] numCnt = new int[13];
        
        for(Card c: totalCards) {
            numCnt[c.getNum().ordinal()]++;
            suitCnt[c.getSuit().ordinal()]++;
        }
        
        int pairs = 0, triples = 0, fourCards = 0;
        
        for(int i = 12; i >= 0; i--) {
            if(numCnt[i] == 4) fourCards++;
            else if(numCnt[i] == 3) triples++;
            else if(numCnt[i] == 2) pairs++;
        }

        // 플러시 검사
        boolean isFlush = false;
        int flushIdx = -1;
        for(int i=0; i < suitCnt.length; i++) {
            if(suitCnt[i] >= 5) {
                isFlush = true;
                flushIdx = i;
            }
        }
        
        // 스트레이트 플러시 검사
        boolean isStraightFlush = false;
        int straightFlushCnt = 0;
        int straightFlushHigh = -1;
        
        if(isFlush) {
            int[] flushNumCnt = new int[13];
            for(Card c: totalCards) {
                if(c.getSuit().ordinal() == flushIdx) {
                    flushNumCnt[c.getNum().ordinal()]++;
                }
            }
            
            for(int i = 0; i < 13; i++) {
                if(flushNumCnt[i] > 0) {
                    straightFlushCnt++;
                    if(straightFlushCnt >= 5) { // 6장 연속일 때도 대장 숫자가 업데이트 되도록 >= 적용
                        isStraightFlush = true;
                        straightFlushHigh = i; 
                    }
                } else {
                    straightFlushCnt = 0;
                }
            }
            
            if(!isStraightFlush && flushNumCnt[0] > 0 && flushNumCnt[1] > 0 && flushNumCnt[2] > 0 && flushNumCnt[3] > 0 && flushNumCnt[12] > 0) {
                isStraightFlush = true;
                straightFlushHigh = 3; 
            }
        }
        
        // 일반 스트레이트 검사
        boolean isStraight = false;
        int straightCnt = 0;
        int straightHigh = -1;
        
        for(int i=0; i < 13; i++) {
            if(numCnt[i] > 0) {
                straightCnt++;
                if(straightCnt >= 5) { // 교정 완료
                    isStraight = true;
                    straightHigh = i;
                }
            } else {
                straightCnt = 0;
            }
        }
        
        if(!isStraight && numCnt[0] > 0 && numCnt[1] > 0 && numCnt[2] > 0 && numCnt[3] > 0 && numCnt[12] > 0) {
            isStraight = true;
            straightHigh = 3;
        }
        
        // === 족보 판별 폭포수 및 점수판(HandResult) 반환 ===
        
        if(isStraightFlush) {
            return new HandResult(Rank.STRAIGHT_FLUSH, Arrays.asList(straightFlushHigh));
        }
        else if (fourCards == 1) {
            int fourValue = -1, kicker = -1;
            for(int i=12; i>=0; i--) {
                if(numCnt[i] == 4) fourValue = i;
                else if(numCnt[i] >= 1 && kicker == -1) kicker = i;
            }
            return new HandResult(Rank.FOUR_OF_A_KIND, Arrays.asList(fourValue, kicker));
        }   
        else if ((triples == 1 && pairs >= 1) || triples >= 2) {
            int tripleValue = -1, pairValue = -1;
            for(int i = 12; i >= 0; i--) {
                if(numCnt[i] == 3 && tripleValue == -1) tripleValue = i;
                else if(numCnt[i] >= 2 && pairValue == -1) pairValue = i;
            }
            return new HandResult(Rank.FULL_HOUSE, Arrays.asList(tripleValue, pairValue));
        } 
        else if(isFlush) { 
            List<Integer> flushCards = new ArrayList<>();
            for(Card c : totalCards) {
                if(c.getSuit().ordinal() == flushIdx) {
                    flushCards.add(c.getNum().ordinal()); 
                    if(flushCards.size() == 5) break;
                }
            }
            return new HandResult(Rank.FLUSH, flushCards);
        }
        else if(isStraight) {
            return new HandResult(Rank.STRAIGHT, Arrays.asList(straightHigh));
        }
        else if (triples >= 1) {
            int tripleval = -1;
            List<Integer> kickers = new ArrayList<>(); 
            for(int i = 12; i >= 0; i--) {
                if(numCnt[i] == 3) tripleval = i;
                else if(numCnt[i] == 1) kickers.add(i); 
            }
            return new HandResult(Rank.TRIPLE, Arrays.asList(tripleval, kickers.get(0), kickers.get(1)));
        } 
        else if (pairs >= 2) {
            List<Integer> twoPValue = new ArrayList<>(); 
            int kicker = -1;
            for(int i = 12; i >= 0; i--) {
                if(numCnt[i] == 2 && twoPValue.size() < 2) twoPValue.add(i); 
                else if(numCnt[i] >= 1 && kicker == -1) kicker = i; 
            }
            return new HandResult(Rank.TWOPAIR, Arrays.asList(twoPValue.get(0), twoPValue.get(1), kicker));
        }
        else if (pairs == 1) {
            int pairValue = -1;
            List<Integer> kickers = new ArrayList<>();
            for(int i = 12; i >= 0; i--) {
                if(numCnt[i] == 2) pairValue = i; 
                else if(numCnt[i] == 1) kickers.add(i); 
            }
            return new HandResult(Rank.ONEPAIR, Arrays.asList(pairValue, kickers.get(0), kickers.get(1), kickers.get(2)));
        } 
        else {
            List<Integer> kickers = new ArrayList<>();
            for(int i = 12; i >= 0; i--) {
                if(numCnt[i] == 1) kickers.add(i);
            }
            return new HandResult(Rank.HIGH_CARD, Arrays.asList(kickers.get(0), kickers.get(1), kickers.get(2), kickers.get(3), kickers.get(4)));
        }
    }
}
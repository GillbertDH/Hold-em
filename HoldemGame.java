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

	public boolean checkFoldGameEnd() {
		Player hero = players.get(0);
		Player villan = players.get(1);

		if (hero.isFold()) {
			System.out.println("\n[결과] Hero가 폴드했습니다. Villain이 판돈 " + pot + " 달러를 획득합니다!");
			villan.win(pot);
			return true;
		} else if (villan.isFold()) {
			System.out.println("\n[결과] Villain이 폴드했습니다. Hero가 판돈 " + pot + " 달러를 획득합니다!");
			hero.win(pot);
			return true;
		}
		return false;
	}

	public void bettingRound(String roundName) {
		System.out.println("\n=== [" + roundName + " 베팅 시작] ===");
        
		for (Player p : players) p.initRound();
        
		int currentHighestBet = 0;
		boolean actionNeeded = true;
        
		while (actionNeeded) {
			actionNeeded = false;
            
			for (Player p : players) {
				if (p.isFold()) continue;
				if (checkFoldGameEnd()) return;
                
				if (p.getCurrentBet() < currentHighestBet || currentHighestBet == 0) {
					actionNeeded = true;
                    
					System.out.println("\n▶ [" + p.getName() + "]님의 차례 (잔액: " + p.getAccount() + ")");
					int needToCall = currentHighestBet - p.getCurrentBet();
					System.out.println("현재 최고 베팅액: " + currentHighestBet + " (콜하려면 " + needToCall + " 필요)");
					System.out.println("1. 폴드(Fold)  2. 콜/체크(Call/Check)  3. 레이즈(Raise)");
					System.out.print("선택: ");
                    
					int choice = 0;
					if (p.getName().equals("Villan")) {
						System.out.println("\n빌런(Villain)이 고민 중입니다...");
						try { Thread.sleep(1500); } catch(Exception e){}
                        
						int rand = (int)(Math.random() * 10);
						if (rand < 1) choice = 1;      
						else if (rand < 8) choice = 2; 
						else choice = 3;               
					} else {
						choice = sc.nextInt(); 
					}
                    
					if (choice == 1) {
						System.out.println(p.getName() + " 폴드!");
						p.fold();
					} 
					else if (choice == 2) {
						if (needToCall > p.getAccount()) needToCall = p.getAccount();
						p.bet(needToCall);
						pot += needToCall;
						System.out.println(p.getName() + " 콜/체크! (지불: " + needToCall + " | 팟: " + pot + ")");
					} 
					else if (choice == 3) {
						int raiseAmount = 0;
						if (p.getName().equals("Villan")) {
							raiseAmount = (int)(Math.random() * 21) + 10; 
							System.out.println("추가로 얼마를 더 올리겠습니까?: " + raiseAmount + " (빌런 자동 선택)");
						} else {
							System.out.print("추가로 얼마를 더 올리겠습니까?: ");
							raiseAmount = sc.nextInt();
						}
                        
						int totalToPay = needToCall + raiseAmount;
                        
						if (totalToPay > p.getAccount()) totalToPay = p.getAccount();
						p.bet(totalToPay);
						pot += totalToPay;
                        
						currentHighestBet = p.getCurrentBet();
						System.out.println(p.getName() + " 레이즈! (지불: " + totalToPay + " | 팟: " + pot + ")");
					}
				}
			}
			if (currentHighestBet == 0 && !actionNeeded) break; 
		}
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

		bettingRound("프리플랍");
		if (checkFoldGameEnd()) return;
        
		for(int i=0; i<3; i++) communityCards.add(deck.draw());
		System.out.println("\n 보드(플랍): " + communityCards);
        
		bettingRound("플랍");
		if (checkFoldGameEnd()) return;

		communityCards.add(deck.draw());
		System.out.println("\n 보드(플랍+턴): " + communityCards);
        
		bettingRound("턴");
		if (checkFoldGameEnd()) return;

		communityCards.add(deck.draw());
		System.out.println("\n 보드(플랍+턴+리버): " + communityCards);
        
		bettingRound("리버");
		if (checkFoldGameEnd()) return;
        
		List<Card> heroTotalCards = new ArrayList<>();
		List<Card> villanTotalCards = new ArrayList<>();
        
		heroTotalCards.addAll(hero.getHands());
		villanTotalCards.addAll(villan.getHands());
		heroTotalCards.addAll(communityCards);
		villanTotalCards.addAll(communityCards);
        
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
			hero.win(pot / 2);
			villan.win(pot / 2);
		}
        
		System.out.println("\n[최종 잔액] Hero: " + hero.getAccount() + " | Villain: " + villan.getAccount());
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

		boolean isFlush = false;
		int flushIdx = -1;
		for(int i=0; i < suitCnt.length; i++) {
			if(suitCnt[i] >= 5) {
				isFlush = true;
				flushIdx = i;
			}
		}
        
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
					if(straightFlushCnt >= 5) {
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
        
		boolean isStraight = false;
		int straightCnt = 0;
		int straightHigh = -1;
        
		for(int i=0; i < 13; i++) {
			if(numCnt[i] > 0) {
				straightCnt++;
				if(straightCnt >= 5) {
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
        
		if(isStraightFlush) {
			List<Integer> tieBreakers = Arrays.asList(straightFlushHigh);
			return new HandResult(Rank.STRAIGHT_FLUSH, tieBreakers);
		}
		else if (fourCards == 1) {
			int fourValue = -1;
			int kicker = -1;
			for(int i=12; i>=0; i--) {
				if(numCnt[i] == 4) {
					fourValue = i;
				}
				else if(numCnt[i] >= 1 && kicker == -1) kicker = i;
			}
			List<Integer> tieBreakers = Arrays.asList(fourValue, kicker);
			return new HandResult(Rank.FOUR_OF_A_KIND, tieBreakers);
		}   
		else if ((triples == 1 && pairs >= 1) || triples >= 2) {
			int tripleValue = -1;
			int pairValue = -1;

			for(int i = 12; i >= 0; i--) {
				if(numCnt[i] == 3 && tripleValue == -1) {
					tripleValue = i;
				} 
				else if(numCnt[i] >= 2 && pairValue == -1) {
					pairValue = i;
				}
			}

			List<Integer> tieBreakers = Arrays.asList(tripleValue, pairValue);
			return new HandResult(Rank.FULL_HOUSE, tieBreakers);
		} 
		else if(isFlush) { 
			List<Integer> flushCards = new ArrayList<>();
			for(Card c : totalCards) {
				if(c.getSuit().ordinal() == flushIdx) {
					flushCards.add(c.getNum().ordinal()); 
					if(flushCards.size() == 5) {
						break;
					}
				}
			}
			return new HandResult(Rank.FLUSH, flushCards);
		}
		else if(isStraight) {
			List<Integer> tieBreakers = Arrays.asList(straightHigh);
			return new HandResult(Rank.STRAIGHT, tieBreakers);
		}
		else if (triples >= 1) {
			int tripleval = -1;
			List<Integer> kickers = new ArrayList<>(); 
			for(int i = 12; i >= 0; i--) {
				if(numCnt[i] == 3) {
					tripleval = i;
				} else if(numCnt[i] == 1) {
					kickers.add(i); 
				}
			}
			List<Integer> tieBreakers = Arrays.asList(tripleval, kickers.get(0), kickers.get(1));
			return new HandResult(Rank.TRIPLE, tieBreakers);
		} 
		else if (pairs >= 2) {
			List<Integer> twoPValue = new ArrayList<>(); 
			int kicker = -1;

			for(int i = 12; i >= 0; i--) {
				if(numCnt[i] == 2 && twoPValue.size() < 2) {
					twoPValue.add(i); 
				} 
				else if(numCnt[i] >= 1 && kicker == -1) {
					kicker = i; 
				}
			}
			List<Integer> tieBreakers = Arrays.asList(twoPValue.get(0), twoPValue.get(1), kicker);
			return new HandResult(Rank.TWOPAIR, tieBreakers);
		} 
		else if (pairs == 1) {
			int pairValue = -1;
			List<Integer> kickers = new ArrayList<>(); 
			for(int i = 12; i >= 0; i--) {
				if(numCnt[i] == 2) {
					pairValue = i; 
				} else if(numCnt[i] == 1) {
					kickers.add(i); 
				}
			}
			List<Integer> tieBreakers = Arrays.asList(pairValue, kickers.get(0), kickers.get(1), kickers.get(2));
			return new HandResult(Rank.ONEPAIR, tieBreakers);
		} 
		else {
			List<Integer> kickers = new ArrayList<>();
			for(int i = 12; i >= 0; i--) {
				if(numCnt[i] == 1) {
					kickers.add(i);
				}
			}
			List<Integer> tieBreakers = Arrays.asList(kickers.get(0), kickers.get(1), kickers.get(2), kickers.get(3), kickers.get(4));
			return new HandResult(Rank.HIGH_CARD, tieBreakers);
		}
	}
}
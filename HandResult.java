package 숙제7;

import java.util.List;

public class HandResult implements Comparable<HandResult> {
    private HandRank.Rank rank;
    private List<Integer> tieBreakers; 

    public HandResult(HandRank.Rank rank, List<Integer> tieBreakers) {
        this.rank = rank;
        this.tieBreakers = tieBreakers;
    }

    public HandRank.Rank getRank() {
        return rank;
    }

    @Override
    public int compareTo(HandResult o) {
        
        if (this.rank != o.rank) {
            return this.rank.compareTo(o.rank);
        }
        
        int size = Math.min(this.tieBreakers.size(), o.tieBreakers.size());
        for (int i = 0; i < size; i++) {
            if (this.tieBreakers.get(i) > o.tieBreakers.get(i)) return 1;
            if (this.tieBreakers.get(i) < o.tieBreakers.get(i)) return -1;
        }
        
        return 0;
    }

    @Override
    public String toString() {
        return rank.toString() + " (상세: " + tieBreakers + ")";
    }
}
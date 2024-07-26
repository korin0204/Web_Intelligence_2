package work4a;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ABPairList {
	//リスト1(a,b)　リスト2(a,b)というセットを作る
    private List<Pair> pairList;

    public ABPairList() {
        this.pairList = new ArrayList<>();
    }

    private class Pair {
        String a;
        String b;

        Pair(String a, String b) {
            this.a = a;
            this.b = b;
        }

        @Override
        public String toString() {
            return "(" + a + ", " + b + ")";
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Pair pair = (Pair) obj;
            return Objects.equals(a, pair.a) && Objects.equals(b, pair.b);
        }

        @Override
        public int hashCode() {
            return Objects.hash(a, b);
        }
    }

    // リストにペアを追加するメソッド
    public void addPair(String a, String b) {
        pairList.add(new Pair(a, b));
    }

    public List<String> getValues(String a) {
        List<String> values = new ArrayList<>();
        for (Pair pair : pairList) {
            if (pair.a.equals(a)) {
                values.add(pair.b);
            }
        }
        return values;
    }
    
    public int getSize() {
        return pairList.size();
    }

    public void printPairs() {
        for (Pair pair : pairList) {
            System.out.println(pair);
        }
    }
    
    public String getAat(int index) {
        if (index >= 0 && index < pairList.size()) {
            return pairList.get(index).a;
        } else {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + pairList.size());
        }
    }
    
    public String getBat(int index) {
        if (index >= 0 && index < pairList.size()) {
            return pairList.get(index).b;
        } else {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + pairList.size());
        }
    }

//    public static void main(String[] args) {
//        ABPairList abPairList = new ABPairList();
//
//        // Adding pairs
//        abPairList.addPair("火星", "衛星");
//        abPairList.addPair("火星", "大気");
//        abPairList.addPair("火星", "地形");
//        abPairList.addPair("地球", "衛星");
//
//        // Retrieving values
//        System.out.println("火星の情報: " + abPairList.getValues("火星"));
//        System.out.println("地球の情報: " + abPairList.getValues("地球"));
//
//        // Printing all pairs
//        System.out.println("全ペア:");
//        abPairList.printPairs();
//    }
}

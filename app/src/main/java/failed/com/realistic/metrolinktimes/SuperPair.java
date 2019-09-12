package failed.com.realistic.metrolinktimes;

import java.util.ArrayList;


public class SuperPair {
    ArrayList<String> one, two = new ArrayList<String>();
    public SuperPair (ArrayList<String> p, ArrayList<String> o, int i) {
        one = p;
        two = o;
        k = i;
    }
    private int k;
    public int getK() { return k; }
    public ArrayList<String> getName() {
        return one;
    }
    public ArrayList<String> getValue() {
        return two;
    }
}

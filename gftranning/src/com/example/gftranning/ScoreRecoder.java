
package com.example.gftranning;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ScoreRecoder {

    public int computeScore(int distance, int total, int succeed) {
        if (total == 0) {
            return 0;
        } else if (total == succeed) {
            return total * (distance + 1);
        }
        double score = (1.0 * succeed) / total;
        score = Math.sqrt(score);
        return (int) Math.floor(score * total * (distance + 1));
    }

    public List<Integer> parseToScoreNumber(Set<String> records) {
        ArrayList<Integer> result = new ArrayList<Integer>();
        for (String aRecord : records) {
            int score = Integer.parseInt(aRecord);
            result.add(Integer.valueOf(score));
        }
        return result;
    }

    public Set<String> parseToRecords(List<Integer> scores) {
        HashSet<String> result = new HashSet<String>();
        for (Integer integer : scores) {
            if (integer != null) {
                result.add("" + integer);
            }
        }
        return result;
    }

}

import java.util.Arrays;

class Solution {
    public int stoneGameVI(int[] A, int[] B) {
        int n = A.length;
        int[][] sums = new int[n][];
        for (int i = 0; i < n; i++) {
            sums[i] = new int[]{A[i] + B[i], A[i], B[i]};
        }
        Arrays.sort(sums, (a, b) -> Integer.compare(b[0], a[0]));
        int a = 0;
        int b = 0;
        for (int i = 0; i < n; i++) {
            if (i % 2 == 0) {
                a += sums[i][1];
            }
            else {
                b += sums[i][2];
            }
        }

        return Integer.compare(a, b);
    }
    public String[] compare(String[] words){

        Arrays.sort(words, (first, second) -> Integer.compare(second.length(), first.length()));
        return words;
    }
}

public class leetcode {

    public static void main(String[] args){
        int[] A = new int[]{1,3};
        int[] B = new int[]{2,1};
        String[] words = new String[]{"ff", "kkk", "a"};
        String[] res2 = new Solution().compare(words);
        System.out.println(res2);
        int res = new Solution().stoneGameVI(A,B);
        System.out.println(res);
    }
}
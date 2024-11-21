package swyneai.deprecated;


public class Groups {
    public static void main(String[] args) {
        Token t1 = new Token("волк", 2, 0);
        Token t2 = new Token("волку", 2, 1);
        System.out.println(levenshtein(t1, t2));
    }
    public static void addGroup(String name, Token... tokens) {
        for (Token t : tokens) {
            t.addGroup(name, tokens);
        }
    }
    public static void checkSequences(Token t1, Token t2) {
        String a = t1.getName();
        String b = t2.getName();
        int m = a.length();
        int n = b.length();
        int[][] dp = new int[m + 1][n + 1];
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (a.charAt(i - 1) == b.charAt(j - 1)) {
                    dp[i][j] = dp[i-1][j-1] + 1;
                } else {
                    dp[i][j] = 0;
                }
            }
        }
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (dp[i][j] > 0) {
                    addGroup(a.substring(i - dp[i][j], i), t1, t2);
                }
            }
        }
    }
    public static double levenshtein(Token t1, Token t2) {
        String token1 = t1.getName();
        String token2 = t2.getName();
        int[][] distances = new int[token1.length()+1][token2.length()+1];
        for (int i = 0; i <= token1.length(); i++) {
            distances[i][0] = i;
        }
        for (int i = 0; i <= token2.length(); i++) {
            distances[0][i] = i;
        }
        int a;
        int b;
        int c;
        for (int i = 1; i <= token1.length(); i++) {
            for (int j = 1; j <= token2.length(); j++) {
                if (token1.charAt(i-1) == token2.charAt(j-1)) {
                    distances[i][j] = distances[i-1][j-1];
                } else {
                    a = distances[i][j-1];
                    b = distances[i-1][j];
                    c = distances[i-1][j-1];
                    if (a <= b && a <= c) {
                        distances[i][j] = a + 1;
                    }
                    else if (b <= a && b <= c) {
                        distances[i][j] = b + 1;
                    }
                    else {
                        distances[i][j] = c + 1;
                    }
                }
            }
        }
        int distance = distances[token1.length()][token2.length()];
        int max = Math.max(token1.length(), token2.length());
        return (double) distance / (double) max;
    }
}

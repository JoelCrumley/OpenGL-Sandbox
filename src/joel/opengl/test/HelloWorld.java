package joel.opengl.test;

import java.text.DecimalFormat;
import java.util.Arrays;

public class HelloWorld {

    private void run(String[] args) {

        int maxN = 20, correctReward = 15, incorrectReward = -30;
        double fairWeight = 0.5, cheaterWeight = 0.75, probabilityCheater = 0.5;
        DecimalFormat df = new DecimalFormat("#0.00000");

        for (int n = 1; n <= maxN; n++) {
            double[] fairDistribution = binomialProbabilities(n, fairWeight);
            double[] cheaterDistribution = binomialProbabilities(n, cheaterWeight);

            double[] cumulativeFairDistribution = cumulativeProbability(fairDistribution);
            double[] cumulativeCheaterDistribution = cumulativeProbability(cheaterDistribution);

            double fairMean = fairWeight * n, cheaterMean = cheaterWeight * n;
            double fairDeviation = Math.sqrt(n * fairWeight * (1.0-fairWeight)), cheaterDeviation = Math.sqrt(n * cheaterWeight * (1.0-cheaterWeight));

            System.out.println("\nn = " + n);

            String[][] table = new String[n+2][7];
            table[0] = new String[] { "x:", "fX=x", "fX>=x", "fX<=x", "cX=x", "cX>=x", "cX<=x" };

            for (int i = 0; i <= n; i++) {
//                double fairMeanDistance = (double)i - fairMean, cheaterMeanDistance = (double)i - cheaterMean;
//                double fairDeviationDistance = fairMeanDistance / fairDeviation, cheaterDeviationDistance = cheaterMeanDistance / cheaterDeviation;
                double flessequal = 1.0 - cumulativeFairDistribution[i] + fairDistribution[i];
                double clessequal = 1.0 - cumulativeCheaterDistribution[i] + cheaterDistribution[i];
                table[i+1] = new String[] { i + ":", df.format(fairDistribution[i]), df.format(cumulativeFairDistribution[i]), df.format(flessequal),
                        df.format(cheaterDistribution[i]), df.format(cumulativeCheaterDistribution[i]), df.format(clessequal)};
//                System.out.println(i + ": " + df.format(fairDistribution[i]) + " " + df.format(cumulativeFairDistribution[i]) + " " + df.format(flessequal) + " " +
//                        df.format(cheaterDistribution[i]) + " " + df.format(cumulativeCheaterDistribution[i]) + " " + df.format(clessequal)
//                        );
            }

            System.out.println(Arrays.deepToString(table).replace("], ", "]\n"));

        }

    }

//    System.out.println(Arrays.deepToString(twoDm).replace("], ", "]\n"));

    public double[] cumulativeProbability(double[] distribution) {
        double[] probabilities = new double[distribution.length];
        probabilities[0] = distribution[0];
        for (int i = 1; i < probabilities.length; i++) probabilities[i] = distribution[i] + probabilities[i-1];
        return probabilities;
    }

    public double[] binomialProbabilities(int n, double p) {
        double[] probabilities = new double[n+1];
        for (int i = 0; i <= n; i++) probabilities[i] = choose(n, i) * Math.pow(p, i) * Math.pow(1-p, n-i);
        return probabilities;
    }

    public double choose(int n, int k) {
        // returns n choose k)
        if (k == 0) return 1;
        if (k < 1) return -1;
        if (k > n) return -1;
        if (n == k) return 1;
        return (double) product(k+1, n) / (double) product(1, n-k);
    }

    public int product(int start, int end) {
        if (end < start) {
            int dummy = end;
            end = start;
            start = dummy;
        }
        int product = 1;
        for (int i = start; i <= end; i++) product *= i;
        return product;
    }

    private HelloWorld(String[] args) {
        run(args);
    }

    public static void main(String[] args) {
        new HelloWorld(args);
    }

}

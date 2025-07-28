import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.util.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public class ShamirSecretSolver {
    static class Share {
    BigInteger x;
    BigInteger y;

    Share(BigInteger x, BigInteger y) {
        this.x = x;
        this.y = y;
    }
}

public static void main(String[] args) {
    if (args.length == 0) {
        System.out.println("Usage: java ShamirSecretSolver <path_to_json>");
        return;
    }

    String jsonFilePath = args[0];
    System.out.println("Solving for: " + jsonFilePath);
    try {
        solve(jsonFilePath);
    } catch (Exception e) {
        e.printStackTrace();
    }
}

public static void solve(String jsonPath) throws Exception {
    JSONObject json = new JSONObject(new JSONTokener(new FileInputStream(jsonPath)));

    int n = json.getInt("n");
    int k = json.getInt("k");

    JSONArray sharesArray = json.getJSONArray("shares");

    List<Share> allShares = new ArrayList<>();
    for (int i = 0; i < n; i++) {
        String expr = sharesArray.getString(i);
        String[] parts = expr.split(",");
        BigInteger x = new BigInteger(parts[0].trim());
        BigInteger y = new BigInteger(parts[1].trim());
        allShares.add(new Share(x, y));
    }

    // Map to count secret occurrences from different combinations
    Map<BigInteger, Integer> secretFrequency = new HashMap<>();

    List<List<Share>> combinations = getCombinations(allShares, k);

    for (List<Share> combination : combinations) {
        try {
            BigInteger secret = lagrangeInterpolation(combination);
            secretFrequency.put(secret, secretFrequency.getOrDefault(secret, 0) + 1);
        } catch (ArithmeticException e) {
            System.out.println("Skipping invalid combination: " + e.getMessage());
        }
    }

    BigInteger correctSecret = secretFrequency.entrySet()
        .stream()
        .max(Map.Entry.comparingByValue())
        .map(Map.Entry::getKey)
        .orElseThrow(() -> new RuntimeException("Unable to determine the correct secret"));

    System.out.println("Reconstructed Secret: " + correctSecret);
}

private static BigInteger lagrangeInterpolation(List<Share> shares) {
    BigInteger secret = BigInteger.ZERO;

    for (int i = 0; i < shares.size(); i++) {
        BigInteger xi = shares.get(i).x;
        BigInteger yi = shares.get(i).y;

        BigInteger numerator = BigInteger.ONE;
        BigInteger denominator = BigInteger.ONE;

        for (int j = 0; j < shares.size(); j++) {
            if (i != j) {
                BigInteger xj = shares.get(j).x;
                BigInteger diff = xi.subtract(xj);
                if (diff.equals(BigInteger.ZERO)) {
                    throw new ArithmeticException("Duplicate x-values: " + xi + ", " + xj);
                }

                numerator = numerator.multiply(xj.negate());
                denominator = denominator.multiply(diff);
            }
        }

        BigInteger term = yi.multiply(numerator).divide(denominator);
        secret = secret.add(term);
    }

    return secret;
}

private static List<List<Share>> getCombinations(List<Share> shares, int k) {
    List<List<Share>> result = new ArrayList<>();
    combine(shares, k, 0, new ArrayList<>(), result);
    return result;
}

private static void combine(List<Share> shares, int k, int start, List<Share> temp, List<List<Share>> result) {
    if (temp.size() == k) {
        result.add(new ArrayList<>(temp));
        return;
    }

    for (int i = start; i < shares.size(); i++) {
        temp.add(shares.get(i));
        combine(shares, k, i + 1, temp, result);
        temp.remove(temp.size() - 1);
    }
}

}

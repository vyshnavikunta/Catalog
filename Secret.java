import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Secret {
    public static void main(String[] args) {
        try {
            // Step 1: Read JSON file
            String filePath = "testcases.json"; // Path to your JSON file
            ObjectMapper objectMapper = new ObjectMapper();

            // Parse JSON into a Map
            Map<String, Object> data = objectMapper.readValue(new File(filePath), Map.class);

            // Extract keys
            Map<String, Object> keys = (Map<String, Object>) data.get("keys");
            int n = toInt(keys.get("n")); // Convert to int safely
            int k = toInt(keys.get("k")); // Convert to int safely

            // Extract and decode points
            List<Point> points = new ArrayList<>();
            for (String key : data.keySet()) {
                if (key.matches("\\d+")) { // Check if the key is numeric
                    int x = Integer.parseInt(key);
                    Map<String, String> valueObj = (Map<String, String>) data.get(key);
                    int base = toInt(valueObj.get("base")); // Safely convert to int
                    String value = valueObj.get("value");

                    // Convert the base value to a BigInteger
                    BigInteger y = new BigInteger(value, base); // Decode y as BigInteger
                    points.add(new Point(x, y));
                }
            }

            // Step 3: Find the constant term (c) using Lagrange interpolation
            BigInteger secret = lagrangeInterpolation(points, k);
            System.out.println("Secret (c): " + secret);

        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
        }
    }

    // Safely convert Object to int
    private static int toInt(Object obj) {
        if (obj instanceof Integer) {
            return (Integer) obj;
        } else if (obj instanceof String) {
            return Integer.parseInt((String) obj);
        }
        throw new IllegalArgumentException("Unable to convert to int: " + obj);
    }

    // Lagrange interpolation to find the constant term (c) using BigInteger
    public static BigInteger lagrangeInterpolation(List<Point> points, int k) {
        BigInteger c = BigInteger.ZERO;

        for (int i = 0; i < k; i++) {
            Point p1 = points.get(i);
            BigInteger term = p1.y;

            for (int j = 0; j < k; j++) {
                if (i != j) {
                    Point p2 = points.get(j);
                    BigInteger numerator = BigInteger.valueOf(-p2.x);
                    BigInteger denominator = BigInteger.valueOf(p1.x - p2.x);
                    term = term.multiply(numerator).divide(denominator);
                }
            }

            c = c.add(term);
        }

        return c; // Return the result as BigInteger
    }

    // Helper class to store a point (x, y)
    static class Point {
        int x;
        BigInteger y;

        Point(int x, BigInteger y) {
            this.x = x;
            this.y = y;
        }
    }
}

public class MatchingEngine {

  public static int calculateMatchScore(
        String lostName,
        String foundName,
        String lostDesc,
        String foundDesc,
        String lostCategory,
        String foundCategory,
        String lostColor,
        String foundColor,
        String lostBrand,
        String foundBrand,
        String lostLocation,
        String foundLocation,
        String lostOCR,
        String foundOCR
) {

        int score = 0;

        score += textSimilarity(lostName, foundName) * 25 / 100;
        score += textSimilarity(lostDesc, foundDesc) * 25 / 100;

        if (equalsIgnoreCase(lostCategory, foundCategory)) {
            score += 15;
        }

        if (equalsIgnoreCase(lostColor, foundColor)) {
            score += 10;
        }

        if (equalsIgnoreCase(lostBrand, foundBrand)) {
            score += 10;
        }

        score += textSimilarity(lostLocation, foundLocation) * 15 / 100;
        score += textSimilarity(lostOCR, foundOCR) * 25 / 100;

        return Math.min(score, 100);
    }

    public static int textSimilarity(String a, String b) {
        if (a == null || b == null) return 0;

        a = a.toLowerCase();
        b = b.toLowerCase();

        String[] words = a.split("\\s+");

        int matched = 0;

        for (String word : words) {
            if (word.length() > 2 && b.contains(word)) {
                matched++;
            }
        }

        if (words.length == 0) return 0;

        return (matched * 100) / words.length;
    }

    public static boolean equalsIgnoreCase(String a, String b) {
        if (a == null || b == null) return false;
        return a.trim().equalsIgnoreCase(b.trim());
    }

    public static String confidenceLabel(int score) {
        if (score >= 80) return "HIGH";
        if (score >= 50) return "MEDIUM";
        return "LOW";
    }
}
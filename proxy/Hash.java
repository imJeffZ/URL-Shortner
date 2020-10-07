package proxy;

public class Hash {
    public static int getHash(String shortURL, int numShards) {
        int sum = 0;
        for (int i = 0; i < shortURL.length(); i++)
            sum += (int) shortURL.charAt(i);

        return sum % numShards;
    }
}

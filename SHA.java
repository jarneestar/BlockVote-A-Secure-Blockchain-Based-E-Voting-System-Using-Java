
import java.security.MessageDigest;

public class SHA{
    public static void main(String[] args) throws Exception {

        String input = "Hello Blockchain";

        MessageDigest digest = MessageDigest.getInstance("SHA-256");

        byte[] hash = digest.digest(input.getBytes("UTF-8"));

        StringBuilder hexString = new StringBuilder();

        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }

        System.out.println("SHA-256 Hash: " + hexString.toString());
    }
}
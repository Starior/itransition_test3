import java.util.HashSet;
import java.security.SecureRandom;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Random;
import java.util.Scanner;

class Test {
  public static void main(String[] args) {

    if (args.length < 3) {
      System.out.println(
          "The number of arguments must be three or more and be odd and not be repeated\n"
              + "For example: 1 2 3 4 5\n"
              + "For example: rock paper scissors lizard spock");
    } else if (args.length % 2 == 0) {
      System.out.println("The number of arguments must be odd");
    } else if (!check(args)) {
      System.out.println("Arguments must not be repeated");
    } else {
      SecureRandom random = new SecureRandom();
      byte[] secretKey = new byte[16];
      random.nextBytes(secretKey);

      Random randNumber = new Random();
      int randomIndex = randNumber.nextInt(args.length);
      String message = args[randomIndex];
      byte[] byteMessage = message.getBytes();

      System.out.println("HMAC:\n" + bytesToHex(calcHmac(secretKey, byteMessage)));

      while (true) {
        System.out.println("Available moves:");
        int index = 1;
        for (String arg : args) {
          System.out.println(index + " - " + arg);
          index++;
        }
        System.out.print("0 - Exit\nEnter your move: ");
        try {
          Scanner innerScanner = new Scanner(System.in);
          int variant = innerScanner.nextInt();

          if (variant == 0) {
            System.exit(0);
          } else if (variant > args.length) {
            System.out.println("This choice does not exist");
          } else {
            System.out.println("Your move: " + args[variant - 1]);
            innerScanner.close();
            System.out.println("Computer move: " + args[randomIndex]);
            if (variant - 1 == randomIndex) {
              System.out.println("Draw");
            } else if ((variant - 1 < randomIndex && randomIndex - (variant - 1) <= args.length / 2)
                || (variant - 1 > randomIndex && (variant - 1) - randomIndex > args.length / 2)) {
              System.out.println("Computer win");
            } else {
              System.out.println("You win!");
            }
            System.out.println("HMAC key: " + bytesToHex(secretKey));
            System.exit(0);
          }
        } catch (Exception e) {
          System.out.println("Enter an integer from 1 to " + args.length);
        }
      }
    }
  }

  private static boolean check(String[] args) {
    HashSet<String> h = new HashSet<>();
    for (String name : args) {
      if (!h.add(name)) {
        return false;
      }
    }
    return true;
  }

  private static byte[] calcHmac(byte[] secretKey, byte[] message) {
    byte[] hmacValue;
    try {
      Mac mac = Mac.getInstance("HmacSHA3-256");
      SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey, "HmacSHA3-256");
      mac.init(secretKeySpec);
      hmacValue = mac.doFinal(message);

    } catch (Exception e) {
      throw new RuntimeException("Failed to calculate HmacSHA3-256", e);
    }
    return hmacValue;
  }

  private static String bytesToHex(byte[] bytes) {
    StringBuilder sb = new StringBuilder(bytes.length * 2);
    for (byte b : bytes) {
      sb.append(String.format("%02X", b));
    }
    return sb.toString();
  }
}

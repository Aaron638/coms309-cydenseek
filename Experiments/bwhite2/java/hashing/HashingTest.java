import java.util.Scanner;

public class HashingTest {

    public static void main(String[] args) {
		System.out.print("Enter password to hash: ");
		Scanner s = new Scanner(System.in);
		String pwd = s.nextLine();
		s.close();
        System.out.println(hash(pwd));
    }

    public static String hash(String pwd) {
        String a = "";
        int b = 7;
        for(int i = 0; i < 256; i++) {
            b = (b * 31 + pwd.charAt(i % pwd.length())) % 10;
            a += b;
        }
        String hash = "";
        for(int i = 0; i < 256; i++) {
            b = (b * 31 + pwd.charAt(i % pwd.length())) % 10;
            hash += b;
        }
        return hash;
    }
}
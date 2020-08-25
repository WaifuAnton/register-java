import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class Main {
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        LFSR lfsr = new LFSR();
        lfsr.encrypt("register.txt", "plane.txt", "encrypted.txt");
        lfsr.decrypt("register.txt", "encrypted.txt", "decrypted.txt");
    }
}

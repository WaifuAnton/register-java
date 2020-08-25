import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class LFSR {
    private FileWriter registerOut;
    private BufferedReader registerIn;

    private FileWriter dataOut;
    private BufferedReader dataIn;

    private int[] generateRegister() throws NoSuchAlgorithmException {
        int[] register = new int[64];
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
        for (int i = 0; i < register.length; i++)
            register[i] = secureRandom.nextInt(2);
        return register;
    }

    private long generateGamma(int[] register) {
        StringBuilder g = new StringBuilder();
        int s = 0;
        for (int i = 0; i < 64; i++) {
            s = register[register.length - 1];
            s = ((( (s >> 15) ^ (s >> 4) ^ (s >> 2) ^ (s >> 1) ^ s ) & 1 ) << 15 ) | (s >> 1);
            for (int k = register.length - 1; k > 0; k--)
                register[k] = register[k - 1];
            register[0] = s;
            if (s > 0)
                g.append(1);
            else
                g.append(0);
        }
        return Long.parseUnsignedLong(g.toString(), 2);
    }

    private void overlayGamma(long gamma) throws IOException {
        String line;
        while ((line = dataIn.readLine()) != null) {
            for (int i = 0; i < line.length(); i++)
                dataOut.write((char) (line.charAt(i) ^ gamma));
            dataOut.write("\n");
        }
        dataIn.close();
        dataOut.close();
    }

    public void encrypt(String keyPath, String inputPath, String outputPath) throws NoSuchAlgorithmException, IOException {
        int[] key = generateRegister();
        registerOut = new FileWriter(keyPath);
        dataIn = new BufferedReader(new FileReader(inputPath));
        dataOut = new FileWriter(outputPath);
        for (int i = 0; i < key.length; i++)
            registerOut.write(Integer.toString(key[i]));
        registerOut.close();
        overlayGamma(generateGamma(key));
        dataIn.close();
        dataOut.close();
    }

    public void decrypt(String keyPath, String inputPath, String outputPath) throws NoSuchAlgorithmException, IOException {
        int[] register = generateRegister();
        registerIn = new BufferedReader(new FileReader(keyPath));
        dataIn = new BufferedReader(new FileReader(inputPath));
        dataOut = new FileWriter(outputPath);
        String k;
        if ((k = registerIn.readLine()) != null) {
            for (int i = 0; i < k.length(); i++)
                register[i] = Character.digit(k.charAt(i), 10);
            overlayGamma(generateGamma(register));
        }
        registerIn.close();
        dataOut.close();
        registerIn.close();
    }
}

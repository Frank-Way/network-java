package tests.utils;

import java.util.Random;

public class RandomValuesProvider extends ValuesProvider {
    protected static final String alphabet = "qwertyuioplkjhgfdsazxcvbnmQWERTYUIOPLKJHGFDSAZXCVBNM";
    protected final Random random;

    public RandomValuesProvider() {
        this.random = new Random();
    }

    public int getInteger() { return random.nextInt(); }

    public double getDouble() { return random.nextDouble(); }

    public boolean getBoolean() { return random.nextBoolean(); }

    public char getCharacter() { return alphabet.charAt(random.nextInt(alphabet.length())); }

    public String getString() { return getString(5 + random.nextInt(5));}

    public String getString(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++)
            sb.append(getCharacter());
        return sb.toString();
    }
}

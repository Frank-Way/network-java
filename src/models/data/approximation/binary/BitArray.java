package models.data.approximation.binary;

import java.util.Arrays;

public class BitArray {
    private final boolean[] values;

    public BitArray(boolean[] values) {
        this.values = values;
    }

    public BitArray(int size) {
        this(new boolean[size]);
    }

    public int length() {
        return values.length;
    }

    public void set(int index) {
        set(index, true);
    }

    public void set(int indexFrom, int indexTo) {
        set(indexFrom, indexTo, true);
    }

    public void set(int index, boolean value) {
        values[index] = value;
    }

    public void set(int indexFrom, int indexTo, boolean value) {
        for (int index = indexFrom; index < indexTo; index++)
            set(index, value);
    }

    public boolean get(int index) {
        return values[index];
    }

    public BitArray get(int indexFrom, int indexTo) {
        final int size = indexTo - indexFrom;
        final BitArray result = new BitArray(size);
        for (int i = 0; i < size; i++)
            result.set(i, this.get(indexFrom + i));
        return result;
    }

    @Override
    public String toString() {
        return "BitArray{" +
                "size=" + values.length +
                ", values=" + valuesToString() +
                '}';
    }

    private String valuesToString() {
        StringBuilder sb = new StringBuilder();
        for (boolean value: values)
            sb.append(value ? '1' : '0');
        return sb.toString();
    }
}

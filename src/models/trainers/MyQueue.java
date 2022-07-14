package models.trainers;

public class MyQueue {
    private int actualSize;
    protected int size;
    protected double[] values;
    protected int lastPushed;

    public MyQueue(int size) {
        this.size = size;
        values = new double[size];
        for (int i = 0; i < size; i++)
            values[i] = Double.MAX_VALUE;
        lastPushed = 0;
        actualSize = 0;
    }

    public int getSize() {
        return size;
    }

    public int getActualSize() {
        return actualSize;
    }

    public void push(double value) {
        if (actualSize < size)
            actualSize++;
        lastPushed = (lastPushed + 1) % getSize();
        values[lastPushed] = value;
    }

    public double mean() {
        double sum = 0.0;
        for (double value: values)
            sum += value;
        return sum / getActualSize();
    }
}

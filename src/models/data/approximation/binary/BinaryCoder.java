package models.data.approximation.binary;

import models.data.Data;
import models.data.Dataset;
import models.layers.Layer;
import models.math.Matrix;
import models.networks.Network;
import models.operations.Operation;
import models.operations.ParametrizedOperation;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

public class BinaryCoder {
    public static final int DOUBLE_SIZE = 64;

    private Map<Integer, Integer> getBitsToMantisMap() {
        final Map<Integer, Integer> bitsToMantisMap = new HashMap<>();
        bitsToMantisMap.put(16, 10);
        bitsToMantisMap.put(32, 23);
        bitsToMantisMap.put(64, 52);
        return bitsToMantisMap;
    }

    private Map<Integer, Integer> getBitsToExponentMap() {
        final Map<Integer, Integer> bitsToExponentMap = new HashMap<>();
        bitsToExponentMap.put(16, 5);
        bitsToExponentMap.put(32, 8);
        bitsToExponentMap.put(64, 11);
        return bitsToExponentMap;
    }

    public static int reduceSize(int reducingFactor) {
        if (!(reducingFactor == 1 || reducingFactor == 2 || reducingFactor == 4))
            throw new IllegalArgumentException("Не корректное значение параметра reducingFactor: " + reducingFactor);
        return DOUBLE_SIZE / reducingFactor;
    }

    public Dataset code(Dataset dataset, int reducingFactor) {
        return new Dataset(
                code(dataset.getTrainData(), reducingFactor),
                code(dataset.getTestData(), reducingFactor),
                code(dataset.getValidData(), reducingFactor));
    }

    public Network code(Network network, int reducingFactor) {
        return network.builder()
                .layers(IntStream.range(0, network.layersCount())
                        .mapToObj(network::getLayer)
                        .map(layer -> {
                            Layer copy = layer.deepCopy();
                            return Layer.createLayer(copy.getClass(), null, null, copy.getNeurons(),
                                    IntStream.range(0, copy.getOperationsCount())
                                            .mapToObj(copy::getOperation)
                                            .map(operationInCopy -> {
                                                if (operationInCopy instanceof ParametrizedOperation) {
                                                    ParametrizedOperation parametrizedOperationInCopy = (ParametrizedOperation) operationInCopy;
                                                    parametrizedOperationInCopy.setParameter(code(parametrizedOperationInCopy.getParameter(), reducingFactor));
                                                    return parametrizedOperationInCopy;
                                                }
                                                return operationInCopy;
                                            })
                                            .toArray(Operation[]::new));
                        })
                        .toArray(Layer[]::new))
                .build();
    }

    public Data code(Data data, int reducingFactor) {
        return new Data(
                code(data.getInputs(), reducingFactor),
                code(data.getOutputs(), reducingFactor));
    }

    public BinaryMatrix codeMatrix(Matrix matrix, int reducingFactor) {
        return new BinaryMatrix(matrix, reducingFactor);
    }
    
    public Matrix code(Matrix matrix, int reducingFactor) {
        return codeMatrix(matrix, reducingFactor).toMatrix();
    }
    
    public BitArray code(double value, int reducingFactor) {
        final int size = DOUBLE_SIZE;
        final int newSize = reduceSize(reducingFactor);

        final Map<Integer, Integer> bitsToExponentMap = getBitsToExponentMap();
        final Map<Integer, Integer> bitsToMantisMap = getBitsToMantisMap();

        final BitArray valueAsBitArray = toBitArray(value);
        final boolean sign = valueAsBitArray.get(0);

        final BitArray rawExponent = valueAsBitArray.get(1, 1 + bitsToExponentMap.get(size));
        final BitArray rawMantis = valueAsBitArray.get(1 + bitsToExponentMap.get(size), valueAsBitArray.length());

        final BitArray exponent = truncateExponent(rawExponent, bitsToExponentMap.get(newSize));
        final BitArray mantis = truncateMantis(rawMantis, bitsToMantisMap.get(newSize));

        final BitArray result = new BitArray(newSize);

        result.set(0, sign);

        for (int i = 0; i < exponent.length(); i++)
            result.set(1 + i, exponent.get(i));

        for (int i = 0; i < mantis.length(); i++)
            result.set(1 + exponent.length() + i, mantis.get(i));

        return result;
    }

    private BitArray truncateMantis(BitArray value, int newSize) {
        return value.get(0, newSize);
    }

    private BitArray truncateExponent(BitArray value, int newSize) {
        if (value.length() == newSize)
            return value;

        final long biasedExponentValue = decodeLong(value);
        final long bias = calculateBias(value.length());

        final long exponentValue = biasedExponentValue - bias;

        final long newBias = calculateBias(newSize);
        final long newBiasedExponentValue = exponentValue + newBias;

        return toBitArray(newBiasedExponentValue).get(DOUBLE_SIZE - newSize, DOUBLE_SIZE);
    }

    private BitArray toBitArray(final double d) {
        final BitArray result = new BitArray(DOUBLE_SIZE);
        final long doubleBits = Double.doubleToLongBits(d);
        long mask = 1L;
        for(int i = 0; i < DOUBLE_SIZE; i++) {
            final long bitValue = doubleBits & mask;
            result.set(DOUBLE_SIZE - i - 1, bitValue != 0);
            mask <<= 1;
        }
        return result;
    }

    private BitArray toBitArray(final long l) {
        final String binaryString = Long.toBinaryString(l);
        final BitArray result = new BitArray(DOUBLE_SIZE);
        final int offset = DOUBLE_SIZE - binaryString.length();
        for (int i = 0; i < binaryString.length(); i++)
            result.set(offset + i, binaryString.charAt(i) == '1');
        return result;
    }

    public long decodeLong(BitArray BitArray) {
        long result = 0L;
        for (int i = 0; i < BitArray.length(); i++)
            result += (BitArray.get(i) ? 1 : 0) * Math.pow(2.0, BitArray.length() - i - 1);
        return result;
    }

    public double decodeDouble(BitArray BitArray) {
        final int size = BitArray.length();

        final Map<Integer, Integer> bitsToExponentMap = getBitsToExponentMap();
        final Map<Integer, Integer> bitsToMantisMap = getBitsToMantisMap();

        final double sign = BitArray.get(0) ? -1.0 : 1.0;
        final long bias = calculateBias(bitsToExponentMap.get(size));

        long exponent = decodeLong(BitArray.get(1, 1 + bitsToExponentMap.get(size)));
        exponent -= bias;

        final long mantis = decodeLong(BitArray.get(1 + bitsToExponentMap.get(size), BitArray.length()));

        final double tmp1 = Math.pow(2.0, exponent);
        final double tmp2 = 1.0 + mantis * 1.0 / Math.pow(2.0, bitsToMantisMap.get(size));
        return sign * tmp1 * tmp2;
    }

    public Matrix decode(BinaryMatrix binaryMatrix) {
        return binaryMatrix.toMatrix();
    }

    private long calculateBias(int bits) {
        return (long) (Math.pow(2.0, bits - 1) - 1);
    }

}

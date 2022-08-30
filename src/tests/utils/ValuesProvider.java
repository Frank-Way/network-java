package tests.utils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class ValuesProvider {
    /**
     * базовые элементы
     */
    
    public Integer getIntegerBoxed() { return getInteger(); }
    public abstract int getInteger();

    public Double getDoubleBoxed() { return getDouble(); }
    public abstract double getDouble();

    public Boolean getBooleanBoxed() { return getBoolean(); }
    public abstract boolean getBoolean();

    public Character getCharacterBoxed() { return getCharacter(); }
    public abstract char getCharacter();
    
    public String getString() { return getString(getInteger()); }

    public String getString(int length) {
        return String.valueOf(getArrayCharacters(length));
    }
    
    
    /**
     * массивы
     */
    
    public int[] getArrayIntegers(int size) { return IntStream.range(0, size).parallel().map(i -> getInteger()).toArray(); }
    public Integer[] getArrayIntegersBoxed(int size) { return Arrays.stream(getArrayIntegers(size)).parallel().boxed().toArray(Integer[]::new); }
    
    public double[] getArrayDoubles(int size) { return IntStream.range(0, size).parallel().mapToDouble(i -> getDouble()).toArray(); }
    public Double[] getArrayDoublesBoxed(int size) { return Arrays.stream(getArrayDoubles(size)).parallel().boxed().toArray(Double[]::new); }
    
    public boolean[] getArrayBooleans(int size) { 
        boolean[] result = new boolean[size];
        IntStream.range(0, size).parallel().forEach(i -> result[i] = getBoolean());
        return result; 
    }
    public Boolean[] getArrayBooleansBoxed(int size) {
        final boolean[] unboxed = getArrayBooleans(size); 
        return IntStream.range(0, size).parallel().mapToObj(i -> unboxed[i]).toArray(Boolean[]::new);
    }
    
    public char[] getArrayCharacters(int size) {
        char[] result = new char[size];
        IntStream.range(0, size).parallel().forEach(i -> result[i] = getCharacter());
        return result;
    }
    public Character[] getArrayCharactersBoxed(int size) {
        final char[] unboxed = getArrayCharacters(size); 
        return IntStream.range(0, size).parallel().mapToObj(i -> unboxed[i]).toArray(Character[]::new);
    }
    
    public String[] getArrayStrings(int size) {
        return IntStream.range(0, size).parallel().mapToObj(i -> getString()).toArray(String[]::new);
    }

    
    /**
     * вложенные массивы
      */
    
    public int[][] getNestedArrayIntegers(int size1, int size2) {
        int[][] result = new int[size1][];
        for (int i = 0; i < size1; i++)
            result[i] = getArrayIntegers(size2);
        return result;
    }
    public Integer[][] getNestedArrayIntegersBoxed(int size1, int size2) {
        Integer[][] result = new Integer[size1][];
        for (int i = 0; i < size1; i++)
            result[i] = getArrayIntegersBoxed(size2);
        return result;
    }
    
    public double[][] getNestedArrayDoubles(int size1, int size2) {
        double[][] result = new double[size1][];
        for (int i = 0; i < size1; i++)
            result[i] = getArrayDoubles(size2);
        return result;
    }
    public Double[][] getNestedArrayDoublesBoxed(int size1, int size2) {
        Double[][] result = new Double[size1][];
        for (int i = 0; i < size1; i++)
            result[i] = getArrayDoublesBoxed(size2);
        return result;
    }
    
    public boolean[][] getNestedArrayBooleans(int size1, int size2) {
        boolean[][] result = new boolean[size1][];
        for (int i = 0; i < size1; i++)
            result[i] = getArrayBooleans(size2);
        return result;
    }
    public Boolean[][] getNestedArrayBooleansBoxed(int size1, int size2) {
        Boolean[][] result = new Boolean[size1][];
        for (int i = 0; i < size1; i++)
            result[i] = getArrayBooleansBoxed(size2);
        return result;
    }
    
    public char[][] getNestedArrayCharacters(int size1, int size2) {
        char[][] result = new char[size1][];
        for (int i = 0; i < size1; i++)
            result[i] = getArrayCharacters(size2);
        return result;
    }
    public Character[][] getNestedArrayCharactersBoxed(int size1, int size2) {
        Character[][] result = new Character[size1][];
        for (int i = 0; i < size1; i++)
            result[i] = getArrayCharactersBoxed(size2);
        return result;
    }
    
    public String[][] getNestedArrayStrings(int size1, int size2) {
        String[][] result = new String[size1][];
        for (int i = 0; i < size1; i++)
            result[i] = getArrayStrings(size2);
        return result;
    }


    /**
     * Множества
     */
    
    public Set<Integer> getSetIntegersBoxed(int size) {
        return Arrays.stream(getArrayIntegersBoxed(size)).collect(Collectors.toSet());
    }
    public Set<Double> getSetDoublesBoxed(int size) {
        return Arrays.stream(getArrayDoublesBoxed(size)).collect(Collectors.toSet());
    }
    public Set<Boolean> getSetBooleansBoxed(int size) {
        return Arrays.stream(getArrayBooleansBoxed(size)).collect(Collectors.toSet());
    }
    public Set<Character> getSetCharactersBoxed(int size) {
        return Arrays.stream(getArrayCharactersBoxed(size)).collect(Collectors.toSet());
    }
    public Set<String> getSetStrings(int size) {
        return Arrays.stream(getArrayStrings(size)).collect(Collectors.toSet());
    }

    /**
     * Вложенные множества
     */
    
    public Set<Set<Integer>> getNestedSetIntegersBoxed(int size1, int size2) {
        return IntStream.range(0, size1).mapToObj(i -> getSetIntegersBoxed(size2)).collect(Collectors.toSet());
    }
    public Set<Set<Double>> getNestedSetDoublesBoxed(int size1, int size2) {
        return IntStream.range(0, size1).mapToObj(i -> getSetDoublesBoxed(size2)).collect(Collectors.toSet());
    }
    public Set<Set<Boolean>> getNestedSetBooleansBoxed(int size1, int size2) {
        return IntStream.range(0, size1).mapToObj(i -> getSetBooleansBoxed(size2)).collect(Collectors.toSet());
    }
    public Set<Set<Character>> getNestedSetCharactersBoxed(int size1, int size2) {
        return IntStream.range(0, size1).mapToObj(i -> getSetCharactersBoxed(size2)).collect(Collectors.toSet());
    }
    public Set<Set<String>> getNestedSetStrings(int size1, int size2) {
        return IntStream.range(0, size1).mapToObj(i -> getSetStrings(size2)).collect(Collectors.toSet());
    }


    /**
     * Списки
     */
    
    public List<Integer> getListIntegersBoxed(int size) {
        return Arrays.stream(getArrayIntegersBoxed(size)).collect(Collectors.toList());
    }
    public List<Double> getListDoublesBoxed(int size) {
        return Arrays.stream(getArrayDoublesBoxed(size)).collect(Collectors.toList());
    }
    public List<Boolean> getListBooleansBoxed(int size) {
        return Arrays.stream(getArrayBooleansBoxed(size)).collect(Collectors.toList());
    }
    public List<Character> getListCharactersBoxed(int size) {
        return Arrays.stream(getArrayCharactersBoxed(size)).collect(Collectors.toList());
    }
    public List<String> getListStrings(int size) {
        return Arrays.stream(getArrayStrings(size)).collect(Collectors.toList());
    }

    /**
     * Вложенные списки
     */
    
    public List<List<Integer>> getNestedListIntegersBoxed(int size1, int size2) {
        return IntStream.range(0, size1).mapToObj(i -> getListIntegersBoxed(size2)).collect(Collectors.toList());
    }
    public List<List<Double>> getNestedListDoublesBoxed(int size1, int size2) {
        return IntStream.range(0, size1).mapToObj(i -> getListDoublesBoxed(size2)).collect(Collectors.toList());
    }
    public List<List<Boolean>> getNestedListBooleansBoxed(int size1, int size2) {
        return IntStream.range(0, size1).mapToObj(i -> getListBooleansBoxed(size2)).collect(Collectors.toList());
    }
    public List<List<Character>> getNestedListCharactersBoxed(int size1, int size2) {
        return IntStream.range(0, size1).mapToObj(i -> getListCharactersBoxed(size2)).collect(Collectors.toList());
    }
    public List<List<String>> getNestedListStrings(int size1, int size2) {
        return IntStream.range(0, size1).mapToObj(i -> getListStrings(size2)).collect(Collectors.toList());
    }


    /**
     * Ассоциативные массивы
     */

    public <K, V> Map<K, V> getMap(K[] keys, V[] values) {
        HashMap<K, V> result = new HashMap<>();
        for (int i = 0; i < keys.length; i++)
            result.put(keys[i], values[i]);
        return result;
    }

    @Override
    public String toString() {
        return getClass().getCanonicalName() + "{}";
    }
}

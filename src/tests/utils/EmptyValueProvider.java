package tests.utils;

import java.util.*;

public class EmptyValueProvider extends ValuesProvider{
    @Override
    public int getInteger() {
        return 0;
    }

    @Override
    public double getDouble() {
        return 0.0;
    }

    @Override
    public boolean getBoolean() {
        return false;
    }

    @Override
    public char getCharacter() {
        return 0;
    }

    @Override
    public String getString() {
        return getString(0);
    }

    @Override
    public String getString(int length) {
        return "";
    }

    @Override
    public int[] getArrayIntegers(int size) {
        return new int[size];
    }

    @Override
    public Integer[] getArrayIntegersBoxed(int size) {
        return new Integer[size];
    }

    @Override
    public double[] getArrayDoubles(int size) {
        return new double[size];
    }

    @Override
    public Double[] getArrayDoublesBoxed(int size) {
        return new Double[size];
    }

    @Override
    public boolean[] getArrayBooleans(int size) {
        return new boolean[size];
    }

    @Override
    public Boolean[] getArrayBooleansBoxed(int size) {
        return new Boolean[size];
    }

    @Override
    public char[] getArrayCharacters(int size) {
        return new char[size];
    }

    @Override
    public Character[] getArrayCharactersBoxed(int size) {
        return new Character[size];
    }

    @Override
    public String[] getArrayStrings(int size) {
        return new String[size];
    }

    @Override
    public int[][] getNestedArrayIntegers(int size1, int size2) {
        return new int[size1][];
    }

    @Override
    public Integer[][] getNestedArrayIntegersBoxed(int size1, int size2) {
        return new Integer[size1][];
    }

    @Override
    public double[][] getNestedArrayDoubles(int size1, int size2) {
        return new double[size1][];
    }

    @Override
    public Double[][] getNestedArrayDoublesBoxed(int size1, int size2) {
        return new Double[size1][];
    }

    @Override
    public boolean[][] getNestedArrayBooleans(int size1, int size2) {
        return new boolean[size1][];
    }

    @Override
    public Boolean[][] getNestedArrayBooleansBoxed(int size1, int size2) {
        return new Boolean[size1][];
    }

    @Override
    public char[][] getNestedArrayCharacters(int size1, int size2) {
        return new char[size1][];
    }

    @Override
    public Character[][] getNestedArrayCharactersBoxed(int size1, int size2) {
        return new Character[size1][];
    }

    @Override
    public String[][] getNestedArrayStrings(int size1, int size2) {
        return new String[size1][];
    }

    @Override
    public Set<Integer> getSetIntegersBoxed(int size) {
        return new HashSet<>();
    }

    @Override
    public Set<Double> getSetDoublesBoxed(int size) {
        return new HashSet<>();
    }

    @Override
    public Set<Character> getSetCharactersBoxed(int size) {
        return new HashSet<>();
    }

    @Override
    public Set<String> getSetStrings(int size) {
        return new HashSet<>();
    }

    @Override
    public Set<Set<Integer>> getNestedSetIntegersBoxed(int size1, int size2) {
        return new HashSet<>();
    }

    @Override
    public Set<Set<Double>> getNestedSetDoublesBoxed(int size1, int size2) {
        return new HashSet<>();
    }

    @Override
    public Set<Set<Character>> getNestedSetCharactersBoxed(int size1, int size2) {
        return new HashSet<>();
    }

    @Override
    public Set<Set<String>> getNestedSetStrings(int size1, int size2) {
        return new HashSet<>();
    }

    @Override
    public List<Integer> getListIntegersBoxed(int size) {
        return new ArrayList<>();
    }

    @Override
    public List<Double> getListDoublesBoxed(int size) {
        return new ArrayList<>();
    }

    @Override
    public List<Character> getListCharactersBoxed(int size) {
        return new ArrayList<>();
    }

    @Override
    public List<String> getListStrings(int size) {
        return new ArrayList<>();
    }

    @Override
    public List<List<Integer>> getNestedListIntegersBoxed(int size1, int size2) {
        return new ArrayList<>();
    }

    @Override
    public List<List<Double>> getNestedListDoublesBoxed(int size1, int size2) {
        return new ArrayList<>();
    }

    @Override
    public List<List<Character>> getNestedListCharactersBoxed(int size1, int size2) {
        return new ArrayList<>();
    }

    @Override
    public List<List<String>> getNestedListStrings(int size1, int size2) {
        return new ArrayList<>();
    }

    @Override
    public List<Boolean> getListBooleansBoxed(int size) {
        return new ArrayList<>();
    }

    @Override
    public List<List<Boolean>> getNestedListBooleansBoxed(int size1, int size2) {
        return new ArrayList<>();
    }

    @Override
    public <K, V> Map<K, V> getMap(K[] keys, V[] values) {
        return new HashMap<>();
    }
}

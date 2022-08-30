//package serialization.wrappers.simple;
//
//import serialization.formatters.Formatter;
//import serialization.wrappers.Wrapper;
//
//public class NullWrapper extends Wrapper {
//    public NullWrapper(Class<?> clazz, Formatter formatter) {
//        super(clazz, formatter);
//    }
//
//    @Override
//    protected Object readValueInner(String fieldName, String yaml) {
//        return formatter.readNull(yaml);
//    }
//
//    @Override
//    public String writeValueInner(String fieldName, Object value) {
//        return formatter.write();
//    }
//
//    @Override
//    protected String getMsgIfCanNotBeWrapped() {
//        return "!null";
//    }
//
//    public static boolean isNull(Class<?> clazz) {
//        return new NullWrapper(clazz, null).canBeWrapped();
//    }
//
//    @Override
//    protected Class<?>[] getWrappedClasses() {
//        return new Class[0];
//    }
//}

package smn.learn.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ObjectHelper {
	/**
     * Gets an array of all fields in a class hierarchy walking up to parent classes
     * @param objectClass the class
     * @return the fields array
     */
    public static Field[] getAllFieldsInHierarchy(Class<?> objectClass) {
        Set<Field> allFields = new HashSet<Field>();
        Field[] declaredFields = objectClass.getDeclaredFields();
        Field[] fields = objectClass.getFields();
        if (objectClass.getSuperclass() != null) {
            Class<?> superClass = objectClass.getSuperclass();
            Field[] superClassFields = getAllFieldsInHierarchy(superClass);
            allFields.addAll(Arrays.asList(superClassFields));
        }
        allFields.addAll(Arrays.asList(declaredFields));
        allFields.addAll(Arrays.asList(fields));
        return allFields.toArray(new Field[allFields.size()]);
    }
    
    /**
     * Gets an array of all methods in a class hierarchy walking up to parent classes
     * @param objectClass the class
     * @return the methods array
     */
    public static Method[] getAllMethodsInHierarchy(Class<?> objectClass) {
        Set<Method> allMethods = new HashSet<Method>();
        Method[] declaredMethods = objectClass.getDeclaredMethods();
        Method[] methods = objectClass.getMethods();
        if (objectClass.getSuperclass() != null) {
            Class<?> superClass = objectClass.getSuperclass();
            Method[] superClassMethods = getAllMethodsInHierarchy(superClass);
            allMethods.addAll(Arrays.asList(superClassMethods));
        }
        allMethods.addAll(Arrays.asList(declaredMethods));
        allMethods.addAll(Arrays.asList(methods));
        return allMethods.toArray(new Method[allMethods.size()]);
    }
    
    /**
     * Validates that all values are set.
     *
     * @param values a varargs array of arguments
     */
    public static void assertNotNull(Object... values) {
        if (values == null) {
            throw new IllegalArgumentException("values is null");
        }
        for (int i = 0; i < values.length; i++) {
            Object value = values[i];
            if (value == null) {
                throw new IllegalArgumentException(String.format("values[%d] is null", i));
            }
        }
    }
    
    /**
     * Prevents from instantiation
     */
    private ObjectHelper() {
    }
}

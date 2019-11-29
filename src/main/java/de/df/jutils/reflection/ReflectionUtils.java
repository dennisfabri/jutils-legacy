package de.df.jutils.reflection;

import java.lang.reflect.InvocationTargetException;

public final class ReflectionUtils {

    private static final Class<?>[] NO_PARAMETER_TYPES = new Class<?>[0];
    private static final Object[]   NO_ARGUMENTS       = new Object[0];

    private ReflectionUtils() {
        // Never used
    }

    public static boolean check(final String klasse) {
        try {
            Class.forName(klasse);
            return true;
        } catch (ClassNotFoundException classnotfoundexception) {
            return false;
        } catch (NoClassDefFoundError noclassdeffounderror) {
            return false;
        }
    }

    public static Object getObject(final String klasse) {
        try {
            return executeConstructor(klasse);
        } catch (Exception e) {
            return null;
        }
    }

    public static Object executeConstructor(final String klasse)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return Class.forName(klasse).getConstructor(NO_PARAMETER_TYPES).newInstance(NO_ARGUMENTS);
    }

    public static Object executeConstructor(final Class<?> klasse)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return klasse.getConstructor(NO_PARAMETER_TYPES).newInstance(NO_ARGUMENTS);
    }

    public static Object executeMethod(final String klasse, final String method)
            throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return Class.forName(klasse).getMethod(method, NO_PARAMETER_TYPES).invoke(null, NO_ARGUMENTS);
    }

    public static void executeMain(final ClassLoader loader, final String klass)
            throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        loader.loadClass(klass).getMethod("main", NO_PARAMETER_TYPES).invoke(klass, NO_ARGUMENTS);
    }

    public static void executeMain(final String klasse)
            throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Class.forName(klasse).getMethod("main", NO_PARAMETER_TYPES).invoke(klasse, NO_ARGUMENTS);
    }

    public static Object executeConstructor(final ClassLoader loader, final String klasse)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return loader.loadClass(klasse).getConstructor(NO_PARAMETER_TYPES).newInstance(NO_ARGUMENTS);
    }

    public static Object executeMethod(final ClassLoader loader, final String klasse, final String method)
            throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return loader.loadClass(klasse).getMethod(method, NO_PARAMETER_TYPES).invoke(klasse, NO_ARGUMENTS);
    }
}
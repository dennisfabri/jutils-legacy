/*
 * Created on 13.01.2004
 */
package de.df.jutils.util;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.InvocationTargetException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.df.jutils.reflection.ReflectionUtils;

/**
 * @author Dennis
 */
public class SimpleReflectionTest {

    private Object o;

    @BeforeEach
    public void setUp() {
        o = null;
    }

    @Test
    public void testGetObject() {
        o = ReflectionUtils.getObject("java.lang.String");
        if (!(o instanceof String)) {
            fail();
        }
        o = ReflectionUtils.getObject("java.lang.String2");
        if (o != null) {
            fail();
        }
    }

    @Test
    public void testExecuteConstructor()
            throws SecurityException, IllegalArgumentException, ClassNotFoundException, NoSuchMethodException,
            InstantiationException, IllegalAccessException, InvocationTargetException {
        o = ReflectionUtils.executeConstructor("java.lang.String");
        if (!(o instanceof String)) {
            fail();
        }
        try {
            o = ReflectionUtils.executeConstructor("java.lang.String2");
            fail();
        } catch (SecurityException | IllegalArgumentException | ClassNotFoundException | NoSuchMethodException
                | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            // ok
        }
    }

    @Test
    public void testExecuteMethod()
            throws IllegalArgumentException, SecurityException, ClassNotFoundException, IllegalAccessException,
            InvocationTargetException, NoSuchMethodException {
        o = ReflectionUtils.executeMethod("java.lang.System", "currentTimeMillis");
        assertNotNull(o);

        try {
            o = ReflectionUtils.executeMethod("java.lang.System", "currentTimeMillis2");
            fail();
        } catch (SecurityException | IllegalArgumentException | ClassNotFoundException | NoSuchMethodException
                | IllegalAccessException | InvocationTargetException e) {
            // ok
        }
    }

}

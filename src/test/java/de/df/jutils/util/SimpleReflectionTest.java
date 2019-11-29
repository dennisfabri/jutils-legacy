/*
 * Created on 13.01.2004
 */
package de.df.jutils.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.InvocationTargetException;

import org.junit.jupiter.api.*;

import de.df.jutils.reflection.ReflectionUtils;

/**
 * @author Dennis
 */
public class SimpleReflectionTest {

    private Object  o = null;
    private boolean b = false;


    @BeforeEach
    public void setUp() {
        o = null;
        b = false;
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
    public void testExecuteConstructor() throws SecurityException, IllegalArgumentException, ClassNotFoundException, NoSuchMethodException,
            InstantiationException, IllegalAccessException, InvocationTargetException {
        o = ReflectionUtils.executeConstructor("java.lang.String");
        if (!(o instanceof String)) {
            fail();
        }
        try {
            o = ReflectionUtils.executeConstructor("java.lang.String2");
            fail();
        } catch (SecurityException e) {
            // ok
        } catch (IllegalArgumentException e) {
            // ok
        } catch (ClassNotFoundException e) {
            // ok
        } catch (NoSuchMethodException e) {
            // ok
        } catch (InstantiationException e) {
            // ok
        } catch (IllegalAccessException e) {
            // ok
        } catch (InvocationTargetException e) {
            // ok
        }
    }

    @Test
    public void testCheck() {
        b = ReflectionUtils.check("java.lang.String");
        assertTrue(b);

        b = ReflectionUtils.check("java.lang.String2");
        assertFalse(b);
    }

    @Test
    public void testExecuteMethod() throws IllegalArgumentException, SecurityException, ClassNotFoundException, IllegalAccessException,
            InvocationTargetException, NoSuchMethodException {
        o = ReflectionUtils.executeMethod("java.lang.System", "currentTimeMillis");
        assertNotNull(o);

        try {
            o = ReflectionUtils.executeMethod("java.lang.System", "currentTimeMillis2");
            fail();
        } catch (SecurityException e) {
            // ok
        } catch (IllegalArgumentException e) {
            // ok
        } catch (ClassNotFoundException e) {
            // ok
        } catch (NoSuchMethodException e) {
            // ok
        } catch (IllegalAccessException e) {
            // ok
        } catch (InvocationTargetException e) {
            // ok
        }
    }

}

package de.df.jutils.reflection;

import java.net.*;

public final class ExtendedURLClassLoader extends URLClassLoader {

    public ExtendedURLClassLoader(final URL[] url) {
        super(url);
    }

    public ExtendedURLClassLoader(final URL[] url, final ClassLoader cl) {
        super(url, cl);
    }

    public ExtendedURLClassLoader(final URL[] url, final ClassLoader cl, final URLStreamHandlerFactory ushf) {
        super(url, cl, ushf);
    }

    @Override
    public Class<?> loadClass(final String name) throws ClassNotFoundException {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException cnfe) {
            return super.loadClass(name);
        }
    }
}
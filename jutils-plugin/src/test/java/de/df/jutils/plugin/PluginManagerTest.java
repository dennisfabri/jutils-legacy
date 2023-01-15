package de.df.jutils.plugin;

import org.junit.jupiter.api.Test;

import de.df.jutils.util.TimeMeasurement;

class PluginManagerTest {

    @Test
    @SuppressWarnings("unused")
    void test() {
        PluginManager pm = new PluginManager("test", null, new TimeMeasurement(System.out, 10), null, false, false,
                "src/test/resources/plugins1");
    }
}

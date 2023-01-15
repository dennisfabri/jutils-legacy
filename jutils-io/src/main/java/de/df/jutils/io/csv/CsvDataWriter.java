/*
 * Created on 05.02.2006
 */
package de.df.jutils.io.csv;

public interface CsvDataWriter {

    /**
     * Checks if the parameter o can be convertet into its String-representation by
     * this converter.
     * 
     * @param o
     * @return
     */
    boolean canConvert(Object o);

    /**
     * Converts the parameter o to its String-representation
     * 
     * @param o
     * @return
     */
    String convert(Object o, String format);
}

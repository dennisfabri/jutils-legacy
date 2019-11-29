/*
 * Created on 14.04.2005
 */
package de.df.jutils.i18n;

import javax.swing.Icon;

public interface UIElementsProvider {

    String getString(String id);

    Icon getIcon(String id);
}

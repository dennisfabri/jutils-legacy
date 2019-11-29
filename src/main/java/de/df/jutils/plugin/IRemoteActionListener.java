/*
 * Created on 29.04.2005
 */
package de.df.jutils.plugin;

public interface IRemoteActionListener {

    /**
     * Signals the appearance of a remote action.
     * 
     * @param ra
     * @return true if consumed
     */
    boolean remoteAction(RemoteAction ra);

    /**
     * Signals that a remote action has been consumed.
     * 
     * @param ra
     */
    void consumedRemoteAction(RemoteAction ra);
}

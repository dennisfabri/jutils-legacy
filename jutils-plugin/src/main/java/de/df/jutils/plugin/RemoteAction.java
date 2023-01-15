/*
 * Created on 29.04.2005
 */
package de.df.jutils.plugin;

public class RemoteAction {

    private final String verb;
    private final String command;

    private boolean consumed;

    public RemoteAction(String verb, String command) {
        this.verb = verb;
        this.command = command;
    }

    public String getCommand() {
        return command;
    }

    public String getVerb() {
        return verb;
    }

    public void consume() {
        consumed = true;
    }

    public boolean isConsumed() {
        return consumed;
    }
}

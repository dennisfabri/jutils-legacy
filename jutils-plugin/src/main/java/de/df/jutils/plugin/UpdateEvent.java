/*
 * Created on 26.03.2004
 */
package de.df.jutils.plugin;

/**
 * @author Dennis Fabri
 * @date 26.03.2004
 */
public class UpdateEvent {

    private final long reason;
    private final String title;
    private final Object data;
    private final IFeature source;
    private final Object addition;

    private static ReasonChecker checker;

    public UpdateEvent(String title, long updatereason, IFeature source) {
        this(title, updatereason, null, null, source);
    }

    public UpdateEvent(String title, long updatereason, Object data, Object addition, IFeature source) {
        if (checker != null) {
            reason = checker.check(updatereason);
        } else {
            reason = updatereason;
        }
        this.addition = addition;
        this.title = title;
        this.data = data;
        this.source = source;
    }

    public boolean isSource(IFeature f) {
        return source == f;
    }

    public long getChangeReason() {
        return reason;
    }

    public String getTitle() {
        return title;
    }

    IFeature getSource() {
        return source;
    }

    public Object getData() {
        return data;
    }

    public Object getAdditionalInformation() {
        return addition;
    }

    public boolean isReason(long bitmask) {
        return (reason & bitmask) > 0;
    }

    public static interface ReasonChecker {
        long check(long reason);
    }

    public static void installReasonChecker(ReasonChecker rc) {
        checker = rc;
    }
}

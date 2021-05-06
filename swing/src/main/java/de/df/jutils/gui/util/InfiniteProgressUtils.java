package de.df.jutils.gui.util;

import de.df.jutils.gui.JInfiniteProgressDialog;
import de.df.jutils.gui.JInfiniteProgressFrame;

public final class InfiniteProgressUtils {
	private InfiniteProgressUtils() {}

	public static void setTextAsync(JInfiniteProgressFrame f, String text) {
	    EDTUtils.executeOnEDTAsync(new SetTextRunnable(f, text));
	}

	public static void setTextAsync(JInfiniteProgressDialog f, String text) {
		EDTUtils.executeOnEDTAsync(new SetTextRunnable2(f, text));
	}
	
    private static class SetTextRunnable implements Runnable {

        private JInfiniteProgressFrame w;
        private String                 v;

        public SetTextRunnable(JInfiniteProgressFrame w, String v) {
            this.w = w;
            this.v = v;
        }

        @Override
        public void run() {
            w.setText(v);
        }
    }

    private static class SetTextRunnable2 implements Runnable {

        private JInfiniteProgressDialog w;
        private String                  v;

        public SetTextRunnable2(JInfiniteProgressDialog w, String v) {
            this.w = w;
            this.v = v;
        }

        @Override
        public void run() {
            w.setText(v);
        }

    }	
}

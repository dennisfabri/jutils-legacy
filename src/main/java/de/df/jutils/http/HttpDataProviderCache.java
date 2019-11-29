package de.df.jutils.http;

import java.io.IOException;
import java.util.Hashtable;

public class HttpDataProviderCache implements IHttpDataProvider {

    private final IHttpDataProvider                provider;
    private final Hashtable<String, DataContainer> cache;
    private long                                   lastupdate;

    public HttpDataProviderCache(IHttpDataProvider provider) {
        this.provider = provider;
        cache = new Hashtable<String, DataContainer>();
        lastupdate = 0;
    }

    public void notifyDataUpdate() {
        lastupdate = System.currentTimeMillis();
    }

    public void clearCache() {
        cache.clear();
    }

    @Override
    public byte[] sendData(Request request) throws IOException {
        String id = request.toString();
        boolean success = false;
        try {
            DataContainer dc = cache.get(id);
            if (dc != null) {
                if (lastupdate < dc.getTime()) {
                    return dc.getData();
                }
                cache.remove(id);
            }
            byte[] data = provider.sendData(request);
            success = true;
            cache.put(id, new DataContainer(data));
            return data;
        } finally {
            if (!success) {
                cache.remove(id);
            }
        }
    }

    private static class DataContainer {

        private long   time;
        private byte[] data;

        public long getTime() {
            return time;
        }

        public byte[] getData() {
            return data;
        }

        public DataContainer(byte[] data) {
            time = System.currentTimeMillis();
            this.data = data;
        }
    }
}
// 
// Decompiled by Procyon v0.5.36
// 

package ir.am3n.needtool.webserver.sun;

import java.util.ListIterator;
import java.io.IOException;
import java.nio.channels.Selector;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.LinkedList;

public class SelectorCache
{
    static SelectorCache cache;
    LinkedList<SelectorWrapper> freeSelectors;
    
    private SelectorCache() {
        this.freeSelectors = new LinkedList<SelectorWrapper>();
        AccessController.doPrivileged((PrivilegedAction<CacheCleaner>)new PrivilegedAction<CacheCleaner>() {
            public CacheCleaner run() {
                final CacheCleaner cacheCleaner = new CacheCleaner();
                cacheCleaner.setDaemon(true);
                return cacheCleaner;
            }
        }).start();
    }
    
    public static SelectorCache getSelectorCache() {
        synchronized (SelectorCache.class) {
            if (SelectorCache.cache == null) {
                SelectorCache.cache = new SelectorCache();
            }
        }
        return SelectorCache.cache;
    }
    
    synchronized Selector getSelector() throws IOException {
        Selector selector;
        if (this.freeSelectors.size() > 0) {
            selector = this.freeSelectors.remove().getSelector();
        }
        else {
            selector = Selector.open();
        }
        return selector;
    }
    
    synchronized void freeSelector(final Selector selector) {
        this.freeSelectors.add(new SelectorWrapper(selector));
    }
    
    static {
        SelectorCache.cache = null;
    }
    
    private static class SelectorWrapper
    {
        private Selector sel;
        private boolean deleteFlag;
        
        private SelectorWrapper(final Selector sel) {
            this.sel = sel;
            this.deleteFlag = false;
        }
        
        public Selector getSelector() {
            return this.sel;
        }
        
        public boolean getDeleteFlag() {
            return this.deleteFlag;
        }
        
        public void setDeleteFlag(final boolean deleteFlag) {
            this.deleteFlag = deleteFlag;
        }
    }
    
    class CacheCleaner extends Thread
    {
        @Override
        public void run() {
            final long n = ServerConfig.getSelCacheTimeout() * 1000L;
            while (true) {
                try {
                    Thread.sleep(n);
                }
                catch (Exception ex) {}
                synchronized (SelectorCache.this.freeSelectors) {
                    final ListIterator<SelectorWrapper> listIterator = SelectorCache.this.freeSelectors.listIterator();
                    while (listIterator.hasNext()) {
                        final SelectorWrapper selectorWrapper = listIterator.next();
                        if (selectorWrapper.getDeleteFlag()) {
                            try {
                                selectorWrapper.getSelector().close();
                            } catch (IOException ex2) {}
                            listIterator.remove();
                        }
                        else {
                            selectorWrapper.setDeleteFlag(true);
                        }
                    }
                }
            }
        }
    }
}

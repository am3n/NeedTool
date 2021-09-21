// 
// Decompiled by Procyon v0.5.36
// 

package ir.am3n.needtool.webserver;

import java.util.ListIterator;
import java.util.List;
import java.io.IOException;

public abstract class Filter
{
    protected Filter() {
    }
    
    public abstract void doFilter(final HttpExchange p0, final Chain p1) throws IOException;
    
    public abstract String description();
    
    public static class Chain
    {
        private List<Filter> filters;
        private ListIterator<Filter> iter;
        private HttpHandler handler;
        
        public Chain(final List<Filter> filters, final HttpHandler handler) {
            this.filters = filters;
            this.iter = filters.listIterator();
            this.handler = handler;
        }
        
        public void doFilter(final HttpExchange httpExchange) throws IOException {
            if (!this.iter.hasNext()) {
                this.handler.handle(httpExchange);
            }
            else {
                this.iter.next().doFilter(httpExchange, this);
            }
        }
    }
}

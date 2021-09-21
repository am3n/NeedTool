// 
// Decompiled by Procyon v0.5.36
// 

package ir.am3n.needtool.webserver.sun;

import java.util.logging.Logger;
import java.util.List;
import ir.am3n.needtool.webserver.HttpServer;
import java.util.HashMap;
import ir.am3n.needtool.webserver.Authenticator;
import ir.am3n.needtool.webserver.Filter;
import java.util.LinkedList;
import java.util.Map;
import ir.am3n.needtool.webserver.HttpHandler;
import ir.am3n.needtool.webserver.HttpContext;

class HttpContextImpl extends HttpContext
{
    private String path;
    private String protocol;
    private HttpHandler handler;
    private Map<String, Object> attributes;
    private ServerImpl server;
    private LinkedList<Filter> sfilters;
    private LinkedList<Filter> ufilters;
    private Authenticator authenticator;
    private AuthFilter authfilter;
    
    HttpContextImpl(final String s, final String path, final HttpHandler handler, final ServerImpl server) {
        this.attributes = new HashMap<String, Object>();
        this.sfilters = new LinkedList<Filter>();
        this.ufilters = new LinkedList<Filter>();
        if (path == null || s == null || path.length() < 1 || path.charAt(0) != '/') {
            throw new IllegalArgumentException("Illegal value for path or protocol");
        }
        this.protocol = s.toLowerCase();
        this.path = path;
        if (!this.protocol.equals("http") && !this.protocol.equals("https")) {
            throw new IllegalArgumentException("Illegal value for protocol");
        }
        this.handler = handler;
        this.server = server;
        this.authfilter = new AuthFilter(null);
        this.sfilters.add(this.authfilter);
    }
    
    @Override
    public HttpHandler getHandler() {
        return this.handler;
    }
    
    @Override
    public void setHandler(final HttpHandler handler) {
        if (handler == null) {
            throw new NullPointerException("Null handler parameter");
        }
        if (this.handler != null) {
            throw new IllegalArgumentException("handler already set");
        }
        this.handler = handler;
    }
    
    @Override
    public String getPath() {
        return this.path;
    }
    
    @Override
    public HttpServer getServer() {
        return this.server.getWrapper();
    }
    
    ServerImpl getServerImpl() {
        return this.server;
    }
    
    public String getProtocol() {
        return this.protocol;
    }
    
    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }
    
    @Override
    public List<Filter> getFilters() {
        return this.ufilters;
    }
    
    List<Filter> getSystemFilters() {
        return this.sfilters;
    }
    
    @Override
    public Authenticator setAuthenticator(final Authenticator authenticator) {
        final Authenticator authenticator2 = this.authenticator;
        this.authenticator = authenticator;
        this.authfilter.setAuthenticator(authenticator);
        return authenticator2;
    }
    
    @Override
    public Authenticator getAuthenticator() {
        return this.authenticator;
    }
    
    Logger getLogger() {
        return this.server.getLogger();
    }
}

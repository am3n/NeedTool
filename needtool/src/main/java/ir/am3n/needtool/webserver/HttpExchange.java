// 
// Decompiled by Procyon v0.5.36
// 

package ir.am3n.needtool.webserver;

import java.net.InetSocketAddress;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.net.URI;

public abstract class HttpExchange
{
    protected HttpExchange() {
    }
    
    public abstract Headers getRequestHeaders();
    
    public abstract Headers getResponseHeaders();
    
    public abstract URI getRequestURI();
    
    public abstract String getRequestMethod();
    
    public abstract HttpContext getHttpContext();
    
    public abstract void close();
    
    public abstract InputStream getRequestBody();
    
    public abstract OutputStream getResponseBody();
    
    public abstract void sendResponseHeaders(final int p0, final long p1) throws IOException;
    
    public abstract InetSocketAddress getRemoteAddress();
    
    public abstract int getResponseCode();
    
    public abstract InetSocketAddress getLocalAddress();
    
    public abstract String getProtocol();
    
    public abstract Object getAttribute(final String p0);
    
    public abstract void setAttribute(final String p0, final Object p1);
    
    public abstract void setStreams(final InputStream p0, final OutputStream p1);
    
    public abstract HttpPrincipal getPrincipal();
}

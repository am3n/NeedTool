// 
// Decompiled by Procyon v0.5.36
// 

package ir.am3n.needtool.webserver;

import java.util.concurrent.Executor;
import ir.am3n.needtool.webserver.spi.HttpServerProvider;
import java.io.IOException;
import java.net.InetSocketAddress;

public abstract class HttpServer
{
    protected HttpServer() {
    }
    
    public static HttpServer create() throws IOException {
        return create(null, 0);
    }
    
    public static HttpServer create(final InetSocketAddress inetSocketAddress, final int n) throws IOException {
        return HttpServerProvider.provider().createHttpServer(inetSocketAddress, n);
    }
    
    public abstract void bind(final InetSocketAddress p0, final int p1) throws IOException;
    
    public abstract void start();
    
    public abstract void setExecutor(final Executor p0);
    
    public abstract Executor getExecutor();
    
    public abstract void stop(final int p0);
    
    public abstract HttpContext createContext(final String p0, final HttpHandler p1);
    
    public abstract HttpContext createContext(final String p0);
    
    public abstract void removeContext(final String p0) throws IllegalArgumentException;
    
    public abstract void removeContext(final HttpContext p0);
    
    public abstract InetSocketAddress getAddress();
}

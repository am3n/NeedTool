// 
// Decompiled by Procyon v0.5.36
// 

package ir.am3n.needtool.webserver.sun;

import ir.am3n.needtool.webserver.HttpContext;
import ir.am3n.needtool.webserver.HttpHandler;
import java.util.concurrent.Executor;
import java.io.IOException;
import java.net.InetSocketAddress;
import ir.am3n.needtool.webserver.HttpServer;

public class HttpServerImpl extends HttpServer
{
    ServerImpl server;
    
    HttpServerImpl() throws IOException {
        this(new InetSocketAddress(80), 0);
    }
    
    HttpServerImpl(final InetSocketAddress inetSocketAddress, final int n) throws IOException {
        this.server = new ServerImpl(this, "http", inetSocketAddress, n);
    }
    
    @Override
    public void bind(final InetSocketAddress inetSocketAddress, final int n) throws IOException {
        this.server.bind(inetSocketAddress, n);
    }
    
    @Override
    public void start() {
        this.server.start();
    }
    
    @Override
    public void setExecutor(final Executor executor) {
        this.server.setExecutor(executor);
    }
    
    @Override
    public Executor getExecutor() {
        return this.server.getExecutor();
    }
    
    @Override
    public void stop(final int n) {
        this.server.stop(n);
    }
    
    @Override
    public HttpContextImpl createContext(final String s, final HttpHandler httpHandler) {
        return this.server.createContext(s, httpHandler);
    }
    
    @Override
    public HttpContextImpl createContext(final String s) {
        return this.server.createContext(s);
    }
    
    @Override
    public void removeContext(final String s) throws IllegalArgumentException {
        this.server.removeContext(s);
    }
    
    @Override
    public void removeContext(final HttpContext httpContext) throws IllegalArgumentException {
        this.server.removeContext(httpContext);
    }
    
    @Override
    public InetSocketAddress getAddress() {
        return this.server.getAddress();
    }
}

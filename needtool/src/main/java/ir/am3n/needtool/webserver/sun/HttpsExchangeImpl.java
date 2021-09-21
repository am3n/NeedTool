// 
// Decompiled by Procyon v0.5.36
// 

package ir.am3n.needtool.webserver.sun;

import ir.am3n.needtool.webserver.HttpContext;
import ir.am3n.needtool.webserver.HttpPrincipal;
import javax.net.ssl.SSLSession;
import java.net.InetSocketAddress;
import java.io.OutputStream;
import java.io.InputStream;
import java.net.URI;
import ir.am3n.needtool.webserver.Headers;
import java.io.IOException;
import ir.am3n.needtool.webserver.HttpsExchange;

class HttpsExchangeImpl extends HttpsExchange
{
    ExchangeImpl impl;
    
    HttpsExchangeImpl(final ExchangeImpl impl) throws IOException {
        this.impl = impl;
    }
    
    @Override
    public Headers getRequestHeaders() {
        return this.impl.getRequestHeaders();
    }
    
    @Override
    public Headers getResponseHeaders() {
        return this.impl.getResponseHeaders();
    }
    
    @Override
    public URI getRequestURI() {
        return this.impl.getRequestURI();
    }
    
    @Override
    public String getRequestMethod() {
        return this.impl.getRequestMethod();
    }
    
    @Override
    public HttpContextImpl getHttpContext() {
        return this.impl.getHttpContext();
    }
    
    @Override
    public void close() {
        this.impl.close();
    }
    
    @Override
    public InputStream getRequestBody() {
        return this.impl.getRequestBody();
    }
    
    @Override
    public int getResponseCode() {
        return this.impl.getResponseCode();
    }
    
    @Override
    public OutputStream getResponseBody() {
        return this.impl.getResponseBody();
    }
    
    @Override
    public void sendResponseHeaders(final int n, final long n2) throws IOException {
        this.impl.sendResponseHeaders(n, n2);
    }
    
    @Override
    public InetSocketAddress getRemoteAddress() {
        return this.impl.getRemoteAddress();
    }
    
    @Override
    public InetSocketAddress getLocalAddress() {
        return this.impl.getLocalAddress();
    }
    
    @Override
    public String getProtocol() {
        return this.impl.getProtocol();
    }
    
    @Override
    public SSLSession getSSLSession() {
        return this.impl.getSSLSession();
    }
    
    @Override
    public Object getAttribute(final String s) {
        return this.impl.getAttribute(s);
    }
    
    @Override
    public void setAttribute(final String s, final Object o) {
        this.impl.setAttribute(s, o);
    }
    
    @Override
    public void setStreams(final InputStream inputStream, final OutputStream outputStream) {
        this.impl.setStreams(inputStream, outputStream);
    }
    
    @Override
    public HttpPrincipal getPrincipal() {
        return this.impl.getPrincipal();
    }
    
    ExchangeImpl getExchangeImpl() {
        return this.impl;
    }
}

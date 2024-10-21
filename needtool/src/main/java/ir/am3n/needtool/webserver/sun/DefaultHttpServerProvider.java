// 
// Decompiled by Procyon v0.5.36
// 

package ir.am3n.needtool.webserver.sun;

import ir.am3n.needtool.webserver.HttpsServer;

import java.io.IOException;

import ir.am3n.needtool.webserver.HttpServer;

import java.net.InetSocketAddress;

import ir.am3n.needtool.webserver.spi.HttpServerProvider;

public class DefaultHttpServerProvider extends HttpServerProvider {
    @Override
    public HttpServer createHttpServer(final InetSocketAddress inetSocketAddress, final int n) throws IOException {
        return new HttpServerImpl(inetSocketAddress, n);
    }

    @Override
    public HttpsServer createHttpsServer(final InetSocketAddress inetSocketAddress, final int n) throws IOException {
        return new HttpsServerImpl(inetSocketAddress, n);
    }
}

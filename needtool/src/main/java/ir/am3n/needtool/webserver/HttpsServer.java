// 
// Decompiled by Procyon v0.5.36
// 

package ir.am3n.needtool.webserver;

import ir.am3n.needtool.webserver.spi.HttpServerProvider;
import java.io.IOException;
import java.net.InetSocketAddress;

public abstract class HttpsServer extends HttpServer
{
    protected HttpsServer() {
    }
    
    public static HttpsServer create() throws IOException {
        return create(null, 0);
    }
    
    public static HttpsServer create(final InetSocketAddress inetSocketAddress, final int n) throws IOException {
        return HttpServerProvider.provider().createHttpsServer(inetSocketAddress, n);
    }
    
    public abstract void setHttpsConfigurator(final HttpsConfigurator p0);
    
    public abstract HttpsConfigurator getHttpsConfigurator();
}

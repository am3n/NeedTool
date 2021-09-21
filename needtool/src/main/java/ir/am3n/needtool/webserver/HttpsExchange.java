// 
// Decompiled by Procyon v0.5.36
// 

package ir.am3n.needtool.webserver;

import javax.net.ssl.SSLSession;

public abstract class HttpsExchange extends HttpExchange
{
    protected HttpsExchange() {
    }
    
    public abstract SSLSession getSSLSession();
}

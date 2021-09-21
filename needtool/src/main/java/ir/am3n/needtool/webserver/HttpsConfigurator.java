// 
// Decompiled by Procyon v0.5.36
// 

package ir.am3n.needtool.webserver;

import javax.net.ssl.SSLContext;

public class HttpsConfigurator
{
    private SSLContext context;
    
    public HttpsConfigurator(final SSLContext context) {
        if (context == null) {
            throw new NullPointerException("null SSLContext");
        }
        this.context = context;
    }
    
    public SSLContext getSSLContext() {
        return this.context;
    }
    
    public void configure(final HttpsParameters httpsParameters) {
    }
}

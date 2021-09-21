// 
// Decompiled by Procyon v0.5.36
// 

package ir.am3n.needtool.webserver;

import java.net.InetSocketAddress;

public abstract class HttpsParameters
{
    private String[] cipherSuites;
    private String[] protocols;
    private boolean wantClientAuth;
    private boolean needClientAuth;
    
    protected HttpsParameters() {
    }
    
    public abstract HttpsConfigurator getHttpsConfigurator();
    
    public abstract InetSocketAddress getClientAddress();
    
    public String[] getCipherSuites() {
        return this.cipherSuites;
    }
    
    public void setCipherSuites(final String[] cipherSuites) {
        this.cipherSuites = cipherSuites;
    }
    
    public String[] getProtocols() {
        return this.protocols;
    }
    
    public void setProtocols(final String[] protocols) {
        this.protocols = protocols;
    }
    
    public boolean getWantClientAuth() {
        return this.wantClientAuth;
    }
    
    public void setWantClientAuth(final boolean wantClientAuth) {
        this.wantClientAuth = wantClientAuth;
    }
    
    public boolean getNeedClientAuth() {
        return this.needClientAuth;
    }
    
    public void setNeedClientAuth(final boolean needClientAuth) {
        this.needClientAuth = needClientAuth;
    }
}

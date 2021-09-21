// 
// Decompiled by Procyon v0.5.36
// 

package ir.am3n.needtool.webserver.spi;

import java.security.AccessController;
import ir.am3n.needtool.webserver.sun.DefaultHttpServerProvider;

import java.security.PrivilegedAction;
import java.util.Iterator;
import ir.am3n.needtool.webserver.HttpsServer;
import java.io.IOException;
import ir.am3n.needtool.webserver.HttpServer;
import java.net.InetSocketAddress;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

public abstract class HttpServerProvider
{
    private static final Object lock;
    private static HttpServerProvider provider;
    
    public abstract HttpServer createHttpServer(final InetSocketAddress p0, final int p1) throws IOException;
    
    public abstract HttpsServer createHttpsServer(final InetSocketAddress p0, final int p1) throws IOException;
    
    protected HttpServerProvider() {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(new RuntimePermission("httpServerProvider"));
        }
    }
    
    private static boolean loadProviderFromProperty() {
        final String property = System.getProperty("ir.am3n.needtool.webserver.HttpServerProvider");
        if (property == null) {
            return false;
        }
        try {
            HttpServerProvider.provider = (HttpServerProvider)Class.forName(property, true, ClassLoader.getSystemClassLoader()).newInstance();
            return true;
        }
        catch (ClassNotFoundException ex) {
            throw new ServiceConfigurationError(((Throwable)ex).getMessage());
        }
        catch (IllegalAccessException ex2) {
            throw new ServiceConfigurationError(((Throwable)ex2).getMessage());
        }
        catch (InstantiationException ex3) {
            throw new ServiceConfigurationError(((Throwable)ex3).getMessage());
        }
        catch (SecurityException ex4) {
            throw new ServiceConfigurationError(((Throwable)ex4).getMessage());
        }
    }
    
    private static boolean loadProviderAsService() {
        final Iterator providers = ServiceLoader.load((Class)HttpServerProvider.class, ClassLoader.getSystemClassLoader()).iterator();
        while (true) {
            try {
                if (!providers.hasNext()) {
                    return false;
                }
                HttpServerProvider.provider = (HttpServerProvider) providers.next();
                return true;
            }
            catch (ServiceConfigurationError serviceConfigurationError) {
                if (serviceConfigurationError.getCause() instanceof SecurityException) {
                    continue;
                }
                throw serviceConfigurationError;
            }
        }
    }
    
    public static HttpServerProvider provider() {
        synchronized (HttpServerProvider.lock) {
            if (HttpServerProvider.provider != null) {
                return HttpServerProvider.provider;
            }
            return AccessController.doPrivileged((PrivilegedAction<HttpServerProvider>) () -> {
                if (loadProviderFromProperty()) {
                    return HttpServerProvider.provider;
                }
                if (loadProviderAsService()) {
                    return HttpServerProvider.provider;
                }
                HttpServerProvider.provider = new DefaultHttpServerProvider();
                return HttpServerProvider.provider;
            });
        }
    }
    
    static {
        lock = new Object();
        HttpServerProvider.provider = null;
    }
}

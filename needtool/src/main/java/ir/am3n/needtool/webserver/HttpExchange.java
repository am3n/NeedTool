// 
// Decompiled by Procyon v0.5.36
// 

package ir.am3n.needtool.webserver;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;

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

    public void sendUTF8Response(String data) throws IOException {
        this.getResponseHeaders().set("Content-Type", "text/plain; charset=utf-8");
        send(data);
    }

    public void sendUTF8HtmlResponse(String data) throws IOException {
        getResponseHeaders().set("Content-Type", "text/htnl; charset=utf-8");
        send(data);
    }

    public void sendUTF8JsonResponse(@NotNull JSONObject oject) throws IOException {
        getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        send(oject.toString());
    }

    public void sendUTF8JsonResponse(@NotNull JSONArray oject) throws IOException {
        getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        send(oject.toString());
    }

    private void send(String data) throws IOException {
        sendResponseHeaders(200, 0);
        BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(getResponseBody(), StandardCharsets.UTF_8),
                8 * 1024
        );
        writer.write(data);
        writer.flush();
        writer.close();
    }

}

// 
// Decompiled by Procyon v0.5.36
// 

package ir.am3n.needtool.webserver;

import java.io.IOException;

public interface HttpHandler
{
    void handle(final HttpExchange p0) throws IOException;
}

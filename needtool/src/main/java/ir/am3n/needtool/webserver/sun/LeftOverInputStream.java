// 
// Decompiled by Procyon v0.5.36
// 

package ir.am3n.needtool.webserver.sun;

import java.io.IOException;
import java.io.InputStream;
import java.io.FilterInputStream;

abstract class LeftOverInputStream extends FilterInputStream
{
    ExchangeImpl t;
    ServerImpl server;
    protected boolean closed;
    protected boolean eof;
    byte[] one;
    
    public LeftOverInputStream(final ExchangeImpl t, final InputStream in) {
        super(in);
        this.closed = false;
        this.eof = false;
        this.one = new byte[1];
        this.t = t;
        this.server = t.getServerImpl();
    }
    
    public boolean isDataBuffered() throws IOException {
        assert this.eof;
        return super.available() > 0;
    }
    
    @Override
    public void close() throws IOException {
        if (this.closed) {
            return;
        }
        this.closed = true;
        if (!this.eof) {
            this.eof = this.drain(ServerConfig.getDrainAmount());
        }
    }
    
    public boolean isClosed() {
        return this.closed;
    }
    
    public boolean isEOF() {
        return this.eof;
    }
    
    protected abstract int readImpl(final byte[] p0, final int p1, final int p2) throws IOException;
    
    @Override
    public synchronized int read() throws IOException {
        if (this.closed) {
            throw new IOException("Stream is closed");
        }
        final int impl = this.readImpl(this.one, 0, 1);
        if (impl == -1 || impl == 0) {
            return impl;
        }
        return this.one[0] & 0xFF;
    }
    
    @Override
    public synchronized int read(final byte[] array, final int n, final int n2) throws IOException {
        if (this.closed) {
            throw new IOException("Stream is closed");
        }
        return this.readImpl(array, n, n2);
    }
    
    public boolean drain(long n) throws IOException {
        final int n2 = 2048;
        final byte[] array = new byte[n2];
        while (n > 0L) {
            final long n3 = this.readImpl(array, 0, n2);
            if (n3 == -1L) {
                return this.eof = true;
            }
            n -= n3;
        }
        return false;
    }
}

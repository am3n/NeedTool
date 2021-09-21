// 
// Decompiled by Procyon v0.5.36
// 

package ir.am3n.needtool.webserver.sun;

import java.io.IOException;
import java.io.OutputStream;
import java.io.FilterOutputStream;

class FixedLengthOutputStream extends FilterOutputStream
{
    private long remaining;
    private boolean eof;
    private boolean closed;
    ExchangeImpl t;
    
    FixedLengthOutputStream(final ExchangeImpl t, final OutputStream out, final long remaining) {
        super(out);
        this.eof = false;
        this.closed = false;
        this.t = t;
        this.remaining = remaining;
    }
    
    @Override
    public void write(final int n) throws IOException {
        if (this.closed) {
            throw new IOException("stream closed");
        }
        this.eof = (this.remaining == 0L);
        if (this.eof) {
            throw new StreamClosedException();
        }
        this.out.write(n);
        --this.remaining;
    }
    
    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException {
        if (this.closed) {
            throw new IOException("stream closed");
        }
        this.eof = (this.remaining == 0L);
        if (this.eof) {
            throw new StreamClosedException();
        }
        if (len > this.remaining) {
            throw new IOException("too many bytes to write to stream");
        }
        this.out.write(b, off, len);
        this.remaining -= len;
    }
    
    @Override
    public void close() throws IOException {
        if (this.closed) {
            return;
        }
        this.closed = true;
        if (this.remaining > 0L) {
            this.t.close();
            throw new IOException("insufficient bytes written to stream");
        }
        this.flush();
        this.eof = true;
        final LeftOverInputStream originalInputStream = this.t.getOriginalInputStream();
        if (!originalInputStream.isClosed()) {
            try {
                originalInputStream.close();
            }
            catch (IOException ex) {}
        }
        this.t.getHttpContext().getServerImpl().addEvent(new WriteFinishedEvent(this.t));
    }
}

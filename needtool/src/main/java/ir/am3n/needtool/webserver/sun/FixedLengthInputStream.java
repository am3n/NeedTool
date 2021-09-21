// 
// Decompiled by Procyon v0.5.36
// 

package ir.am3n.needtool.webserver.sun;

import java.io.IOException;
import java.io.InputStream;

class FixedLengthInputStream extends LeftOverInputStream
{
    private int remaining;
    
    FixedLengthInputStream(final ExchangeImpl exchangeImpl, final InputStream inputStream, final int remaining) {
        super(exchangeImpl, inputStream);
        this.remaining = remaining;
    }
    
    @Override
    protected int readImpl(final byte[] b, final int off, int remaining) throws IOException {
        this.eof = (this.remaining == 0);
        if (this.eof) {
            return -1;
        }
        if (remaining > this.remaining) {
            remaining = this.remaining;
        }
        final int read = this.in.read(b, off, remaining);
        if (read > -1) {
            this.remaining -= read;
            if (this.remaining == 0) {
                this.t.getServerImpl().requestCompleted(this.t.getConnection());
            }
        }
        return read;
    }
    
    @Override
    public int available() throws IOException {
        if (this.eof) {
            return 0;
        }
        final int available = this.in.available();
        return (available < this.remaining) ? available : this.remaining;
    }
    
    @Override
    public boolean markSupported() {
        return false;
    }
    
    @Override
    public void mark(final int n) {
    }
    
    @Override
    public void reset() throws IOException {
        throw new IOException("mark/reset not supported");
    }
}

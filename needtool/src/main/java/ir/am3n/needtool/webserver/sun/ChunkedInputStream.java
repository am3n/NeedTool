// 
// Decompiled by Procyon v0.5.36
// 

package ir.am3n.needtool.webserver.sun;

import java.io.IOException;
import java.io.InputStream;

class ChunkedInputStream extends LeftOverInputStream
{
    private int remaining;
    private boolean needToReadHeader;
    static char CR;
    static char LF;
    
    ChunkedInputStream(final ExchangeImpl exchangeImpl, final InputStream inputStream) {
        super(exchangeImpl, inputStream);
        this.needToReadHeader = true;
    }
    
    private int numeric(final char[] array, final int n) throws IOException {
        assert array.length >= n;
        int n2 = 0;
        for (final char c : array) {
            int n3;
            if (c >= '0' && c <= '9') {
                n3 = c - '0';
            }
            else if (c >= 'a' && c <= 'f') {
                n3 = c - 'a' + 10;
            }
            else {
                if (c < 'A' || c > 'F') {
                    throw new IOException("invalid chunk length");
                }
                n3 = c - 'A' + 10;
            }
            n2 = n2 * 16 + n3;
        }
        return n2;
    }
    
    private int readChunkHeader() throws IOException {
        int n = 0;
        final char[] array = new char[16];
        int n2 = 0;
        int n3 = 0;
        char c;
        while ((c = (char)this.in.read()) != -1) {
            if (n2 == array.length - 1) {
                throw new IOException("invalid chunk header");
            }
            if (n != 0) {
                if (c == ChunkedInputStream.LF) {
                    return this.numeric(array, n2);
                }
                n = 0;
                if (n3 != 0) {
                    continue;
                }
                array[n2++] = c;
            }
            else if (c == ChunkedInputStream.CR) {
                n = 1;
            }
            else if (c == ';') {
                n3 = 1;
            }
            else {
                if (n3 != 0) {
                    continue;
                }
                array[n2++] = c;
            }
        }
        throw new IOException("end of stream reading chunk header");
    }
    
    @Override
    protected int readImpl(final byte[] b, final int off, int remaining) throws IOException {
        if (this.eof) {
            return -1;
        }
        if (this.needToReadHeader) {
            this.remaining = this.readChunkHeader();
            if (this.remaining == 0) {
                this.eof = true;
                this.consumeCRLF();
                this.t.getServerImpl().requestCompleted(this.t.getConnection());
                return -1;
            }
            this.needToReadHeader = false;
        }
        if (remaining > this.remaining) {
            remaining = this.remaining;
        }
        final int read = this.in.read(b, off, remaining);
        if (read > -1) {
            this.remaining -= read;
        }
        if (this.remaining == 0) {
            this.needToReadHeader = true;
            this.consumeCRLF();
        }
        return read;
    }
    
    private void consumeCRLF() throws IOException {
        if ((char)this.in.read() != ChunkedInputStream.CR) {
            throw new IOException("invalid chunk end");
        }
        if ((char)this.in.read() != ChunkedInputStream.LF) {
            throw new IOException("invalid chunk end");
        }
    }
    
    @Override
    public int available() throws IOException {
        if (this.eof || this.closed) {
            return 0;
        }
        final int available = this.in.available();
        return (available > this.remaining) ? this.remaining : available;
    }
    
    @Override
    public boolean isDataBuffered() throws IOException {
        assert this.eof;
        return this.in.available() > 0;
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
    
    static {
        ChunkedInputStream.CR = '\r';
        ChunkedInputStream.LF = '\n';
    }
}

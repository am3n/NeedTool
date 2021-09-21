// 
// Decompiled by Procyon v0.5.36
// 

package ir.am3n.needtool.webserver.sun;

import java.io.IOException;
import java.io.OutputStream;
import java.io.FilterOutputStream;

class ChunkedOutputStream extends FilterOutputStream
{
    private boolean closed;
    static final int CHUNK_SIZE = 4096;
    static final int OFFSET = 6;
    private int pos;
    private int count;
    private byte[] buf;
    ExchangeImpl t;
    
    ChunkedOutputStream(final ExchangeImpl t, final OutputStream out) {
        super(out);
        this.closed = false;
        this.pos = 6;
        this.count = 0;
        this.buf = new byte[4104];
        this.t = t;
    }
    
    @Override
    public void write(final int n) throws IOException {
        if (this.closed) {
            throw new StreamClosedException();
        }
        this.buf[this.pos++] = (byte)n;
        ++this.count;
        if (this.count == 4096) {
            this.writeChunk();
        }
    }
    
    @Override
    public void write(final byte[] array, int n, int i) throws IOException {
        if (this.closed) {
            throw new StreamClosedException();
        }
        final int n2 = 4096 - this.count;
        if (i > n2) {
            System.arraycopy(array, n, this.buf, this.pos, n2);
            this.count = 4096;
            this.writeChunk();
            i -= n2;
            n += n2;
            while (i >= 4096) {
                System.arraycopy(array, n, this.buf, 6, 4096);
                i -= 4096;
                n += 4096;
                this.count = 4096;
                this.writeChunk();
            }
        }
        if (i > 0) {
            System.arraycopy(array, n, this.buf, this.pos, i);
            this.count += i;
            this.pos += i;
        }
        if (this.count == 4096) {
            this.writeChunk();
        }
    }
    
    private void writeChunk() throws IOException {
        final char[] charArray = Integer.toHexString(this.count).toCharArray();
        final int length = charArray.length;
        final int off = 4 - length;
        int i;
        for (i = 0; i < length; ++i) {
            this.buf[off + i] = (byte)charArray[i];
        }
        this.buf[off + i++] = 13;
        this.buf[off + i++] = 10;
        this.buf[off + i++ + this.count] = 13;
        this.buf[off + i++ + this.count] = 10;
        this.out.write(this.buf, off, i + this.count);
        this.count = 0;
        this.pos = 6;
    }
    
    @Override
    public void close() throws IOException {
        if (this.closed) {
            return;
        }
        this.flush();
        this.writeChunk();
        this.out.flush();
        this.closed = true;
        final LeftOverInputStream originalInputStream = this.t.getOriginalInputStream();
        if (!originalInputStream.isClosed()) {
            try {
                originalInputStream.close();
            }
            catch (IOException ex) {}
        }
        this.t.getHttpContext().getServerImpl().addEvent(new WriteFinishedEvent(this.t));
    }
    
    @Override
    public void flush() throws IOException {
        if (this.closed) {
            throw new StreamClosedException();
        }
        if (this.count > 0) {
            this.writeChunk();
        }
        this.out.flush();
    }
}

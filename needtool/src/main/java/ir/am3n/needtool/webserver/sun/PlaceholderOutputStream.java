// 
// Decompiled by Procyon v0.5.36
// 

package ir.am3n.needtool.webserver.sun;

import java.io.IOException;
import java.io.OutputStream;

class PlaceholderOutputStream extends OutputStream
{
    OutputStream wrapped;
    
    PlaceholderOutputStream(final OutputStream wrapped) {
        this.wrapped = wrapped;
    }
    
    void setWrappedStream(final OutputStream wrapped) {
        this.wrapped = wrapped;
    }
    
    boolean isWrapped() {
        return this.wrapped != null;
    }
    
    private void checkWrap() throws IOException {
        if (this.wrapped == null) {
            throw new IOException("response headers not sent yet");
        }
    }
    
    @Override
    public void write(final int n) throws IOException {
        this.checkWrap();
        this.wrapped.write(n);
    }
    
    @Override
    public void write(final byte[] b) throws IOException {
        this.checkWrap();
        this.wrapped.write(b);
    }
    
    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException {
        this.checkWrap();
        this.wrapped.write(b, off, len);
    }
    
    @Override
    public void flush() throws IOException {
        this.checkWrap();
        this.wrapped.flush();
    }
    
    @Override
    public void close() throws IOException {
        this.checkWrap();
        this.wrapped.close();
    }
}

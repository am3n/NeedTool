// 
// Decompiled by Procyon v0.5.36
// 

package ir.am3n.needtool.webserver.sun;

import java.nio.channels.SelectionKey;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.io.IOException;

import ir.am3n.needtool.webserver.Headers;

import java.io.OutputStream;
import java.io.InputStream;
import java.nio.channels.SocketChannel;

class Request {

    static final int BUF_LEN = 2048;
    static final byte CR = 13;
    static final byte LF = 10;
    private String startLine;
    private SocketChannel chan;
    private InputStream is;
    private OutputStream os;
    char[] buf;
    int pos;
    StringBuffer lineBuf;
    Headers hdrs;

    Request(final InputStream is, final OutputStream os) throws IOException {
        this.buf = new char[2048];
        this.hdrs = null;
        this.chan = this.chan;
        this.is = is;
        this.os = os;
        do {
            this.startLine = this.readLine();
            if (this.startLine == null) {
                return;
            }
        } while (this.startLine.equals(""));
    }

    public InputStream inputStream() {
        return this.is;
    }

    public OutputStream outputStream() {
        return this.os;
    }

    public String readLine() throws IOException {
        int n = 0;
        int i = 0;
        this.pos = 0;
        this.lineBuf = new StringBuffer();
        while (i == 0) {
            final int read = this.is.read();
            if (read == -1) {
                return null;
            }
            if (n != 0) {
                if (read == 10) {
                    i = 1;
                } else {
                    n = 0;
                    this.consume(13);
                    this.consume(read);
                }
            } else if (read == 13) {
                n = 1;
            } else {
                this.consume(read);
            }
        }
        this.lineBuf.append(this.buf, 0, this.pos);
        return new String(this.lineBuf);
    }

    private void consume(final int n) {
        if (this.pos == 2048) {
            this.lineBuf.append(this.buf);
            this.pos = 0;
        }
        this.buf[this.pos++] = (char) n;
    }

    public String requestLine() {
        return this.startLine;
    }

    Headers headers() throws IOException {
        if (this.hdrs != null) {
            return this.hdrs;
        }
        this.hdrs = new Headers();
        char[] array = new char[10];
        int n = this.is.read();
        Label_0036:
        while (n != 10 && n != 13 && n >= 0) {
            int n2 = 0;
            int n3 = -1;
            int n4 = (n > 32) ? 1 : 0;
            array[n2++] = (char) n;
            while (true) {
                int read;
                while ((read = this.is.read()) >= 0) {
                    switch (read) {
                        case 58: {
                            if (n4 != 0 && n2 > 0) {
                                n3 = n2;
                            }
                            n4 = 0;
                            break;
                        }
                        case 9: {
                            read = 32;
                        }
                        case 32: {
                            n4 = 0;
                            break;
                        }
                        case 10:
                        case 13: {
                            n = this.is.read();
                            if (read == 13 && n == 10) {
                                n = this.is.read();
                                if (n == 13) {
                                    n = this.is.read();
                                }
                            }
                            if (n != 10 && n != 13) {
                                if (n <= 32) {
                                    read = 32;
                                    break;
                                }
                            }
                            while (n2 > 0 && array[n2 - 1] <= ' ') {
                                --n2;
                            }
                            String copyValue;
                            if (n3 <= 0) {
                                copyValue = null;
                                n3 = 0;
                            } else {
                                copyValue = String.copyValueOf(array, 0, n3);
                                if (n3 < n2 && array[n3] == ':') {
                                    ++n3;
                                }
                                while (n3 < n2 && array[n3] <= ' ') {
                                    ++n3;
                                }
                            }
                            String copyValue2;
                            if (n3 >= n2) {
                                copyValue2 = new String();
                            } else {
                                copyValue2 = String.copyValueOf(array, n3, n2 - n3);
                            }
                            this.hdrs.add(copyValue, copyValue2);
                            continue Label_0036;
                        }
                    }
                    if (n2 >= array.length) {
                        final char[] array2 = new char[array.length * 2];
                        System.arraycopy(array, 0, array2, 0, n2);
                        array = array2;
                    }
                    array[n2++] = (char) read;
                }
                n = -1;
                continue;
            }
        }
        return this.hdrs;
    }

    static class ReadStream extends InputStream {
        SocketChannel channel;
        ByteBuffer chanbuf;
        byte[] one;
        private boolean closed;
        private boolean eof;
        ByteBuffer markBuf;
        boolean marked;
        boolean reset;
        int readlimit;
        static long readTimeout;
        ServerImpl server;
        static final int BUFSIZE = 8192;

        public ReadStream(final ServerImpl server, final SocketChannel channel) throws IOException {
            this.closed = false;
            this.eof = false;
            this.channel = channel;
            this.server = server;
            (this.chanbuf = ByteBuffer.allocate(8192)).clear();
            this.one = new byte[1];
            assert this.channel.isBlocking();
            final boolean closed = false;
            this.reset = closed;
            this.marked = closed;
            this.closed = closed;
        }

        @Override
        public synchronized int read(final byte[] array) throws IOException {
            return this.read(array, 0, array.length);
        }

        @Override
        public synchronized int read() throws IOException {
            if (this.read(this.one, 0, 1) == 1) {
                return this.one[0] & 0xFF;
            }
            return -1;
        }

        @Override
        public synchronized int read(final byte[] src, final int offset, final int n) throws IOException {
            if (this.closed) {
                throw new IOException("Stream closed");
            }
            if (this.eof) {
                return -1;
            }
            if (offset < 0 || n < 0 || n > src.length - offset) {
                throw new IndexOutOfBoundsException();
            }
            int i;
            if (this.reset) {
                final int remaining = this.markBuf.remaining();
                i = ((remaining > n) ? n : remaining);
                this.markBuf.get(src, offset, i);
                if (remaining == i) {
                    this.reset = false;
                }
            } else {
                this.chanbuf.clear();
                if (n < 8192) {
                    this.chanbuf.limit(n);
                }
                do {
                    i = this.channel.read(this.chanbuf);
                } while (i == 0);
                if (i == -1) {
                    this.eof = true;
                    return -1;
                }
                this.chanbuf.flip();
                this.chanbuf.get(src, offset, i);
                if (this.marked) {
                    try {
                        this.markBuf.put(src, offset, i);
                    } catch (BufferOverflowException ex) {
                        this.marked = false;
                    }
                }
            }
            return i;
        }

        @Override
        public boolean markSupported() {
            return true;
        }

        @Override
        public synchronized int available() throws IOException {
            if (this.closed) {
                throw new IOException("Stream is closed");
            }
            if (this.eof) {
                return -1;
            }
            if (this.reset) {
                return this.markBuf.remaining();
            }
            return this.chanbuf.remaining();
        }

        @Override
        public void close() throws IOException {
            if (this.closed) {
                return;
            }
            this.server.logStackTrace("Request.close: isOpen=" + this.channel.isOpen());
            this.channel.close();
            this.closed = true;
        }

        @Override
        public synchronized void mark(final int n) {
            if (this.closed) {
                return;
            }
            this.readlimit = n;
            this.markBuf = ByteBuffer.allocate(n);
            this.marked = true;
            this.reset = false;
        }

        @Override
        public synchronized void reset() throws IOException {
            if (this.closed) {
                return;
            }
            if (!this.marked) {
                throw new IOException("Stream not marked");
            }
            this.marked = false;
            this.reset = true;
            this.markBuf.flip();
        }
    }

    static class WriteStream extends OutputStream {
        SocketChannel channel;
        ByteBuffer buf;
        SelectionKey key;
        boolean closed;
        byte[] one;
        ServerImpl server;

        public WriteStream(final ServerImpl server, final SocketChannel channel) throws IOException {
            this.channel = channel;
            this.server = server;
            assert channel.isBlocking();
            this.closed = false;
            this.one = new byte[1];
            this.buf = ByteBuffer.allocate(4096);
        }

        @Override
        public synchronized void write(final int n) throws IOException {
            this.one[0] = (byte) n;
            this.write(this.one, 0, 1);
        }

        @Override
        public synchronized void write(final byte[] array) throws IOException {
            this.write(array, 0, array.length);
        }

        @Override
        public synchronized void write(final byte[] src, final int offset, final int length) throws IOException {
            int n = length;
            if (this.closed) {
                throw new IOException("stream is closed");
            }
            final int capacity = this.buf.capacity();
            if (capacity < length) {
                this.buf = ByteBuffer.allocate(2 * (capacity + (length - capacity)));
            }
            this.buf.clear();
            this.buf.put(src, offset, length);
            this.buf.flip();
            int write;
            while ((write = this.channel.write(this.buf)) < n) {
                n -= write;
                if (n == 0) {
                    return;
                }
            }
        }

        @Override
        public void close() throws IOException {
            if (this.closed) {
                return;
            }
            this.server.logStackTrace("Request.OS.close: isOpen=" + this.channel.isOpen());
            this.channel.close();
            this.closed = true;
        }
    }
}

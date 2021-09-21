// 
// Decompiled by Procyon v0.5.36
// 

package ir.am3n.needtool.webserver.sun;

import java.io.OutputStream;
import java.io.InputStream;
import javax.net.ssl.SSLEngineResult;
import java.nio.ByteBuffer;
import javax.net.ssl.SSLException;
import ir.am3n.needtool.webserver.HttpsParameters;
import ir.am3n.needtool.webserver.HttpsConfigurator;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Lock;
import javax.net.ssl.SSLEngine;
import java.nio.channels.SocketChannel;
import javax.net.ssl.SSLContext;

class SSLStreams
{
    SSLContext sslctx;
    SocketChannel chan;
    TimeSource time;
    ServerImpl server;
    SSLEngine engine;
    EngineWrapper wrapper;
    OutputStream os;
    InputStream is;
    static long readTimeout;
    static long writeTimeout;
    Lock handshaking;
    int app_buf_size;
    int packet_buf_size;
    
    SSLStreams(final ServerImpl serverImpl, final SSLContext sslctx, final SocketChannel chan) throws IOException {
        this.handshaking = new ReentrantLock();
        this.server = serverImpl;
        this.time = serverImpl;
        this.sslctx = sslctx;
        this.chan = chan;
        final InetSocketAddress inetSocketAddress = (InetSocketAddress)chan.socket().getRemoteSocketAddress();
        (this.engine = sslctx.createSSLEngine(inetSocketAddress.getHostName(), inetSocketAddress.getPort())).setUseClientMode(false);
        this.configureEngine(serverImpl.getHttpsConfigurator(), inetSocketAddress);
        this.wrapper = new EngineWrapper(chan, this.engine);
    }
    
    private void configureEngine(final HttpsConfigurator httpsConfigurator, final InetSocketAddress inetSocketAddress) {
        if (httpsConfigurator != null) {
            final Parameters parameters = new Parameters(httpsConfigurator, inetSocketAddress);
            httpsConfigurator.configure(parameters);
            if (parameters.getCipherSuites() != null) {
                try {
                    this.engine.setEnabledCipherSuites(parameters.getCipherSuites());
                }
                catch (IllegalArgumentException ex) {}
            }
            this.engine.setNeedClientAuth(parameters.getNeedClientAuth());
            this.engine.setWantClientAuth(parameters.getWantClientAuth());
            if (parameters.getProtocols() != null) {
                try {
                    this.engine.setEnabledProtocols(parameters.getProtocols());
                }
                catch (IllegalArgumentException ex2) {}
            }
        }
    }
    
    void close() throws IOException {
        this.wrapper.close();
    }
    
    InputStream getInputStream() throws IOException {
        if (this.is == null) {
            this.is = new InputStream();
        }
        return this.is;
    }
    
    OutputStream getOutputStream() throws IOException {
        if (this.os == null) {
            this.os = new OutputStream();
        }
        return this.os;
    }
    
    SSLEngine getSSLEngine() {
        return this.engine;
    }
    
    void beginHandshake() throws SSLException {
        this.engine.beginHandshake();
    }
    
    private ByteBuffer allocate(final BufType bufType) {
        return this.allocate(bufType, -1);
    }
    
    private ByteBuffer allocate(final BufType bufType, final int n) {
        assert this.engine != null;
        synchronized (this) {
            int capacity;
            if (bufType == BufType.PACKET) {
                if (this.packet_buf_size == 0) {
                    this.packet_buf_size = this.engine.getSession().getPacketBufferSize();
                }
                if (n > this.packet_buf_size) {
                    this.packet_buf_size = n;
                }
                capacity = this.packet_buf_size;
            }
            else {
                if (this.app_buf_size == 0) {
                    this.app_buf_size = this.engine.getSession().getApplicationBufferSize();
                }
                if (n > this.app_buf_size) {
                    this.app_buf_size = n;
                }
                capacity = this.app_buf_size;
            }
            return ByteBuffer.allocate(capacity);
        }
    }
    
    private ByteBuffer realloc(ByteBuffer src, final boolean b, final BufType bufType) {
        synchronized (this) {
            final ByteBuffer allocate = this.allocate(bufType, 2 * src.capacity());
            if (b) {
                src.flip();
            }
            allocate.put(src);
            src = allocate;
        }
        return src;
    }
    
    public WrapperResult sendData(final ByteBuffer byteBuffer) throws IOException {
        WrapperResult wrapAndSend = null;
        while (byteBuffer.remaining() > 0) {
            wrapAndSend = this.wrapper.wrapAndSend(byteBuffer);
            if (wrapAndSend.result.getStatus() == SSLEngineResult.Status.CLOSED) {
                this.doClosure();
                return wrapAndSend;
            }
            final SSLEngineResult.HandshakeStatus handshakeStatus = wrapAndSend.result.getHandshakeStatus();
            if (handshakeStatus == SSLEngineResult.HandshakeStatus.FINISHED || handshakeStatus == SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING) {
                continue;
            }
            this.doHandshake(handshakeStatus);
        }
        return wrapAndSend;
    }
    
    public WrapperResult recvData(ByteBuffer byteBuffer) throws IOException {
        WrapperResult recvAndUnwrap = null;
        assert byteBuffer.position() == 0;
        while (byteBuffer.position() == 0) {
            recvAndUnwrap = this.wrapper.recvAndUnwrap(byteBuffer);
            byteBuffer = ((recvAndUnwrap.buf != byteBuffer) ? recvAndUnwrap.buf : byteBuffer);
            if (recvAndUnwrap.result.getStatus() == SSLEngineResult.Status.CLOSED) {
                this.doClosure();
                return recvAndUnwrap;
            }
            final SSLEngineResult.HandshakeStatus handshakeStatus = recvAndUnwrap.result.getHandshakeStatus();
            if (handshakeStatus == SSLEngineResult.HandshakeStatus.FINISHED || handshakeStatus == SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING) {
                continue;
            }
            this.doHandshake(handshakeStatus);
        }
        byteBuffer.flip();
        return recvAndUnwrap;
    }
    
    void doClosure() throws IOException {
        try {
            this.handshaking.lock();
            final ByteBuffer allocate = this.allocate(BufType.APPLICATION);
            do {
                allocate.clear();
                allocate.flip();
            } while (this.wrapper.wrapAndSendX(allocate, true).result.getStatus() != SSLEngineResult.Status.CLOSED);
        }
        finally {
            this.handshaking.unlock();
        }
    }
    
    void doHandshake(SSLEngineResult.HandshakeStatus handshakeStatus) throws IOException {
        try {
            this.handshaking.lock();
            ByteBuffer byteBuffer = this.allocate(BufType.APPLICATION);
            while (handshakeStatus != SSLEngineResult.HandshakeStatus.FINISHED && handshakeStatus != SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING) {
                WrapperResult wrapperResult = null;
                switch (handshakeStatus) {
                    case NEED_TASK: {
                        Runnable delegatedTask;
                        while ((delegatedTask = this.engine.getDelegatedTask()) != null) {
                            delegatedTask.run();
                        }
                    }
                    case NEED_WRAP: {
                        byteBuffer.clear();
                        byteBuffer.flip();
                        wrapperResult = this.wrapper.wrapAndSend(byteBuffer);
                        break;
                    }
                    case NEED_UNWRAP: {
                        byteBuffer.clear();
                        wrapperResult = this.wrapper.recvAndUnwrap(byteBuffer);
                        if (wrapperResult.buf != byteBuffer) {
                            byteBuffer = wrapperResult.buf;
                        }
                        assert byteBuffer.position() == 0;
                        break;
                    }
                }
                handshakeStatus = wrapperResult.result.getHandshakeStatus();
            }
        }
        finally {
            this.handshaking.unlock();
        }
    }
    
    static {
        SSLStreams.readTimeout = ServerConfig.getReadTimeout();
        SSLStreams.writeTimeout = ServerConfig.getWriteTimeout();
    }
    
    class Parameters extends HttpsParameters
    {
        InetSocketAddress addr;
        HttpsConfigurator cfg;
        
        Parameters(final HttpsConfigurator cfg, final InetSocketAddress addr) {
            this.addr = addr;
            this.cfg = cfg;
        }
        
        @Override
        public InetSocketAddress getClientAddress() {
            return this.addr;
        }
        
        @Override
        public HttpsConfigurator getHttpsConfigurator() {
            return this.cfg;
        }
    }
    
    class WrapperResult
    {
        SSLEngineResult result;
        ByteBuffer buf;
    }
    
    enum BufType
    {
        PACKET, 
        APPLICATION;
    }
    
    class EngineWrapper
    {
        SocketChannel chan;
        SSLEngine engine;
        Object wrapLock;
        Object unwrapLock;
        ByteBuffer unwrap_src;
        ByteBuffer wrap_dst;
        boolean closed;
        int u_remaining;
        
        EngineWrapper(final SocketChannel chan, final SSLEngine engine) throws IOException {
            this.closed = false;
            this.chan = chan;
            this.engine = engine;
            this.wrapLock = new Object();
            this.unwrapLock = new Object();
            this.unwrap_src = SSLStreams.this.allocate(BufType.PACKET);
            this.wrap_dst = SSLStreams.this.allocate(BufType.PACKET);
        }
        
        void close() throws IOException {
        }
        
        WrapperResult wrapAndSend(final ByteBuffer byteBuffer) throws IOException {
            return this.wrapAndSendX(byteBuffer, false);
        }
        
        WrapperResult wrapAndSendX(final ByteBuffer src, final boolean b) throws IOException {
            if (this.closed && !b) {
                throw new IOException("Engine is closed");
            }
            final WrapperResult wrapperResult = new WrapperResult();
            synchronized (this.wrapLock) {
                this.wrap_dst.clear();
                SSLEngineResult.Status status;
                do {
                    wrapperResult.result = this.engine.wrap(src, this.wrap_dst);
                    status = wrapperResult.result.getStatus();
                    if (status == SSLEngineResult.Status.BUFFER_OVERFLOW) {
                        this.wrap_dst = SSLStreams.this.realloc(this.wrap_dst, true, BufType.PACKET);
                    }
                } while (status == SSLEngineResult.Status.BUFFER_OVERFLOW);
                if (status == SSLEngineResult.Status.CLOSED && !b) {
                    this.closed = true;
                    return wrapperResult;
                }
                if (wrapperResult.result.bytesProduced() > 0) {
                    this.wrap_dst.flip();
                    int i = this.wrap_dst.remaining();
                    assert i == wrapperResult.result.bytesProduced();
                    final long n = SSLStreams.this.time.getTime() + SSLStreams.writeTimeout;
                    while (i > 0) {
                        i -= this.chan.write(this.wrap_dst);
                    }
                }
            }
            return wrapperResult;
        }
        
        WrapperResult recvAndUnwrap(final ByteBuffer buf) throws IOException {
            final SSLEngineResult.Status ok = SSLEngineResult.Status.OK;
            final WrapperResult wrapperResult = new WrapperResult();
            wrapperResult.buf = buf;
            if (this.closed) {
                throw new IOException("Engine is closed");
            }
            int n;
            if (this.u_remaining > 0) {
                this.unwrap_src.compact();
                this.unwrap_src.flip();
                n = 0;
            }
            else {
                this.unwrap_src.clear();
                n = 1;
            }
            synchronized (this.unwrapLock) {
                SSLEngineResult.Status status;
                do {
                    if (n != 0) {
                        int i;
                        do {
                            i = this.chan.read(this.unwrap_src);
                        } while (i == 0);
                        if (i == -1) {
                            throw new IOException("connection closed for reading");
                        }
                        this.unwrap_src.flip();
                    }
                    wrapperResult.result = this.engine.unwrap(this.unwrap_src, wrapperResult.buf);
                    status = wrapperResult.result.getStatus();
                    if (status == SSLEngineResult.Status.BUFFER_UNDERFLOW) {
                        if (this.unwrap_src.limit() == this.unwrap_src.capacity()) {
                            this.unwrap_src = SSLStreams.this.realloc(this.unwrap_src, false, BufType.PACKET);
                        }
                        else {
                            this.unwrap_src.position(this.unwrap_src.limit());
                            this.unwrap_src.limit(this.unwrap_src.capacity());
                        }
                        n = 1;
                    }
                    else if (status == SSLEngineResult.Status.BUFFER_OVERFLOW) {
                        wrapperResult.buf = SSLStreams.this.realloc(wrapperResult.buf, true, BufType.APPLICATION);
                        n = 0;
                    }
                    else {
                        if (status == SSLEngineResult.Status.CLOSED) {
                            this.closed = true;
                            wrapperResult.buf.flip();
                            return wrapperResult;
                        }
                        continue;
                    }
                } while (status != SSLEngineResult.Status.OK);
            }
            this.u_remaining = this.unwrap_src.remaining();
            return wrapperResult;
        }
    }
    
    class InputStream extends java.io.InputStream
    {
        ByteBuffer bbuf;
        boolean closed;
        boolean eof;
        boolean needData;
        byte[] single;
        
        InputStream() {
            this.closed = false;
            this.eof = false;
            this.needData = true;
            this.single = new byte[1];
            this.bbuf = SSLStreams.this.allocate(BufType.APPLICATION);
        }
        
        @Override
        public int read(final byte[] dst, final int offset, int length) throws IOException {
            if (this.closed) {
                throw new IOException("SSL stream is closed");
            }
            if (this.eof) {
                return 0;
            }
            int n = 0;
            if (!this.needData) {
                n = this.bbuf.remaining();
                this.needData = (n == 0);
            }
            if (this.needData) {
                this.bbuf.clear();
                final WrapperResult recvData = SSLStreams.this.recvData(this.bbuf);
                this.bbuf = ((recvData.buf == this.bbuf) ? this.bbuf : recvData.buf);
                if ((n = this.bbuf.remaining()) == 0) {
                    this.eof = true;
                    return 0;
                }
                this.needData = false;
            }
            if (length > n) {
                length = n;
            }
            this.bbuf.get(dst, offset, length);
            return length;
        }
        
        @Override
        public int available() throws IOException {
            return this.bbuf.remaining();
        }
        
        @Override
        public boolean markSupported() {
            return false;
        }
        
        @Override
        public void reset() throws IOException {
            throw new IOException("mark/reset not supported");
        }
        
        @Override
        public long skip(final long n) throws IOException {
            int i = (int)n;
            if (this.closed) {
                throw new IOException("SSL stream is closed");
            }
            if (this.eof) {
                return 0L;
            }
            final int n2 = i;
            while (i > 0) {
                if (this.bbuf.remaining() >= i) {
                    this.bbuf.position(this.bbuf.position() + i);
                    return n2;
                }
                i -= this.bbuf.remaining();
                this.bbuf.clear();
                final WrapperResult recvData = SSLStreams.this.recvData(this.bbuf);
                this.bbuf = ((recvData.buf == this.bbuf) ? this.bbuf : recvData.buf);
            }
            return n2;
        }
        
        @Override
        public void close() throws IOException {
            this.eof = true;
            SSLStreams.this.engine.closeInbound();
        }
        
        @Override
        public int read(final byte[] array) throws IOException {
            return this.read(array, 0, array.length);
        }
        
        @Override
        public int read() throws IOException {
            if (this.read(this.single, 0, 1) == 0) {
                return -1;
            }
            return this.single[0] & 0xFF;
        }
    }
    
    class OutputStream extends java.io.OutputStream
    {
        ByteBuffer buf;
        boolean closed;
        byte[] single;
        
        OutputStream() {
            this.closed = false;
            this.single = new byte[1];
            this.buf = SSLStreams.this.allocate(BufType.APPLICATION);
        }
        
        @Override
        public void write(final int n) throws IOException {
            this.single[0] = (byte)n;
            this.write(this.single, 0, 1);
        }
        
        @Override
        public void write(final byte[] array) throws IOException {
            this.write(array, 0, array.length);
        }
        
        @Override
        public void write(final byte[] src, int offset, int i) throws IOException {
            if (this.closed) {
                throw new IOException("output stream is closed");
            }
            while (i > 0) {
                final int length = (i > this.buf.capacity()) ? this.buf.capacity() : i;
                this.buf.clear();
                this.buf.put(src, offset, length);
                i -= length;
                offset += length;
                this.buf.flip();
                if (SSLStreams.this.sendData(this.buf).result.getStatus() == SSLEngineResult.Status.CLOSED) {
                    this.closed = true;
                    if (i > 0) {
                        throw new IOException("output stream is closed");
                    }
                    continue;
                }
            }
        }
        
        @Override
        public void flush() throws IOException {
        }
        
        @Override
        public void close() throws IOException {
            WrapperResult wrapAndSend = null;
            SSLStreams.this.engine.closeOutbound();
            this.closed = true;
            SSLEngineResult.HandshakeStatus handshakeStatus = SSLEngineResult.HandshakeStatus.NEED_WRAP;
            this.buf.clear();
            while (handshakeStatus == SSLEngineResult.HandshakeStatus.NEED_WRAP) {
                wrapAndSend = SSLStreams.this.wrapper.wrapAndSend(this.buf);
                handshakeStatus = wrapAndSend.result.getHandshakeStatus();
            }
            assert wrapAndSend.result.getStatus() == SSLEngineResult.Status.CLOSED;
        }
    }
}

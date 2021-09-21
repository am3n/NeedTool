// 
// Decompiled by Procyon v0.5.36
// 

package ir.am3n.needtool.webserver.sun;

import ir.am3n.needtool.webserver.Headers;
import javax.net.ssl.SSLEngine;
import java.net.URISyntaxException;
import ir.am3n.needtool.webserver.HttpExchange;
import ir.am3n.needtool.webserver.Filter;
import java.net.URI;
import java.io.BufferedInputStream;
import java.io.OutputStream;
import java.io.InputStream;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SocketChannel;
import java.util.logging.Level;
import ir.am3n.needtool.webserver.HttpContext;
import ir.am3n.needtool.webserver.HttpHandler;
import java.util.Iterator;
import java.net.BindException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.TimerTask;
import java.util.Collections;
import java.util.HashSet;
import java.net.SocketAddress;
import java.util.logging.Logger;
import java.util.Timer;
import ir.am3n.needtool.webserver.HttpServer;
import java.util.List;
import java.util.Set;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.net.InetSocketAddress;
import javax.net.ssl.SSLContext;
import ir.am3n.needtool.webserver.HttpsConfigurator;
import java.util.concurrent.Executor;

class ServerImpl implements TimeSource
{
    private String protocol;
    private boolean https;
    private Executor executor;
    private HttpsConfigurator httpsConfig;
    private SSLContext sslContext;
    private ContextList contexts;
    private InetSocketAddress address;
    private ServerSocketChannel schan;
    private Selector selector;
    private SelectionKey listenerKey;
    private Set<HttpConnection> idleConnections;
    private Set<HttpConnection> allConnections;
    private Set<HttpConnection> newConnections;
    private Set<HttpConnection> rspConnections;
    private List<Event> events;
    private Object lolock;
    private volatile boolean finished;
    private volatile boolean terminating;
    private boolean bound;
    private boolean started;
    private volatile long time;
    private volatile long subticks;
    private volatile long ticks;
    private HttpServer wrapper;
    static final int CLOCK_TICK;
    static final long IDLE_INTERVAL;
    static final int MAX_IDLE_CONNECTIONS;
    static final long TIMER_MILLIS;
    static final long MAX_REQ_TIME;
    static final long MAX_RSP_TIME;
    static final boolean timer1Enabled;
    private Timer timer;
    private Timer timer1;
    private Logger logger;
    Dispatcher dispatcher;
    static boolean debug;
    private int exchangeCount;
    
    ServerImpl(final HttpServer wrapper, final String s, final InetSocketAddress obj, final int backlog) throws IOException {
        this.lolock = new Object();
        this.finished = false;
        this.terminating = false;
        this.bound = false;
        this.started = false;
        this.subticks = 0L;
        this.exchangeCount = 0;
        this.protocol = s;
        this.wrapper = wrapper;
        this.logger = Logger.getLogger("ir.am3n.needtool.webserver");
        this.https = s.equalsIgnoreCase("https");
        this.address = obj;
        this.contexts = new ContextList();
        this.schan = ServerSocketChannel.open();
        if (obj != null) {
            this.schan.socket().bind(obj, backlog);
            this.bound = true;
        }
        this.selector = Selector.open();
        this.schan.configureBlocking(false);
        this.listenerKey = this.schan.register(this.selector, 16);
        this.dispatcher = new Dispatcher();
        this.idleConnections = Collections.synchronizedSet(new HashSet<HttpConnection>());
        this.allConnections = Collections.synchronizedSet(new HashSet<HttpConnection>());
        this.newConnections = Collections.synchronizedSet(new HashSet<HttpConnection>());
        this.rspConnections = Collections.synchronizedSet(new HashSet<HttpConnection>());
        this.time = System.currentTimeMillis();
        (this.timer = new Timer("server-timer", true)).schedule(new ServerTimerTask(), ServerImpl.CLOCK_TICK, ServerImpl.CLOCK_TICK);
        if (ServerImpl.timer1Enabled) {
            (this.timer1 = new Timer("server-timer1", true)).schedule(new ServerTimerTask1(), ServerImpl.TIMER_MILLIS, ServerImpl.TIMER_MILLIS);
        }
        this.events = new LinkedList<Event>();
        this.logger.config("HttpServer created " + s + " " + obj);
    }
    
    public void bind(final InetSocketAddress endpoint, final int backlog) throws IOException {
        if (this.bound) {
            throw new BindException("HttpServer already bound");
        }
        if (endpoint == null) {
            throw new NullPointerException("null address");
        }
        this.schan.socket().bind(endpoint, backlog);
        this.bound = true;
    }
    
    public void start() {
        if (!this.bound || this.started || this.finished) {
            throw new IllegalStateException("server in wrong state");
        }
        if (this.executor == null) {
            this.executor = new DefaultExecutor();
        }
        final Thread thread = new Thread(this.dispatcher);
        this.started = true;
        thread.start();
    }
    
    public void setExecutor(final Executor executor) {
        if (this.started) {
            throw new IllegalStateException("server already started");
        }
        this.executor = executor;
    }
    
    public Executor getExecutor() {
        return this.executor;
    }
    
    public void setHttpsConfigurator(final HttpsConfigurator httpsConfig) {
        if (httpsConfig == null) {
            throw new NullPointerException("null HttpsConfigurator");
        }
        if (this.started) {
            throw new IllegalStateException("server already started");
        }
        this.httpsConfig = httpsConfig;
        this.sslContext = httpsConfig.getSSLContext();
    }
    
    public HttpsConfigurator getHttpsConfigurator() {
        return this.httpsConfig;
    }
    
    public void stop(final int n) {
        if (n < 0) {
            throw new IllegalArgumentException("negative delay parameter");
        }
        this.terminating = true;
        try {
            this.schan.close();
        }
        catch (IOException ex) {}
        this.selector.wakeup();
        while (System.currentTimeMillis() < System.currentTimeMillis() + n * 1000) {
            this.delay();
            if (this.finished) {
                break;
            }
        }
        this.finished = true;
        this.selector.wakeup();
        synchronized (this.allConnections) {
            final Iterator<HttpConnection> iterator = this.allConnections.iterator();
            while (iterator.hasNext()) {
                iterator.next().close();
            }
        }
        this.allConnections.clear();
        this.idleConnections.clear();
        this.timer.cancel();
        if (ServerImpl.timer1Enabled) {
            this.timer1.cancel();
        }
    }
    
    public synchronized HttpContextImpl createContext(final String str, final HttpHandler httpHandler) {
        if (httpHandler == null || str == null) {
            throw new NullPointerException("null handler, or path parameter");
        }
        final HttpContextImpl httpContextImpl = new HttpContextImpl(this.protocol, str, httpHandler, this);
        this.contexts.add(httpContextImpl);
        this.logger.config("context created: " + str);
        return httpContextImpl;
    }
    
    public synchronized HttpContextImpl createContext(final String str) {
        if (str == null) {
            throw new NullPointerException("null path parameter");
        }
        final HttpContextImpl httpContextImpl = new HttpContextImpl(this.protocol, str, null, this);
        this.contexts.add(httpContextImpl);
        this.logger.config("context created: " + str);
        return httpContextImpl;
    }
    
    public synchronized void removeContext(final String str) throws IllegalArgumentException {
        if (str == null) {
            throw new NullPointerException("null path parameter");
        }
        this.contexts.remove(this.protocol, str);
        this.logger.config("context removed: " + str);
    }
    
    public synchronized void removeContext(final HttpContext httpContext) throws IllegalArgumentException {
        if (!(httpContext instanceof HttpContextImpl)) {
            throw new IllegalArgumentException("wrong HttpContext type");
        }
        this.contexts.remove((HttpContextImpl)httpContext);
        this.logger.config("context removed: " + httpContext.getPath());
    }
    
    public InetSocketAddress getAddress() {
        return (InetSocketAddress)this.schan.socket().getLocalSocketAddress();
    }
    
    Selector getSelector() {
        return this.selector;
    }
    
    void addEvent(final Event event) {
        synchronized (this.lolock) {
            this.events.add(event);
            this.selector.wakeup();
        }
    }
    
    int resultSize() {
        synchronized (this.lolock) {
            return this.events.size();
        }
    }
    
    static synchronized void dprint(final String x) {
        if (ServerImpl.debug) {
            System.out.println(x);
        }
    }
    
    static synchronized void dprint(final Exception x) {
        if (ServerImpl.debug) {
            System.out.println(x);
            x.printStackTrace();
        }
    }
    
    Logger getLogger() {
        return this.logger;
    }
    
    void logReply(final int i, final String s, String str) {
        if (str == null) {
            str = "";
        }
        String string;
        if (s.length() > 80) {
            string = s.substring(0, 80) + "<TRUNCATED>";
        }
        else {
            string = s;
        }
        this.logger.fine(string + " [" + i + " " + Code.msg(i) + "] (" + str + ")");
    }
    
    long getTicks() {
        return this.ticks;
    }
    
    public long getTime() {
        return this.time;
    }
    
    void delay() {
        Thread.yield();
        try {
            Thread.sleep(200L);
        }
        catch (InterruptedException ex) {}
    }
    
    synchronized void startExchange() {
        ++this.exchangeCount;
    }
    
    synchronized int endExchange() {
        --this.exchangeCount;
        assert this.exchangeCount >= 0;
        return this.exchangeCount;
    }
    
    HttpServer getWrapper() {
        return this.wrapper;
    }
    
    void requestStarted(final HttpConnection httpConnection) {
        if (ServerImpl.MAX_REQ_TIME > 0L) {
            httpConnection.creationTime = this.getTime();
        }
    }
    
    void requestCompleted(final HttpConnection httpConnection) {
        this.newConnections.remove(httpConnection);
    }
    
    void responseStarted(final HttpConnection httpConnection) {
        if (ServerImpl.MAX_RSP_TIME > 0L) {
            httpConnection.rspStartedTime = this.getTime();
            this.rspConnections.add(httpConnection);
        }
    }
    
    void responseCompleted(final HttpConnection httpConnection) {
        if (ServerImpl.MAX_RSP_TIME > 0L && httpConnection.rspStartedTime != 0L) {
            this.rspConnections.remove(httpConnection);
        }
    }
    
    void logStackTrace(String string) {
        this.logger.finest(string);
        string = "";
        final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (int i = 0; i < stackTrace.length; ++i) {
            string = string + stackTrace[i].toString() + "\n";
        }
        this.logger.finest(string);
    }
    
    static {
        CLOCK_TICK = ServerConfig.getClockTick();
        IDLE_INTERVAL = ServerConfig.getIdleInterval();
        MAX_IDLE_CONNECTIONS = ServerConfig.getMaxIdleConnections();
        TIMER_MILLIS = ServerConfig.getTimerMillis();
        MAX_REQ_TIME = ServerConfig.getMaxReqTime() * 1000L;
        MAX_RSP_TIME = ServerConfig.getMaxRspTime() * 1000L;
        timer1Enabled = (ServerImpl.MAX_REQ_TIME + ServerImpl.MAX_RSP_TIME > 0L);
        ServerImpl.debug = ServerConfig.debugEnabled();
    }
    
    private static class DefaultExecutor implements Executor
    {
        public void execute(final Runnable runnable) {
            runnable.run();
        }
    }
    
    class Dispatcher implements Runnable
    {
        private void handleEvent(final Event event) {
            final ExchangeImpl exchange = event.exchange;
            final HttpConnection connection = exchange.getConnection();
            try {
                if (event instanceof WriteFinishedEvent) {
                    final int endExchange = ServerImpl.this.endExchange();
                    if (ServerImpl.this.terminating && endExchange == 0) {
                        ServerImpl.this.finished = true;
                    }
                    ServerImpl.this.responseCompleted(connection);
                    final SocketChannel channel = connection.getChannel();
                    final LeftOverInputStream originalInputStream = exchange.getOriginalInputStream();
                    if (!originalInputStream.isEOF()) {
                        exchange.close = true;
                    }
                    if (exchange.close || ServerImpl.this.idleConnections.size() >= ServerImpl.MAX_IDLE_CONNECTIONS) {
                        connection.close();
                        ServerImpl.this.allConnections.remove(connection);
                    }
                    else if (originalInputStream.isDataBuffered()) {
                        ServerImpl.this.requestStarted(connection);
                        this.handle(connection.getChannel(), connection);
                    }
                    else {
                        channel.configureBlocking(false);
                        final SelectionKey register = channel.register(ServerImpl.this.selector, 1);
                        register.interestOps(1);
                        register.attach(connection);
                        connection.selectionKey = register;
                        connection.time = ServerImpl.this.getTime() + ServerImpl.IDLE_INTERVAL;
                        ServerImpl.this.idleConnections.add(connection);
                    }
                }
            }
            catch (IOException thrown) {
                ServerImpl.this.logger.log(Level.FINER, "Dispatcher (1)", thrown);
                connection.close();
            }
        }
        
        public void run() {
            while (!ServerImpl.this.finished) {
                try {
                    ServerImpl.this.selector.select(1000L);
                    while (ServerImpl.this.resultSize() > 0) {
                        synchronized (ServerImpl.this.lolock) {
                            this.handleEvent(ServerImpl.this.events.remove(0));
                        }
                    }
                    final Iterator<SelectionKey> iterator = ServerImpl.this.selector.selectedKeys().iterator();
                    while (iterator.hasNext()) {
                        final SelectionKey selectionKey = iterator.next();
                        iterator.remove();
                        if (selectionKey.equals(ServerImpl.this.listenerKey)) {
                            if (ServerImpl.this.terminating) {
                                continue;
                            }
                            final SocketChannel accept = ServerImpl.this.schan.accept();
                            if (accept == null) {
                                continue;
                            }
                            accept.configureBlocking(false);
                            final SelectionKey register = accept.register(ServerImpl.this.selector, 1);
                            final HttpConnection ob = new HttpConnection();
                            ob.selectionKey = register;
                            ob.setChannel(accept);
                            register.attach(ob);
                            ServerImpl.this.allConnections.add(ob);
                            ServerImpl.this.requestStarted(ob);
                        }
                        else {
                            try {
                                if (selectionKey.isReadable()) {
                                    final SocketChannel socketChannel = (SocketChannel)selectionKey.channel();
                                    final HttpConnection httpConnection = (HttpConnection)selectionKey.attachment();
                                    selectionKey.interestOps(0);
                                    ServerImpl.this.requestStarted(httpConnection);
                                    this.handle(socketChannel, httpConnection);
                                }
                                else {
                                    assert false;
                                    continue;
                                }
                            }
                            catch (IOException thrown) {
                                final HttpConnection httpConnection2 = (HttpConnection)selectionKey.attachment();
                                ServerImpl.this.logger.log(Level.FINER, "Dispatcher (2)", thrown);
                                httpConnection2.close();
                            }
                        }
                    }
                }
                catch (CancelledKeyException thrown2) {
                    ServerImpl.this.logger.log(Level.FINER, "Dispatcher (3)", thrown2);
                }
                catch (IOException thrown3) {
                    ServerImpl.this.logger.log(Level.FINER, "Dispatcher (4)", thrown3);
                }
                catch (Exception thrown4) {
                    ServerImpl.this.logger.log(Level.FINER, "Dispatcher (7)", thrown4);
                }
            }
        }
        
        public void handle(final SocketChannel socketChannel, final HttpConnection httpConnection) throws IOException {
            try {
                ServerImpl.this.executor.execute(new Exchange(socketChannel, ServerImpl.this.protocol, httpConnection));
            }
            catch (HttpError thrown) {
                ServerImpl.this.logger.log(Level.FINER, "Dispatcher (5)", thrown);
                httpConnection.close();
            }
            catch (IOException thrown2) {
                ServerImpl.this.logger.log(Level.FINER, "Dispatcher (6)", thrown2);
                httpConnection.close();
            }
        }
    }
    
    class Exchange implements Runnable
    {
        SocketChannel chan;
        HttpConnection connection;
        HttpContextImpl context;
        InputStream rawin;
        OutputStream rawout;
        String protocol;
        ExchangeImpl tx;
        HttpContextImpl ctx;
        boolean rejected;
        
        Exchange(final SocketChannel chan, final String protocol, final HttpConnection connection) throws IOException {
            this.rejected = false;
            this.chan = chan;
            this.connection = connection;
            this.protocol = protocol;
        }
        
        public void run() {
            this.context = this.connection.getHttpContext();
            SSLEngine sslEngine = null;
            String requestLine = null;
            SSLStreams sslStreams = null;
            try {
                boolean b;
                if (this.context != null) {
                    this.rawin = this.connection.getInputStream();
                    this.rawout = this.connection.getRawOutputStream();
                    b = false;
                }
                else {
                    b = true;
                    this.connection.selectionKey.cancel();
                    this.chan.configureBlocking(true);
                    if (ServerImpl.this.https) {
                        if (ServerImpl.this.sslContext == null) {
                            ServerImpl.this.logger.warning("SSL connection received. No https contxt created");
                            throw new HttpError("No SSL context established");
                        }
                        sslStreams = new SSLStreams(ServerImpl.this, ServerImpl.this.sslContext, this.chan);
                        this.rawin = sslStreams.getInputStream();
                        this.rawout = sslStreams.getOutputStream();
                        sslEngine = sslStreams.getSSLEngine();
                    }
                    else {
                        this.rawin = new BufferedInputStream(new Request.ReadStream(ServerImpl.this, this.chan));
                        this.rawout = new Request.WriteStream(ServerImpl.this, this.chan);
                    }
                }
                final Request request = new Request(this.rawin, this.rawout);
                requestLine = request.requestLine();
                if (requestLine == null) {
                    this.connection.close();
                    return;
                }
                final int index = requestLine.indexOf(32);
                if (index == -1) {
                    this.reject(400, requestLine, "Bad request line");
                    return;
                }
                final String substring = requestLine.substring(0, index);
                final int n = index + 1;
                final int index2 = requestLine.indexOf(32, n);
                if (index2 == -1) {
                    this.reject(400, requestLine, "Bad request line");
                    return;
                }
                final URI uri = new URI(requestLine.substring(n, index2));
                requestLine.substring(index2 + 1);
                final Headers headers = request.headers();
                final String first = headers.getFirst("Transfer-encoding");
                int int1 = 0;
                if (first != null && first.equalsIgnoreCase("chunked")) {
                    int1 = -1;
                }
                else {
                    final String first2 = headers.getFirst("Content-Length");
                    if (first2 != null) {
                        int1 = Integer.parseInt(first2);
                    }
                    if (int1 == 0) {
                        ServerImpl.this.requestCompleted(this.connection);
                    }
                }
                this.ctx = ServerImpl.this.contexts.findContext(this.protocol, uri.getPath());
                if (this.ctx == null) {
                    this.reject(404, requestLine, "No context found for request");
                    return;
                }
                this.connection.setContext(this.ctx);
                if (this.ctx.getHandler() == null) {
                    this.reject(500, requestLine, "No handler for context");
                    return;
                }
                this.tx = new ExchangeImpl(substring, uri, request, int1, this.connection);
                final String first3 = headers.getFirst("Connection");
                if (first3 != null && first3.equalsIgnoreCase("close")) {
                    this.tx.close = true;
                }
                if (b) {
                    this.connection.setParameters(this.rawin, this.rawout, this.chan, sslEngine, sslStreams, ServerImpl.this.sslContext, this.protocol, this.ctx, this.rawin);
                }
                final String first4 = headers.getFirst("Expect");
                if (first4 != null && first4.equalsIgnoreCase("100-continue")) {
                    ServerImpl.this.logReply(100, requestLine, null);
                    this.sendReply(100, false, null);
                }
                final Filter.Chain chain = new Filter.Chain(this.ctx.getFilters(), new LinkHandler(new Filter.Chain(this.ctx.getSystemFilters(), this.ctx.getHandler())));
                this.tx.getRequestBody();
                this.tx.getResponseBody();
                if (ServerImpl.this.https) {
                    chain.doFilter(new HttpsExchangeImpl(this.tx));
                }
                else {
                    chain.doFilter(new HttpExchangeImpl(this.tx));
                }
            }
            catch (IOException thrown) {
                ServerImpl.this.logger.log(Level.FINER, "ServerImpl.Exchange (1)", thrown);
                this.connection.close();
            }
            catch (NumberFormatException ex) {
                this.reject(400, requestLine, "NumberFormatException thrown");
            }
            catch (URISyntaxException ex2) {
                this.reject(400, requestLine, "URISyntaxException thrown");
            }
            catch (Exception thrown2) {
                ServerImpl.this.logger.log(Level.FINER, "ServerImpl.Exchange (2)", thrown2);
                this.connection.close();
            }
        }
        
        void reject(final int i, final String s, final String str) {
            this.rejected = true;
            ServerImpl.this.logReply(i, s, str);
            this.sendReply(i, true, "<h1>" + i + Code.msg(i) + "</h1>" + str);
        }
        
        void sendReply(final int i, final boolean b, String str) {
            try {
                final String string = "HTTP/1.1 " + i + Code.msg(i) + "\r\n";
                String s;
                if (str != null && str.length() != 0) {
                    s = string + "Content-Length: " + str.length() + "\r\n" + "Content-Type: text/html\r\n";
                }
                else {
                    s = string + "Content-Length: 0\r\n";
                    str = "";
                }
                if (b) {
                    s += "Connection: close\r\n";
                }
                this.rawout.write((s + "\r\n" + str).getBytes("ISO8859_1"));
                this.rawout.flush();
                if (b) {
                    this.connection.close();
                }
            }
            catch (IOException thrown) {
                ServerImpl.this.logger.log(Level.FINER, "ServerImpl.sendReply", thrown);
                this.connection.close();
            }
        }
        
        class LinkHandler implements HttpHandler
        {
            Filter.Chain nextChain;
            
            LinkHandler(final Filter.Chain nextChain) {
                this.nextChain = nextChain;
            }
            
            public void handle(final HttpExchange httpExchange) throws IOException {
                this.nextChain.doFilter(httpExchange);
            }
        }
    }
    
    class ServerTimerTask extends TimerTask
    {
        @Override
        public void run() {
            final LinkedList<HttpConnection> list = new LinkedList<HttpConnection>();
            ServerImpl.this.time = System.currentTimeMillis();
            ServerImpl.this.ticks++;
            synchronized (ServerImpl.this.idleConnections) {
                for (final HttpConnection e : ServerImpl.this.idleConnections) {
                    if (e.time <= ServerImpl.this.time) {
                        list.add(e);
                    }
                }
                for (final HttpConnection httpConnection : list) {
                    ServerImpl.this.idleConnections.remove(httpConnection);
                    ServerImpl.this.allConnections.remove(httpConnection);
                    httpConnection.close();
                }
            }
        }
    }
    
    class ServerTimerTask1 extends TimerTask
    {
        @Override
        public void run() {
            final LinkedList<HttpConnection> list = new LinkedList<HttpConnection>();
            ServerImpl.this.time = System.currentTimeMillis();
            synchronized (ServerImpl.this.newConnections) {
                for (final HttpConnection e : ServerImpl.this.newConnections) {
                    if (e.creationTime + ServerImpl.TIMER_MILLIS + ServerImpl.MAX_REQ_TIME <= ServerImpl.this.time) {
                        list.add(e);
                    }
                }
                for (final HttpConnection obj : list) {
                    ServerImpl.this.logger.log(Level.FINE, "closing: no request: " + obj);
                    ServerImpl.this.newConnections.remove(obj);
                    ServerImpl.this.allConnections.remove(obj);
                    obj.close();
                }
            }
            final LinkedList<HttpConnection> list2 = new LinkedList<HttpConnection>();
            synchronized (ServerImpl.this.rspConnections) {
                for (final HttpConnection e2 : ServerImpl.this.rspConnections) {
                    if (e2.rspStartedTime + ServerImpl.TIMER_MILLIS + ServerImpl.MAX_RSP_TIME <= ServerImpl.this.time) {
                        list2.add(e2);
                    }
                }
                for (final HttpConnection obj2 : list2) {
                    ServerImpl.this.logger.log(Level.FINE, "closing: no response: " + obj2);
                    ServerImpl.this.rspConnections.remove(obj2);
                    ServerImpl.this.allConnections.remove(obj2);
                    obj2.close();
                }
            }
        }
    }
}

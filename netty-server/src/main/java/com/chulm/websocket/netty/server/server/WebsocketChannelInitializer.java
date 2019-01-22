package com.chulm.websocket.netty.server.server;

import com.chulm.websocket.netty.server.handler.PreflightHandler;
import com.chulm.websocket.netty.server.handler.RouterHandler;
import com.chulm.websocket.netty.server.handler.TransportHandler;
import com.chulm.websocket.netty.server.handler.transports.SockJsTransport;
import com.chulm.websocket.netty.server.handler.transports.WebSocketV1Transport;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ChannelHandler.Sharable
@Slf4j
@Data
public class WebsocketChannelInitializer extends ChannelInitializer<SocketChannel> {

    private String wsPath;
    private long timeout;
    private long port;

    private boolean isHttps = false;
    private String certPath = null;
    private String certPassword = null;


    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        log.info("initializer Handler" + channel);

        ChannelPipeline pipeline = channel.pipeline();
        pipeline.addLast("loggingHandler", new LoggingHandler(LogLevel.DEBUG));

        if (isHttps()) {
            SSLEngine engine = getServerSSLContext(getCertPath(), getCertPassword()).createSSLEngine();

            String enabledCipherSuites[] = engine.getEnabledCipherSuites();
            List<String> cipherSuites = new ArrayList<String>(Arrays.asList(enabledCipherSuites));
            for (String cipher : enabledCipherSuites) {
                if (cipher.contains("DHE")) {
                    cipherSuites.remove(cipher);
                }
            }
            enabledCipherSuites = (String[]) cipherSuites.toArray(new String[cipherSuites.size()]);
            engine.setEnabledCipherSuites(enabledCipherSuites);

            engine.setUseClientMode(false);
            pipeline.addLast("ssl", new SslHandler(engine));
        }

        pipeline.addLast("httpResponseEncoder", new HttpResponseEncoder());
        pipeline.addLast("httpRequestDecoder", new HttpRequestDecoder());
        pipeline.addLast("httpObjectAggregator", new HttpObjectAggregator(1048576));
        pipeline.addLast("chunkedWriteHandler", new ChunkedWriteHandler());
        pipeline.addLast("preflightHandler", new PreflightHandler());
        pipeline.addLast("routerHandler", new RouterHandler(wsPath, isHttps,configureTransport()));
        pipeline.addLast("transportHandler", new TransportHandler(configureTransport()));
    }


    private SockJsTransport configureTransport() {

        SockJsTransport transport = new WebSocketV1Transport();
        return transport;

    }

    public SSLContext getServerSSLContext(String path, String password) throws KeyStoreException, IOException, KeyManagementException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException {
        String algorithm = Security.getProperty("ssl.KeyManagerFactory.algorithm");
        if (algorithm == null) {
            algorithm = "SunX509";
        }
        SSLContext serverContext = null;
        KeyStore ks;
        ks = KeyStore.getInstance("JKS");
        ks.load(new FileInputStream(path), password.toCharArray());

        KeyManagerFactory kmf = KeyManagerFactory.getInstance(algorithm);
        kmf.init(ks, password.toCharArray());

        // Initialize the SSLContext to work with our key managers.
        serverContext = SSLContext.getInstance("TLS");
        serverContext.init(kmf.getKeyManagers(), null, null);

        return serverContext;
    }
}

package cn.xiejx.ddtassistant.utils.http;

import cn.xiejx.ddtassistant.utils.http.enumeration.MethodEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.List;

/**
 * there is introduction
 *
 * @author XJX
 * @date 2021/1/31 2:41
 */
@Slf4j
public class HttpHelper implements Serializable {
    private static final long serialVersionUID = 7802931388945369325L;

    private final CloseableHttpClient httpClient;
    private final HttpRequestMaker httpRequestMaker;
    private final CookieStore httpCookieStore;
    private HttpClientContext context;
    private HttpHost httpHost;

    private static final X509TrustManager X509MGR = new X509TrustManager() {
        @Override
        public void checkClientTrusted(X509Certificate[] xcs, String string) {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] xcs, String string) {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    };

    public HttpHelper(HttpRequestMaker httpRequestMaker) {
        this.httpRequestMaker = httpRequestMaker;
        httpCookieStore = new BasicCookieStore();
        SSLConnectionSocketFactory sslConnectionSocketFactory = null;
        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, (TrustStrategy) (chain, authType) -> true).build();
            sslContext.init(null, new TrustManager[]{X509MGR}, null);
            HostnameVerifier hostnameVerifier = NoopHostnameVerifier.INSTANCE;
            sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContext, hostnameVerifier);
        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
            log.error(e.getMessage(), e);
        }

        this.httpClient = HttpClientBuilder.create().setSSLSocketFactory(sslConnectionSocketFactory).setDefaultRequestConfig(RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build()).setDefaultCookieStore(httpCookieStore).build();
    }

    public HttpHelper(String url, MethodEnum methodEnum) {
        this(HttpRequestMaker.makeHttpHelper(url, methodEnum));
    }

    public HttpHelper(String url) {
        this(url, MethodEnum.METHOD_GET);
    }

    public static HttpHelper makeDefaultGetHttpHelper(String url) {
        return makeDefaultTimeoutHttpHelper(url, MethodEnum.METHOD_GET, null);
    }

    public static HttpHelper makeDefaultTimeoutHttpHelper(String url, MethodEnum methodEnum) {
        return makeDefaultTimeoutHttpHelper(url, methodEnum, null);
    }

    public static HttpHelper makeDefaultTimeoutHttpHelper(String url, MethodEnum methodEnum, int port) {
        HttpRequestMaker httpRequestMaker = HttpRequestMaker.makeHttpHelper(url, methodEnum);
        httpRequestMaker.setHttpHost(new HttpHost("127.0.0.1", port, HttpHost.DEFAULT_SCHEME_NAME));
        return new HttpHelper(httpRequestMaker.buildDefaultTimeoutConfig());
    }

    public static HttpHelper makeDefaultTimeoutHttpHelper(String url, MethodEnum methodEnum, HttpHost httpHost) {
        HttpRequestMaker httpRequestMaker = HttpRequestMaker.makeHttpHelper(url, methodEnum);
        httpRequestMaker.setHttpHost(httpHost);
        return new HttpHelper(httpRequestMaker.buildDefaultTimeoutConfig());
    }

    public static HttpHelper makeDefaultTimeoutHttpHelper(HttpRequestMaker httpRequestMaker) {
        if (httpRequestMaker == null) {
            return null;
        }
        return new HttpHelper(httpRequestMaker.buildDefaultTimeoutConfig());
    }

    public static HttpHelper makeHttpHelper(HttpRequestMaker httpRequestMaker) {
        return new HttpHelper(httpRequestMaker);
    }

    public HttpClientContext getContext() {
        return context;
    }

    public HttpHost getHttpHost() {
        return httpHost;
    }

    public void setHttpHost(HttpHost httpHost) {
        this.httpHost = httpHost;
    }

    public void setUa(String ua) {
        setHeader("user-agent", ua);
    }

    public void setHeader(String header, String value) {
        httpRequestMaker.setHeader(header, value);
    }

    public Header[] getHeader() {
        return httpRequestMaker.getAllHeaders();
    }

    public void setUrlEncodedFormPostBody(List<NameValuePair> pairs) {
        if (CollectionUtils.isEmpty(pairs)) {
            return;
        }

        UrlEncodedFormEntity httpEntity = new UrlEncodedFormEntity(pairs, StandardCharsets.UTF_8);
        httpRequestMaker.setEntity(httpEntity);
    }

    public void setPostBody(String body) {
        httpRequestMaker.setEntity(new StringEntity(body, StandardCharsets.UTF_8));
    }

    public void setPostBody(HttpEntity httpEntity) {
        httpRequestMaker.setEntity(httpEntity);
    }

    public void setPostBody(String body, ContentType contentType) {
        ContentType ct = ContentType.create(contentType.getMimeType(), StandardCharsets.UTF_8);
        StringEntity stringEntity = new StringEntity(body, ct);
        httpRequestMaker.setEntity(stringEntity);
    }

    public void setHeader(Header header) {
        if (header == null) {
            return;
        }

        httpRequestMaker.setHeader(header.getName(), header.getValue());
    }

    public void setHeaders(List<Header> headers) {
        if (CollectionUtils.isEmpty(headers)) {
            return;
        }

        for (Header header : headers) {
            httpRequestMaker.setHeader(header);
        }
    }

    public void setCookieHeader(String value) {
        setHeader("cookie", value);
    }

    public void setCookieHeader(Cookie cookie) {
        if (cookie == null) {
            return;
        }
        setCookieHeader(cookie.getName() + "=" + cookie.getValue());
    }

    public void setCookieHeaders(List<Cookie> cookies) {
        if (CollectionUtils.isEmpty(cookies)) {
            return;
        }

        for (Cookie cookie : cookies) {
            setCookieHeader(cookie);
        }
    }

    public HttpResponseHelper request() {
        CloseableHttpResponse response = null;
        HttpResponseHelper httpResponseHelper;
        IOException ee = null;
        try {
            context = HttpClientContext.create();
            if (httpHost == null) {
                response = httpClient.execute(httpRequestMaker, context);
            } else {
                response = httpClient.execute(httpHost, httpRequestMaker, context);
            }
        } catch (IOException e) {
            String message = e.getMessage();
            log.warn("[HttpHelper]: " + message, e);
            ee = e;
        } finally {
            httpResponseHelper = new HttpResponseHelper(httpCookieStore, response, ee, context);
            HttpClientUtils.closeQuietly(httpClient);
            HttpClientUtils.closeQuietly(response);
        }
        return httpResponseHelper;
    }
}

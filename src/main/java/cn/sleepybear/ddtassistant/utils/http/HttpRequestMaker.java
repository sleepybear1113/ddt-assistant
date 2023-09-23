package cn.sleepybear.ddtassistant.utils.http;

import cn.sleepybear.ddtassistant.utils.http.enumeration.MethodEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.http.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.CloneUtils;

import java.net.URI;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * there is introduction
 *
 * @author XJX
 * @date 2021/1/30 16:51
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class HttpRequestMaker extends HttpRequestBase implements HttpEntityEnclosingRequest {
    public String methodName;

    private HttpEntity entity;

    private HttpHost httpHost;

    public HttpRequestMaker(String url) {
        this(URI.create(url));
    }

    public HttpRequestMaker(String url, MethodEnum methodEnum) {
        this(URI.create(url), methodEnum);
    }

    public HttpRequestMaker(URI uri) {
        this(uri, MethodEnum.METHOD_GET);
    }

    public HttpRequestMaker(URI uri, MethodEnum methodEnum) {
        this.methodName = methodEnum == null ? MethodEnum.METHOD_GET.getMethod() : methodEnum.getMethod();
        this.setURI(uri);
    }

    public static HttpRequestMaker makeHttpHelper(String url, MethodEnum methodEnum) {
        return new HttpRequestMaker(url, methodEnum);
    }

    public static HttpRequestMaker makeHttpHelper(URI uri, MethodEnum methodEnum) {
        return new HttpRequestMaker(uri, methodEnum);
    }

    public static HttpRequestMaker makeGetHttpHelper(String url) {
        return makeHttpHelper(url, MethodEnum.METHOD_GET);
    }

    public static HttpRequestMaker makePostHttpHelper(String url) {
        return makeHttpHelper(url, MethodEnum.METHOD_POST);
    }

    public static HttpRequestMaker makePutHttpHelper(String url) {
        return makeHttpHelper(url, MethodEnum.METHOD_PUT);
    }

    public static HttpRequestMaker makeDeleteHttpHelper(String url) {
        return makeHttpHelper(url, MethodEnum.METHOD_DELETE);
    }

    public static HttpRequestMaker makeHeadHttpHelper(String url) {
        return makeHttpHelper(url, MethodEnum.METHOD_HEAD);
    }

    public static HttpRequestMaker makePatchHttpHelper(String url) {
        return makeHttpHelper(url, MethodEnum.METHOD_PATCH);
    }

    public static HttpRequestMaker makeOptionsHttpHelper(String url) {
        return makeHttpHelper(url, MethodEnum.METHOD_OPTIONS);
    }

    public static HttpRequestMaker makeTraceHttpHelper(String url) {
        return makeHttpHelper(url, MethodEnum.METHOD_TRACE);
    }

    public static HttpRequestMaker makeGetHttpHelper(URI uri) {
        return makeHttpHelper(uri, MethodEnum.METHOD_GET);
    }

    public static HttpRequestMaker makePostHttpHelper(URI uri) {
        return makeHttpHelper(uri, MethodEnum.METHOD_POST);
    }

    public static HttpRequestMaker makePutHttpHelper(URI uri) {
        return makeHttpHelper(uri, MethodEnum.METHOD_PUT);
    }

    public static HttpRequestMaker makeDeleteHttpHelper(URI uri) {
        return makeHttpHelper(uri, MethodEnum.METHOD_DELETE);
    }

    public static HttpRequestMaker makeHeadHttpHelper(URI uri) {
        return makeHttpHelper(uri, MethodEnum.METHOD_HEAD);
    }

    public static HttpRequestMaker makePatchHttpHelper(URI uri) {
        return makeHttpHelper(uri, MethodEnum.METHOD_PATCH);
    }

    public static HttpRequestMaker makeOptionsHttpHelper(URI uri) {
        return makeHttpHelper(uri, MethodEnum.METHOD_OPTIONS);
    }

    public static HttpRequestMaker makeTraceHttpHelper(URI uri) {
        return makeHttpHelper(uri, MethodEnum.METHOD_TRACE);
    }

    public HttpRequestMaker buildDefaultTimeoutConfig() {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(1000)
                .setConnectTimeout(1000 * 5)
                .setSocketTimeout(1000 * 5)
                .setProxy(this.httpHost)
                .build();
        return buildTimeoutConfig(requestConfig);
    }

    public HttpRequestMaker buildTimeoutConfig(RequestConfig requestConfig) {
        this.setConfig(requestConfig);
        return this;
    }

    @Override
    public String getMethod() {
        return this.methodName;
    }

    /**
     * 获取受允许的请求方法
     *
     * @param response 相应
     * @return 返回可接受的枚举 set
     */
    public Set<MethodEnum> getAllowedMethods(HttpResponse response) {
        if (response == null) {
            return Collections.emptySet();
        }

        Header[] allowHeader = response.getHeaders("Allow");
        if (allowHeader == null || allowHeader.length == 0) {
            return Collections.emptySet();
        }

        Set<MethodEnum> methods = new HashSet<>();
        for (Header header : allowHeader) {
            HeaderElement[] elements = header.getElements();

            for (HeaderElement element : elements) {
                String name = element.getName();
                MethodEnum method = MethodEnum.getByMethodName(name);
                if (method == null) {
                    continue;
                }
                methods.add(method);
            }
        }

        return methods;
    }

    @Override
    public HttpEntity getEntity() {
        return this.entity;
    }

    @Override
    public void setEntity(HttpEntity entity) {
        this.entity = entity;
    }

    @Override
    public boolean expectContinue() {
        Header expect = this.getFirstHeader("Expect");
        return expect != null && "100-continue".equalsIgnoreCase(expect.getValue());
    }

    @Override
    public HttpRequestMaker clone() throws CloneNotSupportedException {
        HttpRequestMaker clone = (HttpRequestMaker) super.clone();
        if (this.entity != null) {
            clone.entity = CloneUtils.cloneObject(this.entity);
        }

        return clone;
    }
}

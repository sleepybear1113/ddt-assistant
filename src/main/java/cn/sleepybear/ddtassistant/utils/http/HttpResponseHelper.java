package cn.sleepybear.ddtassistant.utils.http;

import cn.sleepybear.ddtassistant.utils.http.enumeration.HttpStatusCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.http.Header;
import org.apache.http.StatusLine;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * there is introduction
 *
 * @author XJX
 * @date 2021/4/18 12:11
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class HttpResponseHelper {
    private CookieStore httpCookieStore;
    private Header[] headers;
    private StatusLine statusLine;
    private String responseBody;
    private byte[] responseBodyBytes;
    private String exceptionMessage;
    private HttpClientContext context;

    public HttpResponseHelper(CookieStore httpCookieStore, Header[] headers, StatusLine statusLine, String responseBody) {
        this.httpCookieStore = httpCookieStore;
        this.headers = headers;
        this.statusLine = statusLine;
        this.responseBody = responseBody;
    }

    public HttpResponseHelper(CookieStore httpCookieStore, CloseableHttpResponse closeableHttpResponse, IOException ioException, HttpClientContext context) {
        this.httpCookieStore = httpCookieStore;
        this.context = context;
        if (ioException != null) {
            exceptionMessage = ioException.getMessage();
        }

        if (closeableHttpResponse != null) {
            this.statusLine = closeableHttpResponse.getStatusLine();
            this.headers = closeableHttpResponse.getAllHeaders();
            try {
                this.responseBodyBytes = EntityUtils.toByteArray(closeableHttpResponse.getEntity());
                this.responseBody = new String(this.responseBodyBytes, StandardCharsets.UTF_8);
            } catch (IOException e) {
                this.exceptionMessage = e.getMessage();
            }
        }
    }

    public void setHttpCookieStore(CookieStore httpCookieStore) {
        this.httpCookieStore = httpCookieStore;
    }

    public void setHeaders(Header[] headers) {
        this.headers = headers;
    }

    public void setStatusLine(StatusLine statusLine) {
        this.statusLine = statusLine;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    public boolean isResponse2xx3xx() {
        return isResponse2xx() || isResponse3xx();
    }

    public boolean isResponse2xx() {
        if (this.statusLine == null) {
            return false;
        }
        int statusCode = this.statusLine.getStatusCode();
        return HttpStatusCode.OK.getCode() <= statusCode && HttpStatusCode.MULTIPLE_CHOICES.getCode() > statusCode;
    }

    public boolean isResponse3xx() {
        if (this.statusLine == null) {
            return false;
        }
        int statusCode = this.statusLine.getStatusCode();
        return HttpStatusCode.MULTIPLE_CHOICES.getCode() <= statusCode && HttpStatusCode.BAD_REQUEST.getCode() > statusCode;
    }

    @Override
    public String toString() {
        return "HttpResponseHelper{" +
                "httpCookieStore=" + httpCookieStore +
                ", headers=" + Arrays.toString(headers) +
                ", statusLine=" + statusLine +
                ", responseBody='" + responseBody + '\'' +
                '}';
    }
}

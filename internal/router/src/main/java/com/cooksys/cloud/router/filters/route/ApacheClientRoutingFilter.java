package com.cooksys.cloud.router.filters.route;

import com.cooksys.cloud.router.core.RequestContextFactory;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.util.HTTPRequestUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.message.BasicHeader;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

/**
 * Apache HTTP client implementation of a routing filter
 *
 * @author Tim Davidson
 */
public abstract class ApacheClientRoutingFilter extends ZuulFilter {
    public static final String CONTENT_ENCODING = "Content-Encoding";
    public static final String POST = "POST";
    public static final String PUT = "PUT";
    public static final String DELETE = "DELETE";
    public static final String OPTIONS = "OPTIONS";
    public static final String HEAD = "HEAD";
    public static final String GET = "GET";

    protected final RequestContextFactory contextFactory;
    protected final HttpClientFactory httpClientFactory;

    public ApacheClientRoutingFilter(RequestContextFactory contextFactory, HttpClientFactory httpClientFactory) {
        this.contextFactory = contextFactory;
        this.httpClientFactory = httpClientFactory;
    }

    /**
     * Builds the list of headers to be forwarded
     *
     * @param request
     * @return
     */
    protected Header[] buildZuulRequestHeaders(HttpServletRequest request) {
        final List<BasicHeader> headers = new ArrayList<>();
        Enumeration<String> headerNames = request.getHeaderNames();

        while (headerNames.hasMoreElements()) {
            final String name = headerNames.nextElement().toLowerCase();
            final String value = request.getHeader(name);
            if (isValidHeader(name)) {
                headers.add(new BasicHeader(name, value));
            }
        }

        final Map<String, String> zuulRequestHeaders = contextFactory.getZuulRequestContext().getZuulRequestHeaders();

        for (String zuulRequestHeader : zuulRequestHeaders.keySet()) {
            final String name = zuulRequestHeader.toLowerCase();

            Iterator<BasicHeader> it = headers.iterator();

            while (it.hasNext()) {
                final BasicHeader header = it.next();
                if (header.getName().equals(name)) {
                    it.remove();
                }
            }
            headers.add(new BasicHeader(zuulRequestHeader, zuulRequestHeaders.get(zuulRequestHeader)));
        }

        if (contextFactory.getZuulRequestContext().getResponseGZipped()) {
            headers.add(new BasicHeader("accept-encoding", "deflate, gzip"));
        }

        return headers.toArray(new Header[headers.size()]);
    }

    /**
     * Get the HTTP verb from the request
     *
     * @param request
     * @return
     */
    protected String getVerb(HttpServletRequest request) {
        return getVerb(request.getMethod().toUpperCase());
    }

    protected String getVerb(String sMethod) {
        if (sMethod == null) {
            return GET;
        }

        sMethod = sMethod.toLowerCase();

        switch (sMethod) {
            case "post":
                return POST;
            case "put":
                return PUT;
            case "delete":
                return DELETE;
            case "options":
                return OPTIONS;
            case "head":
                return HEAD;
            default:
                return GET;
        }
    }

    /**
     * Set the zuul response in the RequestContext
     *
     * @param response
     * @throws IOException
     */
    protected void setResponse(HttpResponse response) throws IOException {
        RequestContext context = contextFactory.getZuulRequestContext();

        context.set("hostZuulResponse", response);
        StatusLine statusLine = response.getStatusLine();
        int statusCode = statusLine.getStatusCode();
        context.setResponseStatusCode(statusCode);
        context.setResponseDataStream(response.getEntity().getContent());

        boolean isOriginResponseGzipped = false;

        for (Header header : response.getHeaders(CONTENT_ENCODING)) {
            if (HTTPRequestUtils.getInstance().isGzipped(header.getValue())) {
                isOriginResponseGzipped = true;
                break;
            }
        }


        context.setResponseGZipped(isOriginResponseGzipped);

        for (Header header : response.getAllHeaders()) {
            context.addOriginResponseHeader(header.getName(), header.getValue());

            if (header.getName().equalsIgnoreCase("content-length")) {
                context.setOriginContentLength(header.getValue());
            }

            if (isValidHeader(header)) {
                context.addZuulResponseHeader(header.getName(), header.getValue());
            }
        }
    }

    /**
     * Validates the response headers
     *
     * @param header
     * @return
     */
    protected boolean isValidHeader(Header header) {
        switch (header.getName().toLowerCase()) {
            case "connection":
            case "content-length":
            case "content-encoding":
            case "server":
            case "transfer-encoding":
                return false;
            default:
                return true;
        }
    }

    /**
     * Validates the request headers
     *
     * @param name
     * @return
     */
    protected boolean isValidHeader(String name) {
        if (name.toLowerCase().contains("content-length")) {
            return false;
        }

        if (!RequestContext.getCurrentContext().getResponseGZipped()) {
            if (name.toLowerCase().contains("accept-encoding")) {
                return false;
            }
        }
        return true;
    }

}

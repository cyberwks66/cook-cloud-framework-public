package com.cooksys.cloud.router.filters.route;

import com.cooksys.cloud.router.core.RequestContextFactory;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.util.HTTPRequestUtils;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;


/**
 * Base class for implementing routing filters
 *
 * @author Tim Davidson
 */
public abstract class AbstractRoutingFilter extends ZuulFilter {
    public static final String CONTENT_ENCODING = "Content-Encoding";

    protected final RequestContextFactory contextFactory;

    public AbstractRoutingFilter(RequestContextFactory contextFactory) {
        this.contextFactory = contextFactory;
    }

    protected HttpHeaders buildZuulRequestHeaders(HttpServletRequest request) {
        final HttpHeaders headers = new HttpHeaders();
        Enumeration<String> headerNames = request.getHeaderNames();

        while (headerNames.hasMoreElements()) {
            final String name = headerNames.nextElement().toLowerCase();
            final String value = request.getHeader(name);
            if (isValidRequestHeader(name)) {
                headers.add(name, value);
            }
        }

        final Map<String, String> zuulRequestHeaders = contextFactory.getZuulRequestContext().getZuulRequestHeaders();

        for (String zuulRequestHeader : zuulRequestHeaders.keySet()) {
            if (headers.containsKey(zuulRequestHeader)) {
                headers.remove(zuulRequestHeader);
            }
            headers.add(zuulRequestHeader, zuulRequestHeaders.get(zuulRequestHeader));
        }

        if (contextFactory.getZuulRequestContext().getResponseGZipped()) {
            headers.add("accept-encoding", "deflate, gzip");
        }

        return headers;
    }

    protected HttpMethod getVerb(HttpServletRequest request) {
        final String sMethod = request.getMethod().toUpperCase();

        if(sMethod==null) {
            return HttpMethod.GET;
        }

        return HttpMethod.valueOf(sMethod);
    }

    protected void setResponse(ResponseEntity<Resource> response) throws IOException {
        final RequestContext context = contextFactory.getZuulRequestContext();

        context.set("hostZuulResponse", response);
        final HttpStatus statusCode = response.getStatusCode();
        context.setResponseStatusCode(statusCode.value());
        context.setResponseDataStream(response.getBody().getInputStream());

        boolean isOriginResponseGzipped = false;
        final HttpHeaders headers = response.getHeaders();

        if(headers.containsKey(CONTENT_ENCODING)) {
            for(String value : headers.get(CONTENT_ENCODING)) {
                if(HTTPRequestUtils.getInstance().isGzipped(value)) {
                    isOriginResponseGzipped=true;
                    break;
                }
            }
        }

        context.setResponseGZipped(isOriginResponseGzipped);

        for(Map.Entry<String,List<String>> header : headers.entrySet()) {
            for(String value : header.getValue()) {
                context.addOriginResponseHeader(header.getKey(),value);

                if(header.getKey().equalsIgnoreCase("content-length")) {
                    context.setOriginContentLength(value);
                }

                if(isValidResponseHeader(header.getKey())) {
                    context.addZuulResponseHeader(header.getKey(),value);
                }
            }
        }

    }

    /**
     * Validates the request headers
     *
     * @param header
     * @return
     */
    protected boolean isValidRequestHeader(String header) {
        if (header.toLowerCase().contains("content-length")) {
            return false;
        }

        if (!RequestContext.getCurrentContext().getResponseGZipped()) {
            if (header.toLowerCase().contains("accept-encoding")) {
                return false;
            }
        }
        return true;
    }

    /**
     * Validates the response headers
     *
     * @param header
     * @return
     */
    protected boolean isValidResponseHeader(String header) {
        switch (header.toLowerCase()) {
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


}

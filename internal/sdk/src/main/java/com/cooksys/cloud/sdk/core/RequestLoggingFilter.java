package com.cooksys.cloud.sdk.core;

import com.cooksys.cloud.commons.SharedConstants;
import com.cooksys.cloud.sdk.CloudRequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.AbstractRequestLoggingFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.Date;


/**
 * Filter that grabs the payload of a request for logging.
 *
 * @author Tim Davidson
 */
@Component
public class RequestLoggingFilter extends AbstractRequestLoggingFilter {

    @Autowired
    private CloudRequestContext requestContext;

    /**
     * Place the payload of the request in the RequestContext for logging.
     */
    @Override
    protected void beforeRequest(HttpServletRequest request, String payload) {
        requestContext.put(SharedConstants.REQUEST_BODY, payload);
        requestContext.put(SharedConstants.EVENT_START, new Date());
        requestContext.put(SharedConstants.REQUEST_URI, request.getRequestURI());
    }

    @Override
    protected void afterRequest(HttpServletRequest httpServletRequest, String s) {
    }

    /**
     * Pull the payload of a request
     */
    @Override
    protected String createMessage(HttpServletRequest request, String prefix, String suffix) {
        final StringBuilder msg = new StringBuilder();

        final ContentCachingRequestWrapper wrapper = WebUtils.getNativeRequest(request,
                ContentCachingRequestWrapper.class);

        if (wrapper != null) {
            final byte[] buf = wrapper.getContentAsByteArray();

            if (buf.length > 0) {
                final int length = Math.min(buf.length, getMaxPayloadLength());
                String payload;

                try {
                    payload = new String(buf, 0, length, wrapper.getCharacterEncoding());
                } catch (final UnsupportedEncodingException ex) {
                    payload = "[unknown]";
                }

                msg.append(payload);
            }
        }

        return msg.toString();
    }
}

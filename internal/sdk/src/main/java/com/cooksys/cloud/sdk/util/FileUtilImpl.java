package com.cooksys.cloud.sdk.util;

import com.cooksys.cloud.sdk.SpringContext;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URISyntaxException;

/**
 * Standard implementation of FileUtil - misc file utility methods
 *
 * @author Tim Davidson
 */
@Component
public class FileUtilImpl implements FileUtil {

    /*
     * (non-Javadoc)
     * @see com.cooksys.cloud.sdk.util.FileUtil#readFileFromClasspath(java.lang.String)
     */
    @Override
    public String readFileFromClasspath(String filename) throws IOException, URISyntaxException {
        // String fileContents=null;

        final Resource fileResource = SpringContext.getApplicationContext().getResource("classpath:" + filename);

        final StringWriter sw = new StringWriter();
        IOUtils.copy(fileResource.getInputStream(), sw);

        return sw.toString();

    }

}

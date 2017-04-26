package com.cooksys.cloud.cloudmanager.core.ecs;

import com.cooksys.cloud.commons.event.cloudmanager.Service;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Test class for {@link EcsUtil}
 *
 * @author Tim Davidson
 */
public class EcsUtilTest {

    @Test
    public void testFormatServiceIdentifier() throws Exception {
        final String serviceId = "test";
        final String version = "1.0.1";
        assertTrue("test_1_0_1".equals(EcsUtil.formatServiceIdentifier(serviceId,version)));
    }

    @Test
    public void testParseServiceIdentifier() throws Exception {
        final Service service = EcsUtil.parseServiceIdentifier("test_1_0_1");
        assertTrue("test".equals(service.getServiceId()));
        assertTrue("1.0.1".equals(service.getVersion()));
    }
}
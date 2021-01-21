package com.frameworkium.integration.wiremock;

import com.frameworkium.core.api.tests.BaseAPITest;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.testng.annotations.Test;

@Test
/*
 * This is called in post-integration-test phase to reset wiremock after an integration-test phase in anticipation
 * for any further test phases that may set their own wiremock mappings
 */
public class WireMockJiraTeardown extends BaseAPITest {

    public void mockJiraTeardown() {
        final WireMock wireMock = new WireMock();
        WireMock.reset();
    }
}

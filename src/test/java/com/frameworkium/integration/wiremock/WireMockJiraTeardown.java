package com.frameworkium.integration.wiremock;

import com.frameworkium.core.api.tests.BaseAPITest;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.testng.annotations.Test;

@Test
public class WireMockJiraTeardown extends BaseAPITest {

    public void mockJiraTeardown() {
        final WireMock wireMock = new WireMock();
        WireMock.reset();
    }
}

package com.frameworkium.core.ui.capture.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.frameworkium.core.ui.tests.BaseTest;
import net.sf.uadetector.ReadableUserAgent;
import net.sf.uadetector.UserAgentStringParser;
import net.sf.uadetector.service.UADetectorServiceFactory;

import static com.frameworkium.core.common.properties.Property.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Browser {

    public String name;
    public String version;
    public String device;
    public String platform;
    public String platformVersion;

    public Browser() {

        if (!BaseTest.userAgent.isEmpty()) {
            // Get an UserAgentStringParser and analyze the requesting client
            UserAgentStringParser parser = UADetectorServiceFactory.getResourceModuleParser();
            ReadableUserAgent agent = parser.parse(BaseTest.userAgent);

            // Set the params based on this agent
            this.name = agent.getName();
            this.version = agent.getVersionNumber().toVersionString();
            this.device = agent.getDeviceCategory().getName();
            this.platform = agent.getOperatingSystem().getName();
            this.platformVersion = agent.getOperatingSystem().getVersionNumber().toVersionString();

        } else {

            if (BROWSER.isSpecified()) {
                this.name = BROWSER.getValue().toLowerCase();
            }
            if (BROWSER_VERSION.isSpecified()) {
                this.version = BROWSER_VERSION.getValue();
            }
            if (DEVICE.isSpecified()) {
                this.device = DEVICE.getValue();
            }
            if (PLATFORM.isSpecified()) {
                this.platform = PLATFORM.getValue();
            }
            if (PLATFORM_VERSION.isSpecified()) {
                this.platformVersion = PLATFORM_VERSION.getValue();
            }
        }
    }

}

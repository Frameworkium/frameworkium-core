package com.frameworkium.core.ui.capture.model;

import com.frameworkium.core.common.properties.Property;
import com.frameworkium.core.ui.tests.BaseTest;
import net.sf.uadetector.ReadableUserAgent;
import net.sf.uadetector.UserAgentStringParser;
import net.sf.uadetector.service.UADetectorServiceFactory;

public class Browser {
    private String name;
    private String version;
    private String device;
    private String platform;
    private String platformVersion;

    public Browser() {


        if (!BaseTest.userAgent.isEmpty()) {
            // Get an UserAgentStringParser and analyze the requesting client
            UserAgentStringParser parser = UADetectorServiceFactory.getResourceModuleParser();
            ReadableUserAgent agent = parser.parse(BaseTest.userAgent);

            //Set the params based on this agent
            this.name = agent.getName();
            this.version = agent.getVersionNumber().toVersionString();
            this.device = agent.getDeviceCategory().getName();
            this.platform = agent.getOperatingSystem().getName();
            this.platformVersion = agent.getOperatingSystem().getVersionNumber().toVersionString();

        } else {

            if (Property.BROWSER.isSpecified()) {
                this.name = Property.BROWSER.getValue().toLowerCase();
            }
            if (Property.BROWSER_VERSION.isSpecified()) {
                this.version = Property.BROWSER_VERSION.getValue();
            }
            if (Property.DEVICE.isSpecified()) {
                this.device = Property.DEVICE.getValue();
            }
            if (Property.PLATFORM.isSpecified()) {
                this.platform = Property.PLATFORM.getValue();
            }
            if (Property.PLATFORM_VERSION.isSpecified()) {
                this.platformVersion = Property.PLATFORM_VERSION.getValue();
            }
        }
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getDevice() {
        return device;
    }

    public String getPlatform() {
        return platform;
    }

    public String getPlatformVersion() {
        return platformVersion;
    }
}

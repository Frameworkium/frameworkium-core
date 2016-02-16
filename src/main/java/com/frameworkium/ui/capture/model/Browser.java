package com.frameworkium.ui.capture.model;

import com.frameworkium.config.SystemProperty;
import com.frameworkium.tests.internal.BaseTest;
import net.sf.uadetector.service.UADetectorServiceFactory;
import net.sf.uadetector.UserAgentStringParser;
import net.sf.uadetector.ReadableUserAgent;

public class Browser {
    private String name;
    private String version;
    private String device;
    private String platform;
    private String platformVersion;

    public Browser() {


        if(!BaseTest.userAgent.isEmpty()) {
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

            if (SystemProperty.BROWSER.isSpecified()) {
                this.name = SystemProperty.BROWSER.getValue().toLowerCase();
            }
            if (SystemProperty.BROWSER_VERSION.isSpecified()) {
                this.version = SystemProperty.BROWSER_VERSION.getValue();
            }
            if (SystemProperty.DEVICE.isSpecified()) {
                this.device = SystemProperty.DEVICE.getValue();
            }
            if (SystemProperty.PLATFORM.isSpecified()) {
                this.platform = SystemProperty.PLATFORM.getValue();
            }
            if (SystemProperty.PLATFORM_VERSION.isSpecified()) {
                this.platformVersion = SystemProperty.PLATFORM_VERSION.getValue();
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

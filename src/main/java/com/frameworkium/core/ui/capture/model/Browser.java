package com.frameworkium.core.ui.capture.model;

import com.frameworkium.core.ui.properties.UIProperty;
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

            if (UIProperty.BROWSER.isSpecified()) {
                this.name = UIProperty.BROWSER.getValue().toLowerCase();
            }
            if (UIProperty.BROWSER_VERSION.isSpecified()) {
                this.version = UIProperty.BROWSER_VERSION.getValue();
            }
            if (UIProperty.DEVICE.isSpecified()) {
                this.device = UIProperty.DEVICE.getValue();
            }
            if (UIProperty.PLATFORM.isSpecified()) {
                this.platform = UIProperty.PLATFORM.getValue();
            }
            if (UIProperty.PLATFORM_VERSION.isSpecified()) {
                this.platformVersion = UIProperty.PLATFORM_VERSION.getValue();
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

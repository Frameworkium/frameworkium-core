package com.frameworkium.core.ui.driver.remotes;

import com.frameworkium.core.common.properties.Property;
import com.saucelabs.common.SauceOnDemandAuthentication;
import com.saucelabs.common.SauceOnDemandSessionIdProvider;
import com.saucelabs.saucerest.SauceREST;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class Sauce {

    private static final SauceOnDemandAuthentication authentication = new SauceOnDemandAuthentication(
            System.getenv("SAUCE_USERNAME"), System.getenv("SAUCE_ACCESS_KEY"));

    private static final SauceREST client = new SauceREST(authentication.getUsername(), authentication.getAccessKey());

    public static URL getURL() throws MalformedURLException {
        return new URL(String.format("http://%s:%s@ondemand.saucelabs.com:80/wd/hub", authentication.getUsername(),
                authentication.getAccessKey()));
    }

    public static boolean isDesired() {
        return Property.SAUCE.isSpecified() && Boolean.parseBoolean(Property.SAUCE.getValue());
    }

    public static void updateJobName(SauceOnDemandSessionIdProvider sessionIdProvider, String name) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", name);
        client.updateJobInfo(sessionIdProvider.getSessionId(), updates);
    }

    public static void uploadFile(File file) throws IOException {
        client.uploadFile(file);
    }

    private static boolean environmentVariablesSet() {
        String username = authentication.getUsername();
        String accessKey = authentication.getAccessKey();
        return username != null && !username.isEmpty() && accessKey != null && !accessKey.isEmpty();
    }
}

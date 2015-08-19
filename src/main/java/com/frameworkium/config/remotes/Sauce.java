package com.frameworkium.config.remotes;

import static com.frameworkium.config.SystemProperty.SAUCE;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.saucelabs.common.SauceOnDemandAuthentication;
import com.saucelabs.common.SauceOnDemandSessionIdProvider;
import com.saucelabs.saucerest.SauceREST;

public class Sauce {

    private static final SauceOnDemandAuthentication authentication = new SauceOnDemandAuthentication(
            System.getenv("SAUCE_USERNAME"), System.getenv("SAUCE_ACCESS_KEY"));

    private static final SauceREST client = new SauceREST(authentication.getUsername(), authentication.getAccessKey());

    public static URL getURL() throws MalformedURLException {
        return new URL(String.format("http://%s:%s@ondemand.saucelabs.com:80/wd/hub", authentication.getUsername(),
                authentication.getAccessKey()));
    }

    public static boolean isDesired() {
        return SAUCE.isSpecified() && Boolean.parseBoolean(SAUCE.getValue());
    }

    public static void updateJobName(SauceOnDemandSessionIdProvider sessionIdProvider, String name) {
        Map<String, Object> updates = new HashMap<String, Object>();
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

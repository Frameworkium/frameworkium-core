package com.frameworkium.core.ui.video;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class UrlFetcher {

    private static final Logger logger = LogManager.getLogger();

    /**
     * @param url the url to GET
     * @param maxTries max number of tries to GET url
     * @return the bytes from the downloaded URL
     * @throws TimeoutException if download fails and max tries have been exceeded
     */
    public byte[] fetchWithRetry(URL url, int maxTries) throws TimeoutException {
        logger.debug("Downloading: " + url);
        for (int i = 0; i < maxTries; i++) {
            Response response = RestAssured.get(url);
            if (response.getStatusCode() == HttpStatus.SC_OK) {
                return response.asByteArray();
            }
            logger.debug("Retrying download: " + url);

            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
        }
        throw new TimeoutException();
    }
}

package com.frameworkium.core.ui.proxy;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Proxy;

import java.net.URI;
import java.net.URISyntaxException;

public class SeleniumProxyFactory {

    private static final Logger logger = LogManager.getLogger();

    /**
     * Valid inputs are system, autodetect, direct or http://{hostname}:{port}
     *
     * <p>This does not currently cope with PAC (Proxy auto-configuration from URL)
     *
     * @param proxyProperty the string representing the proxy required
     * @return a Selenium {@link Proxy} representation of proxyProperty
     */
    public static Proxy createProxy(String proxyProperty) {
        Proxy proxy = new Proxy();
        switch (proxyProperty.toLowerCase()) {
            case "system":
                logger.debug("Using system proxy");
                proxy.setProxyType(Proxy.ProxyType.SYSTEM);
                break;
            case "autodetect":
                logger.debug("Using autodetect proxy");
                proxy.setProxyType(Proxy.ProxyType.AUTODETECT);
                break;
            case "direct":
                logger.debug("Using direct i.e. (no) proxy");
                proxy.setProxyType(Proxy.ProxyType.DIRECT);
                break;
            default:
                return createManualProxy(proxyProperty);
        }
        return proxy;
    }

    private static Proxy createManualProxy(String proxyProperty) {
        String proxyString = getProxyURL(proxyProperty);
        logger.debug("All protocols to use proxy address: {}", proxyString);
        Proxy proxy = new Proxy();
        proxy.setProxyType(Proxy.ProxyType.MANUAL)
                .setHttpProxy(proxyString)
                .setFtpProxy(proxyString)
                .setSslProxy(proxyString);
        return proxy;
    }

    private static String getProxyURL(String proxyProperty) {
        try {
            URI proxyURI = new URI(proxyProperty);
            String host = proxyURI.getHost();
            int port = proxyURI.getPort();
            if (host == null || port == -1) {
                throw new URISyntaxException(
                        proxyProperty, "invalid host or port");
            }
            return String.format("%s:%d", host, port);
        } catch (NullPointerException | URISyntaxException e) {
            String message = "Invalid proxy specified, acceptable values are: "
                    + "system, autodetect, direct or http://{hostname}:{port}.";
            logger.fatal(message);
            throw new IllegalArgumentException(message, e);
        }
    }
}

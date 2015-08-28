package com.frameworkium.config.drivers;

import static com.frameworkium.config.SystemProperty.PROXY_ADDRESS;
import static com.frameworkium.config.SystemProperty.PROXY_TYPE;

import org.openqa.selenium.Proxy;
import org.openqa.selenium.Proxy.ProxyType;

import com.frameworkium.config.DriverType;

public abstract class ProxyableDriver extends DriverType {

    /**
     * This method returns a proxy object with settings set by the system properties. If no valid proxy argument is set
     * then it returns null.
     *
     * @return A Selenium proxy object for the current system properties or null if no valid proxy settings
     */
    public Proxy getProxy() {
        if (PROXY_TYPE.isSpecified()) {
            Proxy proxy = new Proxy();
            String proxyType = PROXY_TYPE.getValue().toLowerCase();
            switch (proxyType) {
                case "manual":
                    proxy.setProxyType(ProxyType.MANUAL);
                    if (PROXY_ADDRESS.isSpecified()) {
                        String proxyAddress = PROXY_ADDRESS.getValue();
                        proxy.setHttpProxy(proxyAddress).setFtpProxy(proxyAddress).setSslProxy(proxyAddress);
                    } else {
                        logger.error("Manual proxy selected but no proxy address specified, using browser default settings");
                        return null;
                    }
                    break;
                case "system":
                    proxy.setProxyType(ProxyType.SYSTEM);
                    break;
                case "autodetect":
                    proxy.setProxyType(ProxyType.AUTODETECT);
                    break;
                case "direct":
                    proxy.setProxyType(ProxyType.DIRECT);
                    break;
                default:
                    logger.error("Invalid proxy type specified, using browser default settings");
                    return null;
            }
            return proxy;
        }
        return null;
    }
}

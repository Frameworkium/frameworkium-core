package com.frameworkium.lite.ui.capture.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.frameworkium.lite.ui.listeners.LoggingListener;
import org.openqa.selenium.WebElement;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Command {

    public String action;
    public String using;
    public String value;

    public Command(String action, String using, String value) {
        this.action = action;
        this.using = using;
        this.value = value;
    }

    public Command(String action, WebElement element) {
        this.action = action;
        this.using = "n/a";
        this.value = LoggingListener.getLocatorFromElement(element);
    }

}

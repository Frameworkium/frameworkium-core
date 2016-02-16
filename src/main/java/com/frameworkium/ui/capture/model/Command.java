package com.frameworkium.ui.capture.model;

import org.openqa.selenium.WebElement;

import com.frameworkium.config.DriverType;

public class Command {

    private String action;
    private String using;
    private String value;

    public Command(final String action, final String using, final String value) {
        this.action = action;
        this.using = using;
        this.value = value;
    }

    public Command(final String action, final WebElement element) {
        this.action = action;

        // TODO: Improve this. Use hacky solution in EventListener?
        if (DriverType.isNative()) {
            this.using = "n/a";
            this.value = "n/a";
        } else {
            if (!(element.getAttribute("id")).isEmpty()) {
                this.using = "id";
                this.value = element.getAttribute("id");
            } else if (!(element.getText()).isEmpty()) {
                this.using = "linktext";
                this.value = element.getText();
            } else if (!element.getTagName().isEmpty()) {
                this.using = "css";
                this.value = element.getTagName() + "." + element.getAttribute("class");
            } else {
                // must be something weird
                this.using = "n/a";
                this.value = "n/a";
            }
        }
    }

    public String getAction() {
        return action;
    }

    public String getUsing() {
        return using;
    }

    public String getValue() {
        return value;
    }
}

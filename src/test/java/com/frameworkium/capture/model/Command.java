package com.frameworkium.capture.model;

import org.openqa.selenium.WebElement;

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
        if (!(element.getAttribute("id")).isEmpty()) {
            this.using = "id";
            this.value = element.getAttribute("id");
        } else if (!(element.getText()).isEmpty()) {
            this.using = "linktext";
            this.value = element.getText();
        } else {
            this.using = "css";
            this.value = element.getTagName() + "." + element.getAttribute("class");
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

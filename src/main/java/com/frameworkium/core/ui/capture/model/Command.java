package com.frameworkium.core.ui.capture.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.frameworkium.core.ui.driver.Driver;
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

        // TODO: Improve this. Use hacky solution in EventListener?
        if (Driver.isNative()) {
            this.using = "n/a";
            this.value = "n/a";
        } else {
            if (!(element.getAttribute("id")).isEmpty()) {
                this.using = "id";
                this.value = element.getAttribute("id");
            } else if (!(element.getText()).isEmpty()) {
                this.using = "linkText";
                this.value = element.getText();
            } else if (!element.getTagName().isEmpty()) {
                this.using = "css";
                this.value = element.getTagName() + "."
                        + element.getAttribute("class").replace(" ", ".");
            } else {
                // must be something weird
                this.using = "n/a";
                this.value = "n/a";
            }
        }
    }
}

package com.frameworkium.integration.seleniumhq.pages;

import com.frameworkium.lite.ui.annotations.Visible;
import com.frameworkium.lite.ui.pages.BasePage;
import com.frameworkium.integration.seleniumhq.components.HeaderComponent;
import org.openqa.selenium.support.FindBy;
import com.frameworkium.lite.htmlelements.element.Link;

import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;

public class SeleniumDownloadPage extends BasePage<SeleniumDownloadPage> {

    @Visible
    private HeaderComponent header;

    @Visible
    @FindBy(css = "body > div.split-section.container.dark-background > div.right > p:nth-child(1) > a")
    private Link latestDownloadLink;

    public static SeleniumDownloadPage open() {
        return new SeleniumDownloadPage()
                .get("https://selenium.dev/downloads/");
    }

    public String getLatestVersion() {
        return latestDownloadLink.getText();
    }

    public void waitForContent() {
        wait.until(visibilityOf(latestDownloadLink));
    }

    public void log() {
        logger.trace("Using the BasePage logger");
        logger.info("Using the BasePage logger");
    }
}

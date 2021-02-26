package com.frameworkium.integration.theinternet.pages;

import com.frameworkium.core.ui.annotations.Invisible;
import com.frameworkium.core.ui.annotations.Visible;
import com.frameworkium.core.ui.pages.BasePage;
import io.qameta.allure.Step;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;

public class HoversPage extends BasePage<HoversPage> {

    @Visible
    @FindBy(css = "div.figure:nth-of-type(1)")
    private WebElement firstFigure;

    @Invisible
    @FindBy(css = "div.figure:nth-of-type(1) div.figcaption")
    private WebElement firstFigureCaption;

    @Step("Get 1st figure caption")
    public String getFirstFigureCaption() {

        // Move mouse over the first figure to make caption visible
        (new Actions(driver)).moveToElement(firstFigure).perform();

        // Return text from the now-visible caption
        return firstFigureCaption.getText();
    }

}

package com.frameworkium.pages.web.the_internet.pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;

import ru.yandex.qatools.allure.annotations.Step;
import ru.yandex.qatools.htmlelements.annotations.Name;

import com.frameworkium.pages.internal.BasePage;
import com.frameworkium.pages.internal.Visible;

public class HoversPage extends BasePage<HoversPage> {

    @Visible
    @Name("First Figure")
    @FindBy(css = "div.figure:nth-of-type(1)")
    private WebElement firstFigure;

    @Name("First Figure Caption")
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

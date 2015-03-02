package com.frameworkium.pages.web;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;

import ru.yandex.qatools.allure.annotations.Step;
import ru.yandex.qatools.htmlelements.annotations.Name;

import com.frameworkium.pages.internal.BasePage;
import com.frameworkium.pages.internal.Visible;

public class DragAndDropPage extends BasePage<DragAndDropPage> {

    @Visible
    @Name("Box A")
    @FindBy(xpath = "//div//header[contains(.,'A')]/..")
    private WebElement boxA;

    @Visible
    @Name("Box B")
    @FindBy(xpath = "//div//header[contains(.,'B')]/..")
    private WebElement boxB;

    @Step("Drag A onto B")
    public DragAndDropPage dragAontoB() {

    	(new Actions(driver)).dragAndDrop(boxA, boxB).perform();
    	return this;
    }
    
    @Step("Drag B onto A")
    public DragAndDropPage dragBontoA() {

    	(new Actions(driver)).dragAndDrop(boxB, boxA).perform();
    	return this;
    }
   
}

package com.frameworkium.pages.web.the_internet.pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;

import ru.yandex.qatools.allure.annotations.Step;
import ru.yandex.qatools.htmlelements.annotations.Name;
import ru.yandex.qatools.htmlelements.element.Link;

import com.frameworkium.pages.internal.BasePage;
import com.frameworkium.pages.internal.PageFactory;
import com.frameworkium.pages.internal.Visible;

public class JQueryUIMenuPage extends BasePage<JQueryUIMenuPage> {

    @Visible
    @Name("Enabled menu item")
    @FindBy(linkText = "Enabled")
    private WebElement enabledMenuItem;

    @Name("Back to JQuery UI menu item")
    @FindBy(linkText = "Back to JQuery UI")
    private Link backToJQueryUIMenuItem;

    @Name("downloads menu item")
    @FindBy(linkText = "Downloads")
    private WebElement downloadsMenuItem;

    @Name("excel file menu item")
    @FindBy(linkText = "Excel")
    private Link excelFileMenuItem;
    
    @Step("click back to UI")
    public JQueryUIPage clickBackToUI() {	
    	
    	//Move mouse over the first figure to make caption visible
    	(new Actions(driver)).moveToElement(enabledMenuItem).perform();
    	
    	//Return text from the now-visible caption
    	backToJQueryUIMenuItem.click();
    	
    	//returns us a new page
    	return PageFactory.newInstance(JQueryUIPage.class);
    }

    @Step("Get excel file URL as String")
    public String getExcelFileURLAsString() {	
    	
    	//Move mouse over the first figure to make caption visible
    	(new Actions(driver)).moveToElement(enabledMenuItem).perform();
    	
    	//Move mouse over the first figure to make caption visible
    	(new Actions(driver)).moveToElement(downloadsMenuItem).perform();
    	
    	//Return text from the now-visible caption
    	return excelFileMenuItem.getReference();
    }
   
}

package com.frameworkium.pages.web;

import org.openqa.selenium.support.FindBy;

import ru.yandex.qatools.allure.annotations.Step;
import ru.yandex.qatools.htmlelements.annotations.Name;
import ru.yandex.qatools.htmlelements.element.Select;

import com.frameworkium.pages.internal.BasePage;
import com.frameworkium.pages.internal.Visible;

public class DropdownPage extends BasePage<DropdownPage> {

    @Visible
    @Name("Dropdown list")
    @FindBy(css = "select#dropdown")
    private Select dropdown;

    
    @Step("Return the text displayed in the header")
    public DropdownPage selectFromDropdown(String option) {
    	dropdown.selectByVisibleText(option);
       return this;
    }

    
    @Step("Return the text displayed in the header")
    public String getSelectedOptionText() {
       return dropdown.getFirstSelectedOption().getText();
    }
}

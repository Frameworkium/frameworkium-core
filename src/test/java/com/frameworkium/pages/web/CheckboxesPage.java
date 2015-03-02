package com.frameworkium.pages.web;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;

import ru.yandex.qatools.allure.annotations.Step;
import ru.yandex.qatools.htmlelements.annotations.Name;
import ru.yandex.qatools.htmlelements.element.CheckBox;

import com.frameworkium.pages.internal.BasePage;
import com.frameworkium.pages.internal.Visible;

public class CheckboxesPage extends BasePage<CheckboxesPage> {

    @Visible
    @Name("Unchecked checkbox")
    @CacheLookup
    @FindBy(css = "form input[type='checkbox']")
    private CheckBox uncheckedCheckbox;

    @Visible
    @Name("Checked checkbox")
    @CacheLookup
    @FindBy(css = "form input[type='checkbox']:checked")
    private CheckBox checkedCheckbox;

    @Step("Set all the checkboxes to true")
    public CheckboxesPage checkAllCheckboxes() {
    	
    	//Question - what happens if you don't use the CacheLookup above?
    	uncheckedCheckbox.set(true);
    	uncheckedCheckbox.set(false);
    	uncheckedCheckbox.set(true);

    	checkedCheckbox.set(true);
    	checkedCheckbox.set(false);
    	checkedCheckbox.set(true);

    	return this;
    }
    
    @Step("Return the checkedness of the checkboxes")
    public List<Boolean> getCheckednessOfCheckboxes() {
    	
    	List<Boolean> checkedness = new ArrayList<Boolean>();
    	
    	checkedness.add(uncheckedCheckbox.isSelected());
    	checkedness.add(checkedCheckbox.isSelected());

    	return checkedness;
    }
}

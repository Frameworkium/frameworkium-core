package com.frameworkium.integration.heroku.theinternet.pages.web;

import com.frameworkium.core.ui.AwaitedConditions;
import com.frameworkium.core.ui.annotations.Visible;
import com.frameworkium.core.ui.pages.BasePage;
import org.openqa.selenium.support.FindBy;
import ru.yandex.qatools.allure.annotations.Step;
import ru.yandex.qatools.htmlelements.annotations.Name;
import ru.yandex.qatools.htmlelements.element.CheckBox;

import java.util.List;
import java.util.stream.Collectors;

public class CheckboxesPage extends BasePage<CheckboxesPage> {

    @Visible
    @Name("All checkboxes")
    @FindBy(css = "form input[type='checkbox']")
    private List<CheckBox> allCheckboxes;

    @Step("Set all the checkboxes to true")
    public CheckboxesPage checkAllCheckboxes() {

        allCheckboxes.stream().forEach(CheckBox::select);

        // not required for this test, just testing it doesn't fail
        wait.until(AwaitedConditions.jQueryAjaxDone());

        return this;
    }

    @Step("Return the checked status of all the checkboxes")
    public List<Boolean> getAllCheckboxCheckedStatus() {

        return allCheckboxes.stream()
                .map(CheckBox::isSelected)
                .collect(Collectors.toList());
    }

}

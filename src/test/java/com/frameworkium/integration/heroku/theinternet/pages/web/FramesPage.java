package com.frameworkium.integration.heroku.theinternet.pages.web;

import com.frameworkium.core.ui.annotations.Visible;
import com.frameworkium.core.ui.pages.BasePage;
import com.frameworkium.core.ui.pages.PageFactory;
import org.openqa.selenium.support.FindBy;
import ru.yandex.qatools.allure.annotations.Step;
import ru.yandex.qatools.htmlelements.annotations.Name;
import ru.yandex.qatools.htmlelements.element.Link;

public class FramesPage extends BasePage<FramesPage> {

    @Visible
    @Name("iFrame Link")
    @FindBy(linkText = "iFrame")
    private Link iFrameLink;

    @Step("Click iFrame link")
    public IFramePage clickIFrameLink() {
        iFrameLink.click();
        return PageFactory.newInstance(IFramePage.class);
    }

}

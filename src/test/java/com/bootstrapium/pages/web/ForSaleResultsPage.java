package com.bootstrapium.pages.web;

import org.openqa.selenium.support.FindBy;

import ru.yandex.qatools.htmlelements.annotations.Name;
import ru.yandex.qatools.htmlelements.element.TextBlock;

import com.bootstrapium.pages.internal.BasePage;
import com.bootstrapium.pages.internal.Visible;
import com.bootstrapium.pages.web.components.Header;

public class ForSaleResultsPage extends BasePage<ForSaleResultsPage> {

    @FindBy(id = "siteheader")
    private Header header;

    @Visible
    @Name("Number of properties label")
    @FindBy(id = "numberOfProperties")
    private TextBlock numberOfPropertiesLabel;
    
    public Header header() {
        return header;
    }

}

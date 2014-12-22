package com.bootstrapium.pages.mobi;

import ru.yandex.qatools.htmlelements.annotations.Name;
import ru.yandex.qatools.htmlelements.element.TextBlock;

import com.bootstrapium.pages.internal.BasePage;
import com.bootstrapium.pages.internal.Visible;
import com.bootstrapium.pages.mobi.components.Header;

public class ForSaleResultsPage extends BasePage<ForSaleResultsPage> {

    private Header searchResultsHeader;

    @Visible
    @Name("Save Search and Alert button")
    private TextBlock saveSearchAndAlertButton;
    
    public Header header() {
        return searchResultsHeader;
    }

}

package com.frameworkium.core.ui.pages;

import com.frameworkium.core.ui.litelements.loader.LitElementLoader;
import com.frameworkium.core.ui.pages.BasePage;

public class BasePageExtra<T extends BasePage<T>> extends BasePage<T> {

    @Override
    protected void initPageObjectFields() {
        LitElementLoader.populatePageObject(this, driver);
    }
}
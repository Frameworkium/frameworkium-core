package com.frameworkium.integration.frameworkium.pages;

import com.frameworkium.core.ui.annotations.*;
import com.frameworkium.core.ui.pages.BasePage;
import com.frameworkium.core.ui.pages.PageFactory;
import ru.yandex.qatools.htmlelements.element.CheckBox;

public class MultiVisibilityTypifiedElement extends BasePage<MultiVisibilityTypifiedElement> {

    @Visible
    @ForceVisible
    private CheckBox typifiedElement;

    public static MultiVisibilityTypifiedElement open() {
        return PageFactory.newInstance(MultiVisibilityTypifiedElement.class);
    }
}
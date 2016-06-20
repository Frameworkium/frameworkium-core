package com.frameworkium.core.ui.pages.pageobjects

import com.frameworkium.core.ui.annotations.ForceVisible
import com.frameworkium.core.ui.annotations.Invisible
import com.frameworkium.core.ui.annotations.Visible
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.CacheLookup
import org.openqa.selenium.support.FindBy
import ru.yandex.qatools.htmlelements.annotations.Name
import ru.yandex.qatools.htmlelements.annotations.Timeout
import ru.yandex.qatools.htmlelements.element.CheckBox
import ru.yandex.qatools.htmlelements.element.HtmlElement
import ru.yandex.qatools.htmlelements.element.TextInput

public class PageObjects {

    class SingleVisibleElement {

        @Visible
        WebElement visibleElement
    }

    class SingleInvisibleElement {

        @Invisible
        TextInput invisibleTextInput
    }

    class SingleForceVisibleElement {

        @ForceVisible
        WebElement forceVisibleElement
    }

    class ListOfElements {

        @Visible
        List<WebElement> visibles

        @Invisible
        List<WebElement> invisibles

        @ForceVisible
        List<WebElement> forceVisibles
    }

    class VisibleComponent {

        @Visible
        Component visibleComponent
    }

    class VisibleComponents {

        @Visible
        List<Component> visibleComponents
    }

    class Component extends HtmlElement {

        @Visible
        WebElement myVisibleWebElement
    }

    // Invalid Page Objects:

    class MultiVisibilityTypifiedElement {

        @Visible
        @ForceVisible
        @Invisible
        @Timeout(42)
        @Name("named element")
        @FindBy(css = "html")
        @CacheLookup
        CheckBox myTypifiedElement
    }

    class MultiVisibilityWebElement {

        @Visible
        @Invisible
        @FindBy(css = "body")
        WebElement myWebElement
    }

    class UnsupportedFieldType {

        @Visible
        String aStringIsNotAWebElement
    }
}

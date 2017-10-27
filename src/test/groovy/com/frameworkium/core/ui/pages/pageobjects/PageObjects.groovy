package com.frameworkium.core.ui.pages.pageobjects

import com.frameworkium.core.ui.annotations.ForceVisible
import com.frameworkium.core.ui.annotations.Invisible
import com.frameworkium.core.ui.annotations.Visible
import com.frameworkium.core.ui.pages.BasePage
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.CacheLookup
import org.openqa.selenium.support.FindBy
import ru.yandex.qatools.htmlelements.annotations.Name
import ru.yandex.qatools.htmlelements.annotations.Timeout
import ru.yandex.qatools.htmlelements.element.CheckBox
import ru.yandex.qatools.htmlelements.element.HtmlElement
import ru.yandex.qatools.htmlelements.element.TextInput

class PageObjects {

    class SingleVisibleElement extends BasePage<SingleVisibleElement> {

        @Visible
        WebElement visibleElement
    }

    class SingleInvisibleElement extends BasePage<SingleInvisibleElement> {

        @Invisible
        TextInput invisibleTextInput
    }

    class SingleForceVisibleElement extends BasePage<SingleForceVisibleElement> {

        @ForceVisible
        WebElement forceVisibleElement
    }

    class ListOfElements extends BasePage<ListOfElements> {

        @Visible
        List<WebElement> visibles

        @Invisible
        List<WebElement> invisibles

        @Invisible
        List<WebElement> emptyInvisible

        @ForceVisible
        List<WebElement> forceVisibles
    }

    class VisibleComponent extends BasePage<VisibleComponent> {

        @Visible
        Component visibleComponent
    }

    class VisibleComponents extends BasePage<VisibleComponents> {

        @Visible
        List<Component> visibleComponents
    }

    class Component extends HtmlElement {

        @Visible
        WebElement myVisibleWebElement
    }

    class SubClassedPage extends SingleVisibleElement {

        @Visible
        WebElement subClassedVisibleWebElement
    }

    // Invalid Page Objects:

    class MultiVisibilityTypifiedElement extends BasePage<MultiVisibilityTypifiedElement> {

        @Visible
        @ForceVisible
        @Invisible
        @Timeout(42)
        @Name("named element")
        @FindBy(css = "html")
        @CacheLookup
        CheckBox myTypifiedElement
    }

    class MultiVisibilityWebElement extends BasePage<MultiVisibilityWebElement> {

        @Visible
        @Invisible
        @FindBy(css = "body")
        WebElement myWebElement
    }

    class UnsupportedFieldType extends BasePage<UnsupportedFieldType> {

        @Visible
        String aStringIsNotAWebElement
    }
}

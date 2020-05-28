package com.frameworkium.lite.htmlelements.element;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import java.util.Optional;

/**
 * Represents text input control
 * (such as &lt;input type="text"/&gt; or &lt;textarea/&gt;).
 */
public class TextInput extends TypifiedElement {

    /**
     * Specifies wrapped {@link WebElement}.
     *
     * @param wrappedElement {@code WebElement} to wrap.
     */
    public TextInput(WebElement wrappedElement) {
        super(wrappedElement);
    }

    /**
     * Retrieves the text entered into this text input.
     *
     * @return Text entered into the text input.
     */
    @Override
    public String getText() {
        if ("textarea".equals(getWrappedElement().getTagName())) {
            return getWrappedElement().getText();
        }

        return Optional
                .ofNullable(getWrappedElement().getAttribute("value"))
                .orElse("");
    }

    /**
     * Sets the text of this Input. This is different to
     * {@link #sendKeys(CharSequence...)} because it will delete any existing
     * text first.
     * <p>
     * {@code text} will equal {@link #getText()} after calling this method.
     *
     * @param text the text to set
     */
    public void setText(CharSequence text) {
        getWrappedElement().sendKeys(getClearCharSequence() + text);
    }

    /**
     * Returns sequence of backspaces and deletes that will clear element.
     * clear() can't be used because generates separate onchange event
     * See https://github.com/yandex-qatools/htmlelements/issues/65
     */
    public String getClearCharSequence() {
        return StringUtils.repeat(Keys.DELETE.toString() + Keys.BACK_SPACE, getText().length());
    }
}

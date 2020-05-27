package com.frameworkium.lite.ui;

import com.frameworkium.lite.ui.element.StreamTable;
import org.testng.annotations.Test;
import ru.yandex.qatools.htmlelements.element.Link;

import java.util.ArrayList;
import java.util.List;

/**
 * Tests to ensure elements other than just WebElements can be passed into
 * {@link ExtraExpectedConditions}.
 *
 * These tests do not check the correctness of the implementation, they are here
 * to prevent https://github.com/Frameworkium/frameworkium-core/issues/133
 */
public class ExtraExpectedConditionsTest {

    private final List<Link> links = new ArrayList<>();
    private final List<StreamTable> tables = new ArrayList<>();

    @Test
    public void testNotPresentOrInvisibleWorksWithTypifiedElements() {
        ExtraExpectedConditions.notPresentOrInvisible(links);
    }

    @Test
    public void testSizeGreaterThanWorksWithTypifiedElements() {
        ExtraExpectedConditions.sizeGreaterThan(links, 1);
    }

    @Test
    public void testSizeLessThanWorksWithTypifiedElements() {
        ExtraExpectedConditions.sizeLessThan(links, 3);
    }

    @Test
    public void testNotPresentOrInvisibleWorksWithHtmlElements() {
        ExtraExpectedConditions.notPresentOrInvisible(tables);
    }

    @Test
    public void testSizeGreaterThanWorksWithHtmlElements() {
        ExtraExpectedConditions.sizeGreaterThan(tables, 1);
    }

    @Test
    public void testSizeLessThanWorksWithHtmlElements() {
        ExtraExpectedConditions.sizeLessThan(tables, 3);
    }
}
package com.frameworkium.pages.web;

import static com.jayway.restassured.RestAssured.expect;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.openqa.selenium.support.FindBy;

import ru.yandex.qatools.allure.annotations.Step;
import ru.yandex.qatools.htmlelements.annotations.Name;
import ru.yandex.qatools.htmlelements.element.Link;

import com.frameworkium.pages.internal.BasePage;
import com.frameworkium.pages.internal.Visible;

public class FileDownloadPage extends BasePage<FileDownloadPage> {

    @Visible
    @Name("Generic download link")
    @FindBy(css = "div.example a")
    private List<Link> allDownloadLinks;

    @Visible
    @Name("First download link")
    @FindBy(css = "div.example a:first-of-type")
    private Link firstDownloadLink;

    @Step("Get the size of the first downloadable file")
    public long getSizeOfFirstFile() {
        return getSizeOfFileAtURL(firstDownloadLink.getReference());
    }

    @Step("Return all download links")
    public List<String> getDownloadableFileLinkNames() {

        List<String> listOfFiles = new ArrayList<String>();

        for (Link lnk : allDownloadLinks) {
            listOfFiles.add(lnk.getText());
        }
        return listOfFiles;

    }

    @Step("Get the size of the file {0}")
    public long getSizeOfFile(String linkText) {
        return getSizeOfFileAtURL(getURLOfFile(linkText));
    }

    @Step("Get the URL of the file {0}")
    public String getURLOfFile(String linkText) {
        return findLinkByText(linkText).getReference();
    }

    private Link findLinkByText(String linkText) {

        for (Link link : allDownloadLinks) {
            if (link.getText().equals(linkText)) {
                return link;
            }
        }
        return null;
    }

    private long getSizeOfFileAtURL(String downloadURL) {

        InputStream inputStream = expect().log().headers().when().get(downloadURL).asInputStream();

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            IOUtils.copy(inputStream, byteArrayOutputStream);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        IOUtils.closeQuietly(byteArrayOutputStream);
        IOUtils.closeQuietly(inputStream);

        return byteArrayOutputStream.size();
    }

}

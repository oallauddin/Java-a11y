/**
 * Copyright (c) 2019 Sridhar Bandi.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package io.github.sridharbandi.driver;

import io.github.sridharbandi.Accessibility;
import io.github.sridharbandi.htmlcs.HTMLCS;
import io.github.sridharbandi.util.Statik;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

public class DriverContext implements IDriverContext {

    private HTMLCS htmlcs = HTMLCS.getInstance();
    private JavascriptExecutor javascriptExecutor;
    private WebDriver driver;

    public DriverContext(WebDriver driver) {
        this.driver = driver;
        javascriptExecutor = (JavascriptExecutor) driver;
    }

    @Override
    public String pageTitle() {
        return driver.getTitle();
    }

    @Override
    public long viewPortWidth() {
        return (long) javascriptExecutor.executeScript("return window.innerWidth");
    }

    @Override
    public long viewPortHeight() {
        return (long) javascriptExecutor.executeScript("return window.innerHeight");
    }

    @Override
    public void executeScript() {
        waitForLoad();
        javascriptExecutor.executeScript(htmlcs.getHTMLCS());
        javascriptExecutor.executeScript(String.format(Statik.RUNNER, Accessibility.STANDARD));
    }

    public void waitForLoad() {
        ExpectedCondition<Boolean> pageLoadCondition = webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete");
        WebDriverWait wait = new WebDriverWait(driver, 30);
        wait.until(pageLoadCondition);
    }

    @Override
    public String viewPort() {
        return viewPortWidth() + " X " + viewPortHeight();
    }

    @Override
    public String url() {
        return driver.getCurrentUrl();
    }

    @Override
    public String device() {
        int width = (int) viewPortWidth();
        if (width < 768) {
            return "Phone";
        } else if (width < 992) {
            return "Tablet";
        } else if (width < 1200) {
            return "Small Laptop";
        } else {
            return "Laptop/Desktop";
        }
    }

    @Override
    public String browserName() {
        Capabilities capabilities = ((RemoteWebDriver) driver).getCapabilities();
        return upperCaseFirst(capabilities.getBrowserName()) + " " + capabilities.getVersion();
    }

    private String upperCaseFirst(String name) {
        char[] array = name.toCharArray();
        array[0] = Character.toUpperCase(array[0]);
        return new String(array);
    }

}

package fr.asenka.webchecker;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.Closeable;
import java.util.concurrent.TimeUnit;

public class BrowserService implements Closeable {

    private final WebDriver webDriver;
    private final int timeout;

    public BrowserService(String webDriverPath, int timeout, String... options) {

        System.setProperty("webdriver.chrome.driver", webDriverPath);

        ChromeOptions driverOptions = new ChromeOptions();

        driverOptions.addArguments(randomUserAgent());

        for (String option : options)
            driverOptions.addArguments(option);

        this.webDriver = new ChromeDriver(driverOptions);
        this.webDriver.manage().window().setSize(new Dimension(1440,900));
        this.webDriver.manage().window().setPosition(new Point(150, 150));
        this.timeout = timeout;
    }

    public String getHtmlContent(String url) throws Exception {

        webDriver.navigate().to(url);

        TimeUnit.MILLISECONDS.sleep(timeout);

        return webDriver.getPageSource();
    }

    public String randomUserAgent() {
        return "--user-agent=" + RandomUserAgent.getRandomUserAgent();
    }

    @Override
    public void close() {

        if (webDriver != null)
            webDriver.close();
    }
}

package fr.asenka.webchecker;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Slf4j
public class CheckerService {

    private final BrowserService browser;
    private final int delayAfterCheck;

    public CheckerService(BrowserService browser, int delayAfterCheck) {
        this.browser = browser;
        this.delayAfterCheck = delayAfterCheck;
    }

    public Result check(Inputs inputs) {

        try {
            String htmlContent = browser.getHtmlContent(inputs.getUrl());
            Inputs.Algorithm algorithm = inputs.getCheckingAlgorithm();
            return algorithm.check(htmlContent);
        } catch (Exception e) {
            return Result.builder()
                    .error(true)
                    .exception(e)
                    .build();
        } finally {
            sleep(delayAfterCheck);
        }
    }

    private static void sleep(int delay) {
        try {
            TimeUnit.MILLISECONDS.sleep(delay);
        } catch (InterruptedException e) {
            log.error("Unexpected error!", e);
        }
    }
}

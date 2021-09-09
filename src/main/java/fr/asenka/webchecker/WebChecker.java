package fr.asenka.webchecker;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class WebChecker {

    public static final String PS_5_EDITION_DIGITAL = "ps5-edition-digital";
    public static final String STANDARD = "ps5-edition-standard";
    public static final String DIGITAL = "ps5-edition-digital";

    public static void main(String[] args) {

        int delayBetweenChecks = 5 * 1000; // 5 seconds
        int pageLoadingTimeout = 1 * 1000; // 1 second
        String webDriverPath = "C:\\works\\new\\perso\\java-utils\\drivers\\87\\chromedriver.exe";

        while(true) {

            try (BrowserService browserService = new BrowserService(webDriverPath, pageLoadingTimeout)) {
                CheckerService checkerService = new CheckerService(browserService, delayBetweenChecks);

                for (Inputs inputs : listOfInputs()) {
                    Result result = checkerService.check(inputs);

                    if (result.isAvailable()) {
                        Float price = result.getPrice();

                        if (price != null)
                            log.info("OK ! - {}, {} => {} - Price={} - url={}", inputs.getMerchant(), inputs.getName(), result.getMessage(), result.getPrice(), inputs.getUrl());
                        else
                            log.info("OK ! - {}, {} => {} - url={}", inputs.getMerchant(), inputs.getName(), result.getMessage(), inputs.getUrl());
                    } else if (result.isUnavailable()) {
                        log.info("No... - {}, {} => {}", inputs.getMerchant(), inputs.getName(), result.getMessage());
                    } else {
                        log.info("??? - {}, {} => {}", inputs.getMerchant(), inputs.getName(), result.getMessage());
                    }
                }
            }

        }
    }

    private static List<Inputs> listOfInputs() {
        return List.of(
                culturaDigital(),
                amazonDigital(),
                culturaStandard(),
                amazonStandard()
        );
    }

    private static Inputs culturaDigital() {
        return Inputs.builder()
                .merchant("Cultura")
                .name(DIGITAL)
                .url("https://www.cultura.com/playstation-5-edition-digitale-version-sans-lecteur-optique-0711719395300.html")
                .checkingAlgorithm(html -> Result.notSure())
                .build();
    }

    private static Inputs culturaStandard() {
        return Inputs.builder()
                .merchant("Cultura")
                .name(STANDARD)
                .url("https://www.cultura.com/playstation-5-edition-standard-0711719395201.html")
                .checkingAlgorithm(html -> Result.notSure())
                .build();
    }

    private static Inputs amazonDigital() {
        return Inputs.builder()
                .merchant("Amazon FR")
                .name(DIGITAL)
                .url("https://www.amazon.fr/PlayStation-Digital-Manette-DualSense-Couleur/dp/B08H98GVK8")
                .checkingAlgorithm(html -> Result.notSure())
                .build();
    }

    private static Inputs amazonStandard() {
        return Inputs.builder()
                .merchant("Amazon FR")
                .name(STANDARD)
                .url("https://www.amazon.fr/PlayStation-%C3%89dition-Standard-DualSense-Couleur/dp/B08H93ZRK9")
                .checkingAlgorithm(html -> Result.notSure())
                .build();
    }


}

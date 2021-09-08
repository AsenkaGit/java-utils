package fr.asenka.webchecker;

import fr.asenka.webchecker.checker.CheckerService;
import fr.asenka.webchecker.checker.Inputs;
import fr.asenka.webchecker.checker.Proxy;
import fr.asenka.webchecker.checker.Result;
import org.apache.commons.lang3.StringUtils;

public class WebChecker {

    public static void main(String[] args) {


        CheckerService service = new CheckerService();

        Result result = service.check(Inputs.builder()
                .url("")
                .checking(content -> Result.builder()
                        .available(!unavailable(content))
                        .build()
                )
                .build());

        System.out.println(result);
    }

    private static boolean unavailable(String content) {

        System.out.println(content);

        if (StringUtils.containsIgnoreCase(content, "unavailable"))
            return true;

        if (StringUtils.containsIgnoreCase(content, "indisponible"))
            return true;

        return false;
    }
}

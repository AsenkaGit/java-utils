package fr.asenka.webchecker.checker;

import java.io.IOException;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.function.Function;

import static java.time.temporal.ChronoUnit.SECONDS;

public class CheckerService {

    private static final int TIMEOUT = 10;

    private final HttpClient httpClient;


    public CheckerService() {
        this(null);
    }

    public CheckerService(Proxy proxy) {

        HttpClient.Builder clientBuilder = HttpClient.newBuilder();

        if (proxy != null) {
            clientBuilder.proxy(ProxySelector.of(new InetSocketAddress(proxy.getHost(), proxy.getPort())));

            if (proxy.needsAuthentication()) {
                System.setProperty("jdk.http.auth.tunneling.disabledSchemes", "");

                clientBuilder.authenticator(new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(proxy.getUsername(), proxy.getPassword().toCharArray());
                    }
                });
            }
        }
        httpClient = clientBuilder.build();
    }

    private String getHtmlContent(Inputs inputs) throws URISyntaxException, IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(new URI(inputs.getUrl()))
                .timeout(Duration.of(inputs.getTimeout(), SECONDS))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        int statusCode = response.statusCode();

        if (statusCode == 200)
            return response.body();
        else
            throw new IOException("Impossible de joindre: " + inputs + " - reponse=" + response);
    }

    public Result check(Inputs inputs) {

        try {
            String htmlContent = getHtmlContent(inputs);
            Function<String, Result> checker = inputs.getChecking();

            return checker.apply(htmlContent);
        } catch (Exception e) {
            return Result.builder()
                    .error(true)
                    .exception(e)
                    .build();
        }
    }
}

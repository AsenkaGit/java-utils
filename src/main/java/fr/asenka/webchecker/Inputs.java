package fr.asenka.webchecker;

import lombok.*;

import java.util.function.Function;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(of = {"name", "url"})
public class Inputs {

    private String name;
    private String merchant;
    private String url;
    private Algorithm checkingAlgorithm;

    @FunctionalInterface
    public interface Algorithm {
        Result check(String htmlContent) throws Exception;
    }
}

package fr.asenka.webchecker.checker;

import lombok.*;

import java.util.function.Function;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(of = {"name", "url"})
public class Inputs {

    private String name;
    private String url;
    private Function<String, Result> checking;

    @Builder.Default
    private int timeout = 5;
}

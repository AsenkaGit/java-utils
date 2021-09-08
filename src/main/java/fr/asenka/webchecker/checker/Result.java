package fr.asenka.webchecker.checker;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Result {
    private boolean available;
    private Float price;
    private String link;
    private boolean error;
    private Exception exception;
}

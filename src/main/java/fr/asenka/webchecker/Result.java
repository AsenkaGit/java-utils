package fr.asenka.webchecker;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Result {
    private boolean available;
    private boolean unavailable;
    private Float price;
    private boolean error;
    private Exception exception;
    private String message;

    public static Result notSure() {
        return Result.builder()
                .message("Maybe yes, maybe no...")
                .build();
    }

    public static Result unavailable() {
        return Result.builder()
                .unavailable(true)
                .message("Not available !")
                .build();
    }

    public static Result available() {
        return Result.builder()
                .available(true)
                .message("GO GO GO !!! (but check the price before...)")
                .build();
    }

    public static Result available(Float price) {
        return Result.builder()
                .available(true)
                .message("GO GO GO !!!")
                .price(price)
                .build();
    }

    public static Result error(String error) {
        return Result.builder()
                .error(true)
                .message(error)
                .build();
    }

    public static Result error(Exception e) {
        return Result.builder()
                .error(true)
                .exception(e)
                .message(e.getMessage())
                .build();
    }
}

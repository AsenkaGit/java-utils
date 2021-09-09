package fr.asenka.webchecker;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Proxy {
    private String host;
    private int port;
    private String username;
    private String password;

    public boolean needsAuthentication() {
        return username != null && password != null;
    }
}

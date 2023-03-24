package ch.hftm.blog.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor @AllArgsConstructor
@Builder
@Getter
public class SecretConfig {

    private String encryptionKey;
    private String salt;

}

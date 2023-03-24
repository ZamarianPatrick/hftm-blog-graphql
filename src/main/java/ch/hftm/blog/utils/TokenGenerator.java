package ch.hftm.blog.utils;

import ch.hftm.blog.entity.User;
import io.smallrye.jwt.build.Jwt;
import io.smallrye.jwt.build.JwtClaimsBuilder;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.Dependent;
import java.util.Date;

@Dependent
public class TokenGenerator {

    private static final int SESSION_MINUTES = 6 * 60;

    @ConfigProperty(name = "mp.jwt.verify.issuer")
    String issuer;

    public String generateSessionToken(User user) {
        JwtClaimsBuilder builder1 = Jwt.claims()
                .issuer(issuer)
                .groups(user.getRole().toString())
                .upn(user.getName())
                .expiresAt((new Date().getTime()/1000) + SESSION_MINUTES * 60);

        return builder1.sign();
    }
}


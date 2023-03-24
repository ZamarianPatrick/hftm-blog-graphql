package ch.hftm.blog.resource;

import ch.hftm.blog.BlogBackend;
import ch.hftm.blog.entity.User;
import ch.hftm.blog.entity.UserRole;
import ch.hftm.blog.utils.TokenGenerator;
import org.eclipse.microprofile.graphql.Description;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.GraphQLException;
import org.eclipse.microprofile.graphql.Mutation;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@GraphQLApi
public class AuthResource {

    @Inject
    TokenGenerator tokenGenerator;

    @Mutation("signUp")
    @Description("register a new user account and get a jwt token")
    @Transactional
    public String signUp (@NotNull @Size(min = 5, max = 32) String userName,
                           @NotNull @Size(min = 8, max = 32) String password) throws GraphQLException {

        if (User.find("name", userName).firstResultOptional().isPresent()) {
            throw new GraphQLException("userName already taken");
        }

        String hashedPassword;

        try {
            hashedPassword = BlogBackend.getEncryptor().hashPassword(password);
        } catch (Exception e) {
            e.printStackTrace();
            throw new GraphQLException("internal error");
        }

        User user = User.builder()
                .name(userName)
                .password(hashedPassword)
                .role(UserRole.USER)
                .build();

        user.persist();

        return tokenGenerator.generateSessionToken(user);
    }

    @Mutation("login")
    @Description("get a jwt token for an existing user")
    public String login (@NotNull String userName, @NotNull String password) throws GraphQLException {

        User user = User.find("name", userName).firstResult();
        if (user == null) {
            throw new GraphQLException("invalid login");
        }

        String hashedPassword;

        try {
            hashedPassword = BlogBackend.getEncryptor().hashPassword(password);
        } catch (Exception e) {
            e.printStackTrace();
            throw new GraphQLException("internal error");
        }

        if (!user.getPassword().equals(hashedPassword)) {
            throw new GraphQLException("invalid login");
        }

        return tokenGenerator.generateSessionToken(user);
    }

}

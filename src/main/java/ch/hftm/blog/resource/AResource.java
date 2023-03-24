package ch.hftm.blog.resource;

import ch.hftm.blog.entity.User;
import io.quarkus.security.identity.SecurityIdentity;
import org.eclipse.microprofile.graphql.GraphQLException;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

@RequestScoped
public class AResource {

    @Inject
    SecurityIdentity identity;


    public User mustUser () throws GraphQLException {
        String userName = identity.getPrincipal().getName();
        if (userName == null || userName.isEmpty()) {
            throw new GraphQLException("not allowed");
        }

        User user = User.find("name", userName).firstResult();
        if (user == null) {
            throw new GraphQLException("not allowed");
        }

        return user;
    }

}

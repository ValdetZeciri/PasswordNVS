package org.acme;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;

@Path("/users")
public class UserResource {
    @Inject
    EntityManager entityManager;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Hello from RESTEasy Reactive";
    }

    @Inject
    UserRepository userRepository;

    @POST
    @Path("/add")
    public Response add(User internship) {
        final User createdUser = userRepository.create(internship);
        final URI ressourceUri = URI.create("/internships" + createdUser.getId());
        return Response.created(ressourceUri).build();
    }

    @POST
    @Path("/check")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String checkPasswordForUser(User user) {
        try{
            User retUser = entityManager.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class)
                    .setParameter("username", user.username)
                    .getSingleResult();
            return "ok";
        }catch (NoResultException e){
            System.out.println("User gibt es nicht");
            return "geht nicht";
        }

    }
}
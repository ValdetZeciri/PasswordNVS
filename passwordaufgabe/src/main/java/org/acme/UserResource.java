package org.acme;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

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
    @Transactional
    @Path("/add")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response add(User user) {
        try{
            User retUser = entityManager.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class)
                    .setParameter("username", user.username)
                    .getSingleResult();
            System.out.println("User gibt es schon");
            return Response.ok("User gibt es schon").build();
        }catch (NoResultException e){
            System.out.println("User gibt es noch nicht");
        }

        final User createdUser = userRepository.create(user);
        final URI ressourceUri = URI.create("/user" + createdUser.getId());
        return Response.created(ressourceUri).build();
        //return "hinzugefügt";
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

            //MessageDigest digest = MessageDigest.getInstance("SHA-256");
            //byte[] hash = digest.digest(retUser.getHashedPassword().getBytes(StandardCharsets.UTF_8));
            //String encoded = Base64.getEncoder().encodeToString(hash);

            String[] parts = retUser.getHashedPassword().split("\\$");
            byte[] salt = Base64.getDecoder().decode(parts[0]);
            byte[] hash1 = userRepository.hashWithSalt(user.getHashedPassword(), salt);
            //hashedPassword.equals(Base64.getEncoder().encodeToString(salt) + "$" + Base64.getEncoder().encodeToString(hash));
            //System.out.println(retUser.getHashedPassword());
            //System.out.printf(Base64.getEncoder().encodeToString(salt) + "$" + Base64.getEncoder().encodeToString(hash1));

            if (retUser.getHashedPassword().equals(Base64.getEncoder().encodeToString(salt) + "$" + Base64.getEncoder().encodeToString(hash1))){
                System.out.println("Passwort stimmt");
                return "passwort stimmt";
            }else{
                System.out.println("Passwort stimmt nicht");
                return "passwort falsch";
            }
        }catch (NoResultException e){
            System.out.println("User gibt es nicht");
            return "geht nicht";
        }

    }
}
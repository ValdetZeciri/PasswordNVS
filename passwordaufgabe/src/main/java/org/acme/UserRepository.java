package org.acme;

import lombok.NonNull;
import org.acme.User;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.transaction.Transactional;
import javax.ws.rs.core.Response;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Random;
import java.util.Scanner;

@ApplicationScoped
public class UserRepository {
    @Inject
    EntityManager entityManager;

    @Transactional
    public User create(User user) {
        //String hashedPassword = sha256((user.getHashedPassword()));
        String hashedPassword = hashPassword(user.getHashedPassword());
        user.setHashedPassword(hashedPassword);
        entityManager.persist(user);
        entityManager.flush();
        return user;
    }

    public static String sha256(final String base) {
        try {
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[16];
            random.nextBytes(salt);

            final MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            final byte[] hash = md.digest(base.getBytes("UTF-8"));
            final StringBuilder hexString = new StringBuilder();
            for (int i = 0; i < hash.length; i++) {
                final String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String hashPassword(String password) {
        byte[] salt = generateSalt();
        byte[] hash = hashWithSalt(password, salt);
        return Base64.getEncoder().encodeToString(salt) + "$" + Base64.getEncoder().encodeToString(hash);
    }

    private static byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }

    public static byte[] hashWithSalt(String password, byte[] salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.reset();
            digest.update(salt);
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return hash;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public void resetPassword(String userName){
        int count = 0;
        boolean test = false;
        try{
            User retUser = entityManager.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class)
                    .setParameter("username", userName)
                    .getSingleResult();
            do{
                Random random = new Random();
                String generatedString = String.format("%04d", random.nextInt(10000));
                System.out.println(generatedString);

                Scanner sc= new Scanner(System.in);
                System.out.println("Bitte geben Sie den den einmaligen Code ein:");
                String input = sc.nextLine();

                if(input.equals(generatedString)){
                    System.out.println("Bitte geben Sie ihr neues Password ein:");
                    String newPassword = sc.nextLine();
                    updatePassword(userName, newPassword);
                    System.out.println("Password aktualisiert!");
                    test=true;
                }
                else{
                    System.out.println("Falsche Eingabe!");
                    count++;
                }
            }while(test==false&&count<3);
        }catch (NoResultException e){
            System.out.println("User gibt es noch nicht");
        }


    }

    @Transactional
    public void updatePassword(String userName, String newPassword){
        String hashedPassword = hashPassword(newPassword);

        entityManager.createQuery("UPDATE User SET hashedPassword = :newPassword WHERE username = :userName").setParameter("userName", userName).setParameter("newPassword", hashedPassword).executeUpdate();
    }
}
package org.acme;

import lombok.NonNull;
import org.acme.User;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.security.MessageDigest;

@ApplicationScoped
public class UserRepository {
    @Inject
    EntityManager entityManager;

    @Transactional
    public User create(User user) {
        String hashedPassword = sha256((user.getHashedPassword()));
        user.setHashedPassword(hashedPassword);
        entityManager.persist(user);
        return user;
    }

    public static String sha256(final String base) {
        try {
            final MessageDigest digest = MessageDigest.getInstance("SHA-256");
            final byte[] hash = digest.digest(base.getBytes("UTF-8"));
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
}
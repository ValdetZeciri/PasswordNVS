package org.acme;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@Entity
@Getter
@Setter
@Table(name = "userPass")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String hashedPassword;

    public String username;
    public String telephoneNumber;
}

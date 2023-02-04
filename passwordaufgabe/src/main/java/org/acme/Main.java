package org.acme;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;

import javax.inject.Inject;
import java.util.Scanner;

@QuarkusMain
public class Main {
    public static void main(String... args) {
        Quarkus.run(MyApp.class, args);
    }


    public static class MyApp implements QuarkusApplication {
        @Inject
        UserRepository userRepository;

        @Override
        public int run(String... args) throws Exception {
            System.out.println("Do startup logic here");
            User user = new User();
            user.setUsername("Valdet");
            user.setHashedPassword("test1234");
            user.setTelephoneNumber("06766105547");
            userRepository.create(user);
            userRepository.resetPassword("Valdet");
            Quarkus.waitForExit();
            return 0;
        }
    }
}

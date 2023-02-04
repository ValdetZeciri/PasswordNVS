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
            String input="";
            do {
                Scanner scanner = new Scanner(System.in);
                System.out.println("1. Password vergessen");
                System.out.println("0. Abbrechen");
                input = scanner.nextLine();
                if(input.equals("1")){
                    System.out.println("Bitte geben Sie ihren Username ein: ");
                    String userName = scanner.nextLine();
                    userRepository.resetPassword(userName);
                }
            }while (input.equals("0"));

            Quarkus.waitForExit();
            return 0;
        }
    }
}

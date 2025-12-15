package org.yearup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MarqPlexGames {

    public static void main(String[] args) {

        if(args.length != 2){
            System.out.println("Need to include user and password");
            System.exit(1);
        }else{
            System.setProperty("dbUsername", args[0]);
            System.setProperty("dbPassword", args[1]);
            SpringApplication.run(MarqPlexGames.class, args);
        }
    }

}

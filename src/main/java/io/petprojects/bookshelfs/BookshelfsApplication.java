package io.petprojects.bookshelfs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class BookshelfsApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookshelfsApplication.class, args);
    }

}

package com.dxvalley.nedajpaymnetbackend;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@OpenAPIDefinition(
        info = @Info(
                title = "My API",
                version = "1.0",
                description = "My API documentation"
        )
)
public class PaymentServicesApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentServicesApplication.class, args);
    }

}

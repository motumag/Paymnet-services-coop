package com.dxvalley.nedajpaymnetbackend;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@OpenAPIDefinition(
        info = @Info(
                title = "Payment Service",
                version = "1.0",
                description = "Payment Service Backend @CoopBank",
                contact = @Contact(
                        name = "Motuma Gishu",
                        email = "motumag@coopbankoromia.com.et",
                        url = "https://www.coopbankoromia.com"
                ),
                license = @License(
                        name = "Apache 2.0",
                        url = "https://www.apache.org/licenses/LICENSE-2.0"
                )
        ),
        security = {
                @SecurityRequirement(name = "bearerToken")
        }
)
public class PaymentServicesApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentServicesApplication.class, args);
    }

}

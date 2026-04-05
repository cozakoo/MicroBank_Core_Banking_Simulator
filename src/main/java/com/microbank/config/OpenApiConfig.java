package com.microbank.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("MicroBank Core Banking Simulator API")
                        .description("API REST completa para simulación de operaciones bancarias con transacciones ACID, " +
                                "integridad de datos, auditoría completa y gestión segura de cuentas.")
                        .version("0.2.0")
                        .contact(new Contact()
                                .name("MicroBank Development Team")
                                .email("martinarcosvargas2@gmail.com")
                                .url("https://github.com/cozakoo/MicroBank_Core_Banking_Simulator"))
                        .license(new License()
                                .name("MIT")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Desarrollo local"),
                        new Server()
                                .url("https://api.microbank.dev")
                                .description("Ambiente de producción")
                ));
    }
}

package co.com.crediya.api.config;

import co.com.crediya.api.constants.swagger.DocApi;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI().info(new Info()
                .title(DocApi.TITLE)
                .version(DocApi.VERSION)
                .description(DocApi.DESCRIPTION));
    }
}

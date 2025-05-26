package com.iecube.iecubetutorial.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("iecubeTutorial API")
                        .version("1.0.0")  // API 的版本号，格式通常为语义化版本（SemVer）。
//                        .description("Description") //对 API 功能的描述。
//                        .termsOfService("http://terms-url.com") // 服务条款的 URL。
//                        .contact(new io.swagger.v3.oas.models.info.Contact() // 设置 API 维护者的联系信息
//                                .email("contact@your-company.com")
//                                .name("Contact Name")
//                                .url("http://contact-url.com"))
//                        .license(new io.swagger.v3.oas.models.info.License() //设置 API 的许可证信息
//                                .name("License")
//                                .url("http://license-url.com"))
                );
    }
}
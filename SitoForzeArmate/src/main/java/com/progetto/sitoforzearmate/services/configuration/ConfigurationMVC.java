package com.progetto.sitoforzearmate.services.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

public class ConfigurationMVC {
    @Configuration
    public class MVCConfiguration implements WebMvcConfigurer{

        private static final String[] CLASSPATH_RESOURCE_LOCATIONS = {
                "classpath:/static/",
        };

        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry){
            registry.addResourceHandler("/**").addResourceLocations(CLASSPATH_RESOURCE_LOCATIONS);
        }

        @Bean
        public MultipartResolver multipartResolver(){
            return new StandardServletMultipartResolver();
        }
    }
}

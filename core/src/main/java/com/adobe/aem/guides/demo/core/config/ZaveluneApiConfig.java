package com.adobe.aem.guides.demo.core.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(
    name = "ZAVELUNE API Configuration",
    description = "Configuration for ZAVELUNE API details"
)
public @interface ZaveluneApiConfig {

    @AttributeDefinition(name = "Client ID")
    String clientId();

    @AttributeDefinition(
        name = "API Token",
        type = AttributeType.PASSWORD
    )
    String apiToken();

    @AttributeDefinition(name = "Page Path")
    String pagePath();
}
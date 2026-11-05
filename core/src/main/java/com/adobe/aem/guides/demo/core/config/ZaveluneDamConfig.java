package com.adobe.aem.guides.demo.core.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(
    name = "ZAVELUNE DAM Asset Publishing Configuration",
    description = "Configuration for monitoring DAM asset changes"
)
public @interface ZaveluneDamConfig {

    @AttributeDefinition(
        name = "DAM Folder Path",
        description = "DAM folder whose assets should be monitored"
    )
    String dam_folder_path() default "/content/dam/zavelune";
}
package com.adobe.aem.guides.demo.core.osgi;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition (description = "This is Eloqua Configuration INterface")

public @interface EloquaConfiguration {

    @AttributeDefinition (required = true,
                        type = AttributeType.STRING,
                        name = "Username",
                        description = "This is username description",
                        defaultValue = "Anu")
    public String username() default "Anu";

    @AttributeDefinition (required = true,
                        type = AttributeType.LONG,
                        name = "Client Id",
                        description = "This is clientid description",
                        defaultValue = "741852")
    public long clientid() default 741852;

    @AttributeDefinition (required = true,
                        type = AttributeType.LONG,
                        name = "Client Secret",
                        description = "This is clientsecret description",
                        defaultValue = "963852")
    public long clientsecret() default 963852;

     @AttributeDefinition (required = true,
                        type = AttributeType.STRING,
                        name = "Rest API",
                        description = "This is restapi description",
                        defaultValue = "www.eloqua.com/api/form/3")
    public String restapi() default "www.eloqua.com/api/form/3";


    
}

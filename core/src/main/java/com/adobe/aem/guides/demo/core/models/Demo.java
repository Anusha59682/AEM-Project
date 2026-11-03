
package com.adobe.aem.guides.demo.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(
    adaptables = Resource.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class Demo {

    @ValueMapValue
    private String fullname;

    @ValueMapValue
    private String number;

    public String getFullname() {
        return fullname != null ? fullname : "Default Name";
    }

    public String getNumber() {
        return number != null ? number : "0";
    }
}

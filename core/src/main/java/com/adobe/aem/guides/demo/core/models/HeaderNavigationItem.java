
package com.adobe.aem.guides.demo.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(
    adaptables = Resource.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class HeaderNavigationItem {

    @ValueMapValue
    private String label;

    @ValueMapValue
    private String linkPath;

    @ValueMapValue
    private String date;

    public String getLabel() {
        return label;
    }

    public String getLinkPath() {
        return linkPath;
    }

    public String getDate() {
        return date;
    }
}

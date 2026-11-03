
package com.adobe.aem.guides.demo.core.models;

import java.util.List;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(
    adaptables = Resource.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class HeaderComponent {

    @ValueMapValue
    private String brandName;

    @ValueMapValue
    private String logoPath;

    @ValueMapValue
    private Boolean headerEnabled;

    @ChildResource(name = "navigationItems")
    private List<HeaderNavigationItem> navigationItems;

    public String getBrandName() {
        return brandName;
    }

    public String getLogoPath() {
        return logoPath;
    }

    public boolean getHeaderEnabled() {
        return Boolean.TRUE.equals(headerEnabled);
    }

    public List<HeaderNavigationItem> getNavigationItems() {
        return navigationItems;
    }
}

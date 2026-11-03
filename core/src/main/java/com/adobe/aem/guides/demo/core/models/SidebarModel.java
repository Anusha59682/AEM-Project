
package com.adobe.aem.guides.demo.core.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;

import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(
    adaptables = Resource.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class SidebarModel {

    @Self
    private Resource resource;

    // Header fields
    @ValueMapValue
    private String logoPath;

    @ValueMapValue
    private String logoMobileImage;

    @ValueMapValue
    private String logoLink;

    @ValueMapValue
    private boolean enableSwitch;

    // Region field
    @ValueMapValue
    private String country;

    // Multifield lists
    private List<HeaderLink> headerLinks = new ArrayList<>();
    private List<SideBarNavigation> sideBarNavigation = new ArrayList<>();

    @PostConstruct
    protected void init() {

        // Read Header Links multifield
        Resource headerLinksResource =
            resource.getChild("headerLinks");

        if (headerLinksResource != null) {

            for (Resource item : headerLinksResource.getChildren()) {

                ValueMap values = item.getValueMap();

                HeaderLink link = new HeaderLink(
                    values.get("name", String.class),
                    values.get("image", String.class)
                );

                headerLinks.add(link);
            }
        }

        // Read Sidebar Navigation multifield
        Resource navigationResource =
            resource.getChild("sideBarNavigation");

        if (navigationResource != null) {

            for (Resource item : navigationResource.getChildren()) {

                ValueMap values = item.getValueMap();

                String desktopIcon =
                    values.get("desktopIcon", String.class);

                String mobileIcon =
                    values.get("mobileIcon", String.class);

                List<NavigationItem> navigationItems =
                    new ArrayList<>();

                // Read nested Navigation Items
                Resource nestedResource =
                    item.getChild("navigationItems");

                if (nestedResource != null) {

                    for (Resource nestedItem :
                            nestedResource.getChildren()) {

                        ValueMap nestedValues =
                            nestedItem.getValueMap();

                        String navigationName =
                            nestedValues.get(
                                "navigationName",
                                String.class
                            );

                        String navigationURL =
                            nestedValues.get(
                                "navigationURL",
                                String.class
                            );

                        NavigationItem navigationItem =
                            new NavigationItem(
                                navigationName,
                                navigationURL
                            );

                        navigationItems.add(navigationItem);
                    }
                }

                sideBarNavigation.add(
                    new SideBarNavigation(
                        desktopIcon,
                        mobileIcon,
                        navigationItems
                    )
                );
            }
        }
    }

    // Header getters
    public String getLogoPath() {
        return logoPath;
    }

    public String getLogoMobileImage() {
        return logoMobileImage;
    }

    public String getLogoLink() {
        return logoLink;
    }

    public boolean isEnableSwitch() {
        return enableSwitch;
    }

    // Region getter
    public String getCountry() {
        return country;
    }

    // Header Links getter
    public List<HeaderLink> getHeaderLinks() {
        return Collections.unmodifiableList(headerLinks);
    }

    // Sidebar Navigation getter
    public List<SideBarNavigation> getSideBarNavigation() {
        return Collections.unmodifiableList(sideBarNavigation);
    }

    // Inner class for Header Links
    public static class HeaderLink {

        private final String name;
        private final String image;

        public HeaderLink(String name, String image) {
            this.name = name;
            this.image = image;
        }

        public String getName() {
            return name;
        }

        public String getImage() {
            return image;
        }
    }

    // Inner class for Sidebar Navigation
    public static class SideBarNavigation {

        private final String desktopIcon;
        private final String mobileIcon;
        private final List<NavigationItem> navigationItems;

        public SideBarNavigation(
                String desktopIcon,
                String mobileIcon,
                List<NavigationItem> navigationItems) {

            this.desktopIcon = desktopIcon;
            this.mobileIcon = mobileIcon;
            this.navigationItems = navigationItems;
        }

        public String getDesktopIcon() {
            return desktopIcon;
        }

        public String getMobileIcon() {
            return mobileIcon;
        }

        public List<NavigationItem> getNavigationItems() {
            return Collections.unmodifiableList(navigationItems);
        }
    }
}

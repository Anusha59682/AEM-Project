
package com.adobe.aem.guides.demo.core.models;

public class NavigationItem {

    private final String navigationName;
    private final String navigationURL;

    public NavigationItem(
            String navigationName,
            String navigationURL) {

        this.navigationName = navigationName;
        this.navigationURL = navigationURL;
    }

    public String getNavigationName() {
        return navigationName;
    }

    public String getNavigationURL() {
        return navigationURL;
    }
}

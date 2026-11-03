package com.adobe.aem.guides.demo.core.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.sling.models.annotations.injectorspecific.SlingObject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(
    adaptables = Resource.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class ContactTableModel {

    @ValueMapValue
    private String heading;

    private List<Contact> contacts = new ArrayList<>();

    @SlingObject
    private Resource resource;

    @PostConstruct
    protected void init() {

        ResourceResolver resolver =
            resource.getResourceResolver();

        Resource root =
            resolver.getResource("/var/demo/contact-us");

        if (root == null) {
            return;
        }

        for (Resource child : root.getChildren()) {

            String id = child.getValueMap().get("id", String.class);
            String name = child.getValueMap().get("name", String.class);
            String email = child.getValueMap().get("email", String.class);
            String mobile = child.getValueMap().get("mobile", String.class);

            contacts.add(new Contact(id, name, email, mobile));
        }
    }

    public String getHeading() {
        return heading == null ? "Contact Records" : heading;
    }

    public List<Contact> getContacts() {
        return Collections.unmodifiableList(contacts);
    }

    public static class Contact {

        private final String id;
        private final String name;
        private final String email;
        private final String mobile;

        public Contact(
            String id,
            String name,
            String email,
            String mobile
        ) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.mobile = mobile;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }

        public String getMobile() {
            return mobile;
        }
    }
}
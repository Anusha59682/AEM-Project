
package com.adobe.aem.guides.demo.core.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;

import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;

@Model(
    adaptables = Resource.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class CarouselModel {

    @ChildResource(name = "products")
    private Resource productsResource;

    // Get all perfume products
    public List<Product> getProducts() {

        if (productsResource == null) {
            return Collections.emptyList();
        }

        List<Product> productList = new ArrayList<>();

        for (Resource resource : productsResource.getChildren()) {

            Product product = new Product(resource);

            productList.add(product);
        }

        return productList;
    }

    // Inner class for Product
    public static class Product {

        private String productName;
        private String productImage;

        private List<Note> notes;

        public Product(Resource resource) {

            ValueMap properties = resource.getValueMap();

            productName = properties.get("productName", String.class);

            productImage = properties.get("productImage", String.class);

            notes = new ArrayList<>();

            Resource notesResource = resource.getChild("notes");

            if (notesResource != null) {

                for (Resource noteResource : notesResource.getChildren()) {

                    Note note = new Note(noteResource);

                    notes.add(note);
                }
            }
        }

        public String getProductName() {
            return productName;
        }

        public String getProductImage() {
            return productImage;
        }

        public List<Note> getNotes() {
            return notes;
        }
    }

    // Inner class for Fragrance Note
    public static class Note {

        private String noteName;

        public Note(Resource resource) {

            ValueMap properties = resource.getValueMap();

            noteName = properties.get("noteName", String.class);
        }

        public String getNoteName() {
            return noteName;
        }
    }
}

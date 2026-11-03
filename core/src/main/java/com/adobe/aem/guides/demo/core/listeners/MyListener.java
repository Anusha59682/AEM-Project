package com.adobe.aem.guides.demo.core.listeners;

import java.util.List;

import org.apache.sling.api.resource.observation.ResourceChange;
import org.apache.sling.api.resource.observation.ResourceChangeListener;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component (service = ResourceChangeListener.class, property = {
    ResourceChangeListener.PATHS + "=/content/demo/us/en",
    ResourceChangeListener.CHANGES + "=ADDED",
    ResourceChangeListener.CHANGES + "=CHANGED",
    ResourceChangeListener.CHANGES + "=REMOVED"
})

public class MyListener implements  ResourceChangeListener{

    private static final Logger LOG = LoggerFactory.getLogger(MyListener.class);

    @Override 
    public  void onChange(List<ResourceChange> changes) {
        for(ResourceChange change:changes) {
            LOG.info("Resource change detected.");
            LOG.info("path: {}",change.getPath());
            LOG.info("change Type: {}",change.getType());
        }
    }
    
}

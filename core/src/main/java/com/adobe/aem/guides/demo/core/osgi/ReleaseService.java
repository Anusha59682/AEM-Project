package com.adobe.aem.guides.demo.core.osgi;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Component (immediate = true,
            enabled = true,
            name = "Release Service")

public class ReleaseService {

    private static final Logger LOG = LoggerFactory.getLogger(ReleaseService.class);

    @Reference 
    DemoService demoService;

    @Activate 
    public void activate()
    {
        LOG.info("Release Service Activate method is executed");
        LOG.info("This is msg from Demo Service Interface {}",demoService.getMessage());
    }

    @Deactivate 
    public void deactive()
    {
        LOG.info("Release Service Deactivate method is executed");
    }

    @Modified 
    public void modify()
    {
        LOG.info("Release Service is Modified.");
    }
    
}

package com.adobe.aem.guides.demo.core.osgi;


import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component (immediate = true,
            service = DemoService.class,
            enabled = true,
            name="This is Demo OSGI")
public class DemoServiceImpl implements DemoService{

    private static final Logger LOG = LoggerFactory.getLogger(DemoServiceImpl.class);

    @Activate 
    public void activate()
    {
        LOG.info("Demo Service Activate method is executed");
    }

    @Deactivate 
    public void deactive()
    {
        LOG.info("Demo Service Deactivate method is executed");
    }

    @Modified 
    public void modify()
    {
        LOG.info("Demo Service is Modified.");
    }

    @Override
    public String getMessage() {
        return "This is OSGI using interface implementation";
    }
}


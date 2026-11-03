package com.adobe.aem.guides.demo.core.osgi;



import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component (immediate = true,
            enabled = true,
            name = "This is EloquaService Implementation",
            service = EloquaServiceImpl.class)

@Designate (ocd = EloquaConfiguration.class)
public class EloquaServiceImpl {

    private static final Logger LOG = LoggerFactory.getLogger(EloquaServiceImpl.class);

    private String username;
    private long clientid;
    private long clentsecret;
    private String restapi;
    

    @Activate 
    public void activate(EloquaConfiguration config)
    {
        LOG.info("Eloqua Service Activate method is executed");
    }

    @Deactivate 
    public void deactive(EloquaConfiguration config)
    {
        LOG.info("Eloqua Service Deactivate method is executed");
    }

    @Modified 
    public void modify(EloquaConfiguration config)
    {
        LOG.info("Eloqua Service is Modified.");
    }
    
}

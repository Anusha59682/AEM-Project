package com.adobe.aem.guides.demo.core.events;

import java.util.Collections;
import java.util.Map;

import com.day.cq.replication.ReplicationAction;
import java.util.Arrays;

import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(
    service = EventHandler.class,
    immediate = true,
    property = {
        EventConstants.EVENT_TOPIC + "=com/day/cq/replication"
    }
)
public class PagePublishEventHandler implements EventHandler {

    private static final Logger LOG =
            LoggerFactory.getLogger(PagePublishEventHandler.class);

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Override
public void handleEvent(Event event) {

    String type = (String) event.getProperty("type");

    ReplicationAction action = ReplicationAction.fromEvent(event);

    String[] pagePaths = action != null ? action.getPaths() : null;

    LOG.info("PagePublishEventHandler received event. Type: {}, Paths: {}",
            type, Arrays.toString(pagePaths));

    if (!"ACTIVATE".equals(type)
            || pagePaths == null
            || pagePaths.length == 0) {
        return;
    }

    for (String pagePath : pagePaths) {

        if (pagePath == null || !pagePath.startsWith("/content/")) {
            continue;
        }

        LOG.info("Published page path: {}", pagePath);

        Map<String, Object> authInfo = Collections.singletonMap(
                ResourceResolverFactory.SUBSERVICE,
                "page-change-writer"
        );

        try (ResourceResolver resolver =
                resourceResolverFactory.getServiceResourceResolver(authInfo)) {

            Resource contentResource =
                    resolver.getResource(pagePath + "/jcr:content");

            if (contentResource == null) {
                LOG.warn("jcr:content not found for page: {}", pagePath);
                continue;
            }

            ModifiableValueMap properties =
                    contentResource.adaptTo(ModifiableValueMap.class);

            if (properties == null) {
                LOG.error("Unable to modify properties for: {}", pagePath);
                continue;
            }

            properties.put("changed", true);
            resolver.commit();

            LOG.info("Successfully added changed=true to: {}",
                    pagePath + "/jcr:content");

        } catch (org.apache.sling.api.resource.LoginException e) {
            LOG.error("Service user login failed.", e);

        } catch (PersistenceException e) {
            LOG.error("Unable to save changed property for: {}", pagePath, e);
        }
    }
}
}
package com.adobe.aem.guides.demo.core.services.impl;

import java.util.HashMap;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.Session;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.aem.guides.demo.core.services.ZaveluneApiConfigService;
import com.adobe.aem.guides.demo.core.services.ZavelunePageUpdateService;

@Component(service = ZavelunePageUpdateService.class)
public class ZavelunePageUpdateServiceImpl
        implements ZavelunePageUpdateService {

    private static final Logger LOG =
            LoggerFactory.getLogger(
                ZavelunePageUpdateServiceImpl.class);

    private static final String SUBSERVICE =
            "page-change-writer";

    @Reference
    private ResourceResolverFactory resolverFactory;

    @Reference
    private ZaveluneApiConfigService configService;

    @Override
    public boolean updatePageProperties() {

        String pagePath = configService.getPagePath();

        if (pagePath == null || pagePath.trim().isEmpty()) {
            LOG.error("Page path is empty");
            return false;
        }

        Map<String, Object> params = new HashMap<>();
        params.put(
            ResourceResolverFactory.SUBSERVICE,
            SUBSERVICE
        );

        try (ResourceResolver resolver =
                resolverFactory.getServiceResourceResolver(params)) {

            Resource pageResource = resolver.getResource(pagePath);

            if (pageResource == null) {
                LOG.error("Invalid page path: {}", pagePath);
                return false;
            }

            Resource contentResource =
                    resolver.getResource(
                        pagePath + "/jcr:content"
                    );

            if (contentResource == null) {
                LOG.error(
                    "jcr:content node not found for {}",
                    pagePath
                );
                return false;
            }

            Node contentNode =
                    contentResource.adaptTo(Node.class);

            if (contentNode == null) {
                LOG.error("Unable to access jcr:content node");
                return false;
            }

            contentNode.setProperty(
                "clientId",
                configService.getClientId()
            );

            contentNode.setProperty(
                "apiToken",
                configService.getApiToken()
            );

            Session session =
                    resolver.adaptTo(Session.class);

            if (session == null) {
                LOG.error("Unable to obtain JCR session");
                return false;
            }

            session.save();

            LOG.info(
                "Successfully updated API properties on {}",
                pagePath
            );

            return true;

        } catch (Exception e) {

            LOG.error(
                "Error updating page properties",
                e
            );

            return false;
        }
    }
}
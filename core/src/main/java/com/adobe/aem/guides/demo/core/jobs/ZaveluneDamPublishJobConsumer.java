package com.adobe.aem.guides.demo.core.jobs;

import java.util.HashMap;
import java.util.Map;

import javax.jcr.Session;

import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.consumer.JobConsumer;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.Replicator;

@Component(
    service = JobConsumer.class,
    property = {
        JobConsumer.PROPERTY_TOPICS + "=zavelune/dam/asset/publish"
    }
)
public class ZaveluneDamPublishJobConsumer implements JobConsumer {

    private static final String SUBSERVICE = "page-change-writer";

    @Reference
    private ResourceResolverFactory resolverFactory;

    @Reference
    private Replicator replicator;

    @Override
    public JobResult process(Job job) {

        String assetPath = job.getProperty("assetPath", String.class);

        if (assetPath == null || assetPath.trim().isEmpty()) {
            return JobResult.CANCEL;
        }

        Map<String, Object> authInfo = new HashMap<>();
        authInfo.put(
            ResourceResolverFactory.SUBSERVICE,
            SUBSERVICE
        );

        try (ResourceResolver resolver =
                 resolverFactory.getServiceResourceResolver(authInfo)) {

            Session session = resolver.adaptTo(Session.class);

            if (session == null) {
                return JobResult.FAILED;
            }

            // Check that the asset still exists.
            if (resolver.getResource(assetPath) == null) {
                return JobResult.CANCEL;
            }

            // Publish the updated DAM asset.
            replicator.replicate(
                session,
                ReplicationActionType.ACTIVATE,
                assetPath
            );

            return JobResult.OK;

        } catch (Exception e) {
            return JobResult.FAILED;
        }
    }
}
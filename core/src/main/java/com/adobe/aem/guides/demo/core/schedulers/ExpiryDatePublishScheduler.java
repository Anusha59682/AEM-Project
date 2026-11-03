
package com.adobe.aem.guides.demo.core.schedulers;

import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;

import javax.jcr.Session;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.commons.scheduler.ScheduleOptions;
import org.apache.sling.commons.scheduler.Scheduler;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationException;
import com.day.cq.replication.ReplicationStatus;
import com.day.cq.replication.Replicator;

@Component(service = Runnable.class, immediate = true)
@Designate(ocd = ExpiryDatePublishScheduler.Config.class)
public class ExpiryDatePublishScheduler implements Runnable {

    private static final Logger LOG =
        LoggerFactory.getLogger(ExpiryDatePublishScheduler.class);

    private static final String SCHEDULER_NAME =
        "ZAVELUNE Expiry Date Publish Scheduler";

    private static final String SUBSERVICE = "page-change-writer";

    private static final String QUERY =
        "SELECT * FROM [cq:PageContent] AS pageContent " +
        "WHERE ISDESCENDANTNODE(pageContent, '/content') " +
        "AND pageContent.[Expirydate] IS NOT NULL";

    @Reference
    private Scheduler scheduler;

    @Reference
    private ResourceResolverFactory resolverFactory;

    @Reference
    private Replicator replicator;

    private String cronExpression;

    @ObjectClassDefinition(
        name = "ZAVELUNE Expiry Date Publish Scheduler",
        description = "Publishes or unpublishes pages based on Expirydate"
    )
    public @interface Config {

        @AttributeDefinition(name = "Cron Expression")
        String cronExpression() default "0 */3 * * * ?";

    }

    @Activate
    @Modified
    protected void activate(Config config) {

        cronExpression = config.cronExpression();

        scheduler.unschedule(SCHEDULER_NAME);

        ScheduleOptions options = scheduler.EXPR(cronExpression);
        options.name(SCHEDULER_NAME);
        options.canRunConcurrently(false);

        scheduler.schedule(this, options);

        LOG.info("Expiry Date Scheduler configured.");
        LOG.info("Cron Expression: {}", cronExpression);
    }

    @Override
    public void run() {

        LOG.info("Expiry Date Scheduler started.");

        Map<String, Object> authInfo = Map.of(
            ResourceResolverFactory.SUBSERVICE, SUBSERVICE
        );

        try (ResourceResolver resolver =
                 resolverFactory.getServiceResourceResolver(authInfo)) {

            Session session = resolver.adaptTo(Session.class);

            if (session == null) {
                LOG.error("Unable to get JCR Session.");
                return;
            }

            Iterator<Resource> results =
                resolver.findResources(QUERY, "JCR-SQL2");

            int publishedCount = 0;
            int unpublishedCount = 0;

            Calendar now = Calendar.getInstance();

            while (results.hasNext()) {

                Resource contentResource = results.next();

                Calendar expiryDate = contentResource
                    .getValueMap()
                    .get("Expirydate", Calendar.class);

                if (expiryDate == null) {
                    continue;
                }

                String contentPath = contentResource.getPath();
                String pagePath = contentPath.substring(
                    0, contentPath.length() - "/jcr:content".length()
                );

                Resource pageResource = resolver.getResource(pagePath);

                if (pageResource == null) {
                    continue;
                }

                ReplicationStatus status =
                    pageResource.adaptTo(ReplicationStatus.class);

                boolean isPublished =
                    status != null && status.isActivated();

                try {

                    if (!expiryDate.before(now)) {

                        if (!isPublished) {
                            replicator.replicate(
                                session,
                                ReplicationActionType.ACTIVATE,
                                pagePath
                            );

                            LOG.info("Published page: {}", pagePath);
                            publishedCount++;
                        }

                    } else {

                        if (isPublished) {
                            replicator.replicate(
                                session,
                                ReplicationActionType.DEACTIVATE,
                                pagePath
                            );

                            LOG.info("Unpublished page: {}", pagePath);
                            unpublishedCount++;
                        }
                    }

                } catch (ReplicationException e) {
                    LOG.error(
                        "Replication failed for page: {}",
                        pagePath,
                        e
                    );
                }
            }

            LOG.info(
                "Expiry Scheduler completed. Published: {}, Unpublished: {}",
                publishedCount,
                unpublishedCount
            );

        } catch (Exception e) {
            LOG.error("Expiry Date Scheduler failed.", e);
        }
    }
}


package com.adobe.aem.guides.demo.core.schedulers;

import java.util.Iterator;
import java.util.Map;

import javax.jcr.Session;

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

import com.day.cq.replication.Replicator;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationException;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

@Component(
    service = Runnable.class,
    immediate = true
)
@Designate(ocd = PagePublishScheduler.Config.class)
public class PagePublishScheduler implements Runnable {

    private static final Logger LOG =
        LoggerFactory.getLogger(PagePublishScheduler.class);

    private static final String SCHEDULER_NAME =
        "ZAVELUNE Page Publish Scheduler";

    private static final String SUBSERVICE = "page-change-writer";

    @Reference
    private Scheduler scheduler;

    @Reference
    private ResourceResolverFactory resolverFactory;

    @Reference
    private Replicator replicator;

    private String parentPagePath;

    @ObjectClassDefinition(
        name = "ZAVELUNE Page Publish Scheduler",
        description = "Publishes child pages under the configured parent page"
    )
    public @interface Config {

        @AttributeDefinition(name = "Cron Expression")
        String cronExpression() default "0 */2 * * * ?";

        @AttributeDefinition(name = "Parent Page Path")
        String parent_page_path() default
            "/content/demo/us/en/zavelune-home";
    }

    @Activate
    @Modified
    protected void activate(Config config) {

        parentPagePath = config.parent_page_path();

        scheduler.unschedule(SCHEDULER_NAME);

        ScheduleOptions options =
            scheduler.EXPR(config.cronExpression());

        options.name(SCHEDULER_NAME);
        options.canRunConcurrently(false);

        scheduler.schedule(this, options);

        LOG.info("ZAVELUNE Scheduler configured.");
        LOG.info("Cron Expression: {}", config.cronExpression());
        LOG.info("Parent Page Path: {}", parentPagePath);
    }

    @Override
    public void run() {

        LOG.info("ZAVELUNE Page Publish Scheduler started.");

        Map<String, Object> authInfo = Map.of(
            ResourceResolverFactory.SUBSERVICE,
            SUBSERVICE
        );

        try (ResourceResolver resolver =
                 resolverFactory.getServiceResourceResolver(authInfo)) {

            PageManager pageManager =
                resolver.adaptTo(PageManager.class);

            if (pageManager == null) {
                LOG.error("Unable to get PageManager.");
                return;
            }

            Page parentPage = pageManager.getPage(parentPagePath);

            if (parentPage == null) {
                LOG.error("Parent page not found: {}", parentPagePath);
                return;
            }

            Session session = resolver.adaptTo(Session.class);

            if (session == null) {
                LOG.error("Unable to get JCR Session.");
                return;
            }

            Iterator<Page> childPages = parentPage.listChildren();

            int publishedCount = 0;

            while (childPages.hasNext()) {

                Page childPage = childPages.next();
                String childPath = childPage.getPath();

                try {
                    replicator.replicate(
                        session,
                        ReplicationActionType.ACTIVATE,
                        childPath
                    );

                    LOG.info("Successfully published page: {}", childPath);
                    publishedCount++;

                } catch (ReplicationException e) {
                    LOG.error(
                        "Failed to publish page: {}",
                        childPath,
                        e
                    );
                }
            }

            LOG.info(
                "Scheduler completed. Total pages published: {}",
                publishedCount
            );

        } catch (Exception e) {
            LOG.error("Scheduler execution failed.", e);
        }
    }
}

package com.adobe.aem.guides.demo.core.schedulers;

import org.apache.sling.commons.scheduler.ScheduleOptions;
import org.apache.sling.commons.scheduler.Scheduler;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.aem.guides.demo.core.services.ZavelunePageUpdateService;

@Component(
    service = Runnable.class,
    immediate = true
)
@Designate(ocd = ZaveluneApiConfigScheduler.Config.class)
public class ZaveluneApiConfigScheduler implements Runnable {

    private static final Logger LOG =
            LoggerFactory.getLogger(
                ZaveluneApiConfigScheduler.class);

    @ObjectClassDefinition(
        name = "ZAVELUNE API Config Scheduler"
    )
    public @interface Config {

        String cronExpression() default "0 0/5 * * * ?";
    }

    @Reference
    private Scheduler scheduler;

    @Reference
    private ZavelunePageUpdateService updateService;

    private static final String JOB_NAME =
            "ZAVELUNE API Config Scheduler";

    @Activate
    protected void activate(Config config) {

        ScheduleOptions options =
                scheduler.EXPR(config.cronExpression());

        options.name(JOB_NAME);
        options.canRunConcurrently(false);

        scheduler.schedule(this, options);

        LOG.info(
            "ZAVELUNE API scheduler started with cron: {}",
            config.cronExpression()
        );
    }

    @Override
    public void run() {

        LOG.info("ZAVELUNE API scheduler triggered");

        boolean result =
                updateService.updatePageProperties();

        if (result) {
            LOG.info("Scheduled page update successful");
        } else {
            LOG.error("Scheduled page update failed");
        }
    }

    @Deactivate
    protected void deactivate() {
        scheduler.unschedule(JOB_NAME);
        LOG.info("ZAVELUNE API scheduler stopped");
    }
}
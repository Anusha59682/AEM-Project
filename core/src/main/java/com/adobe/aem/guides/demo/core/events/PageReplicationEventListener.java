
package com.adobe.aem.guides.demo.core.events;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.replication.ReplicationAction;
import com.day.cq.replication.ReplicationActionType;

@Component(
    service = EventHandler.class,
    immediate = true,
    property = {
        "event.topics=com/day/cq/replication"
    }
)
public class PageReplicationEventListener implements EventHandler {

    private static final Logger LOG =
        LoggerFactory.getLogger(PageReplicationEventListener.class);

    @Override
    public void handleEvent(Event event) {

        ReplicationAction action =
            ReplicationAction.fromEvent(event);

        if (action == null) {
            LOG.warn("Unable to read replication event.");
            return;
        }

        ReplicationActionType actionType = action.getType();

        String[] pagePaths = action.getPaths();

        if (pagePaths == null) {
            return;
        }

        if (actionType == ReplicationActionType.ACTIVATE) {

            for (String pagePath : pagePaths) {
                LOG.info("PAGE PUBLISHED: {}", pagePath);
            }

        } else if (actionType == ReplicationActionType.DEACTIVATE) {

            for (String pagePath : pagePaths) {
                LOG.info("PAGE UNPUBLISHED: {}", pagePath);
            }
        }
    }
}

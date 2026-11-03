package com.adobe.aem.guides.demo.core.events;

import java.util.Collections;
import java.util.List;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.observation.ResourceChange;
import org.apache.sling.api.resource.observation.ResourceChangeListener;

import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.model.WorkflowModel;
import com.adobe.granite.workflow.exec.WorkflowData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(
    service = ResourceChangeListener.class,
    immediate = true,
    property = {
        ResourceChangeListener.PATHS + "=/content",
        ResourceChangeListener.CHANGES + "=ADDED"
    }
)
public class PageCreationEventHandler
        implements ResourceChangeListener {

    private static final Logger LOG =
            LoggerFactory.getLogger(PageCreationEventHandler.class);

    private static final String WORKFLOW_MODEL =
            "/var/workflow/models/zavelune-expiry-date-workflow";

    @Reference
    private ResourceResolverFactory resolverFactory;

    

    @Override
    public void onChange(List<ResourceChange> changes) {

        for (ResourceChange change : changes) {

            String path = change.getPath();

            // Only process a page's jcr:content node.
            if (!path.startsWith("/content/") ||
                    !path.endsWith("/jcr:content")) {
                continue;
            }

            String pagePath =
                    path.substring(0, path.length() - "/jcr:content".length());

            try (ResourceResolver resolver =
                    resolverFactory.getServiceResourceResolver(
                        Collections.singletonMap(
                            ResourceResolverFactory.SUBSERVICE,
                            "page-change-writer"))) {

                Resource content = resolver.getResource(path);

                if (content == null ||
                        content.getValueMap().get("cq:template", String.class)
                                == null) {
                    LOG.info("Skipping page without cq:template: {}", pagePath);
                    continue;
                }

                WorkflowSession workflowSession =
                    resolver.adaptTo(WorkflowSession.class);

                    if (workflowSession == null) {
                        LOG.error("Unable to obtain WorkflowSession for page: {}", pagePath);
                        continue;
                    }

                WorkflowModel model =
                        workflowSession.getModel(WORKFLOW_MODEL);

                if (model == null) {
                    LOG.error("Workflow model not found: {}", WORKFLOW_MODEL);
                    continue;
                }

                WorkflowData workflowData =
                        workflowSession.newWorkflowData("JCR_PATH", pagePath);

                workflowSession.startWorkflow(model, workflowData);

                LOG.info("Expiry date workflow started for page: {}", pagePath);

            } catch (Exception e) {
                LOG.error(
                    "Unable to start expiry date workflow for: {}",
                    pagePath, e);
            }
        }
    }
}
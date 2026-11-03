package com.adobe.aem.guides.demo.core.workflows;

import java.util.Calendar;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ModifiableValueMap;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;

@Component(
    service = WorkflowProcess.class,
    property = {
        "process.label=Set Expirydate Based on Template"
    }
)
public class ExpiryDateWorkflowProcess implements WorkflowProcess {

    private static final String PROJECT_TEMPLATE =
        "/conf/demo/settings/wcm/templates/zavelune-page-template";

    @Reference
    private ResourceResolverFactory resolverFactory;

    @Override
    public void execute(
            WorkItem workItem,
            WorkflowSession workflowSession,
            MetaDataMap args) throws WorkflowException {

        String pagePath = workItem.getWorkflow()
                .getWorkflowData()
                .getPayload()
                .toString();

        try (ResourceResolver resolver =
                resolverFactory.getServiceResourceResolver(
                    java.util.Collections.singletonMap(
                        ResourceResolverFactory.SUBSERVICE,
                        "page-change-writer"))) {

            Resource contentResource =
                    resolver.getResource(pagePath + "/jcr:content");

            if (contentResource == null) {
                throw new WorkflowException(
                    "Page jcr:content not found: " + pagePath);
            }

            ModifiableValueMap properties =
                    contentResource.adaptTo(ModifiableValueMap.class);

            if (properties == null) {
                throw new WorkflowException(
                    "Unable to read page properties: " + pagePath);
            }

            String template = properties.get("cq:template", String.class);

            Calendar expiryDate = Calendar.getInstance();

            if (template == null ||
                    !template.startsWith("/conf/demo/")) {

                expiryDate.add(Calendar.DAY_OF_MONTH, -1);
            }

            properties.put("Expirydate", expiryDate);

            resolver.commit();

        } catch (Exception e) {
            throw new WorkflowException(
                "Error setting Expirydate for " + pagePath, e);
        }
    }
}
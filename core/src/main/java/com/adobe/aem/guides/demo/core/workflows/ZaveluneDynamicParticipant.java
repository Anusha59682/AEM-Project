package com.adobe.aem.guides.demo.core.workflows;

import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.ParticipantStepChooser;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.metadata.MetaDataMap;

@Component(
    service = ParticipantStepChooser.class,
    property = {
        "chooser.label=ZAVELUNE Admin Participant Chooser"
    }
)
public class ZaveluneDynamicParticipant
        implements ParticipantStepChooser {

    private static final Logger LOG =
        LoggerFactory.getLogger(ZaveluneDynamicParticipant.class);

    @Override
    public String getParticipant(
            WorkItem workItem,
            WorkflowSession workflowSession,
            MetaDataMap args) throws WorkflowException {

        String pagePath =
            workItem.getWorkflowData().getPayload().toString();

        LOG.info("Dynamic Participant selected for page: {}", pagePath);
        LOG.info("Assigning workflow task to admin user.");

        return "admin";
    }
}
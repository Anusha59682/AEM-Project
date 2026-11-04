package com.adobe.aem.guides.demo.core.workflows;

import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;

@Component(
    service = WorkflowProcess.class,
    property = {
        "process.label=ZAVELUNE Page Change Logger"
    }
)
public class PageChangeWorkflowProcess implements WorkflowProcess {

    private static final Logger LOG =
        LoggerFactory.getLogger(PageChangeWorkflowProcess.class);

    @Override
    public void execute(
            WorkItem workItem,
            WorkflowSession workflowSession,
            MetaDataMap args) throws WorkflowException {

        String pagePath = workItem.getWorkflowData().getPayload().toString();

        LOG.info("ZAVELUNE PAGE CHANGE WORKFLOW EXECUTED: {}", pagePath);
    }
}
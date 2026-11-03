package com.adobe.aem.guides.demo.core.workflows;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationException;
import com.day.cq.replication.Replicator;

import java.util.HashMap;
import java.util.Map;

import javax.jcr.Session;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component (immediate = true,
    property = {
        "process.label=Demo Work Flow Page"
    },
    service = WorkflowProcess.class
)

public class demoFlowProcess implements WorkflowProcess {

    @Reference 
    Replicator replicator;

    @Reference 
    ResourceResolverFactory factory;


    @Override
    public void execute(WorkItem item, WorkflowSession wfsession, MetaDataMap args) throws WorkflowException {
        
        Map<String, Object> props = new HashMap<String, Object>();
        props.put(factory.SUBSERVICE, "npsubservice");
        String payload = item.getWorkflowData().getPayload().toString();


        try {
            ResourceResolver resolver = factory.getServiceResourceResolver(props);
            Session session = resolver.adaptTo(Session.class);
            replicator.replicate(session, ReplicationActionType.ACTIVATE, payload);
        } catch (LoginException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ReplicationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        };
    }
}

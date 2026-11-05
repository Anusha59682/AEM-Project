
package com.adobe.aem.guides.demo.core.listeners;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

import org.apache.sling.api.resource.observation.ResourceChange;
import org.apache.sling.api.resource.observation.ResourceChangeListener;
import org.apache.sling.event.jobs.JobManager;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;

import com.adobe.aem.guides.demo.core.config.ZaveluneDamConfig;

@Component(
    service = ZaveluneDamChangeListener.class,
    immediate = true
)
@Designate(ocd = ZaveluneDamConfig.class)
public class ZaveluneDamChangeListener
        implements ResourceChangeListener {

    private static final String JOB_TOPIC =
        "zavelune/dam/asset/publish";

    @Reference
    private JobManager jobManager;

    private ServiceRegistration<ResourceChangeListener> registration;

    private BundleContext bundleContext;

    private String damPath;

    @Activate
    protected void activate(
            ZaveluneDamConfig config,
            BundleContext context) {

        bundleContext = context;
        registerListener(config.dam_folder_path());
    }

    @Modified
    protected void modified(ZaveluneDamConfig config) {
        registerListener(config.dam_folder_path());
    }

    private void registerListener(String configuredPath) {

        if (registration != null) {
            registration.unregister();
            registration = null;
        }

        if (configuredPath == null ||
                configuredPath.trim().isEmpty()) {
            return;
        }

        damPath = configuredPath.replaceAll("/+$", "");

        Dictionary<String, Object> properties =
            new Hashtable<>();

        properties.put(
            ResourceChangeListener.PATHS,
            new String[] { damPath }
        );

        properties.put(
            ResourceChangeListener.CHANGES,
            new String[] { "ADDED", "CHANGED" }
        );

        registration = bundleContext.registerService(
            ResourceChangeListener.class,
            this,
            properties
        );
    }

    @Override
    public void onChange(List<ResourceChange> changes) {

        Set<String> assetPaths = new HashSet<>();

        for (ResourceChange change : changes) {

            String changedPath = change.getPath();

            if (changedPath == null ||
                    !changedPath.startsWith(damPath + "/")) {
                continue;
            }

            String assetPath = changedPath;

            int contentIndex =
                assetPath.indexOf("/jcr:content");

            if (contentIndex != -1) {
                assetPath =
                    assetPath.substring(0, contentIndex);
            }

            assetPaths.add(assetPath);
        }

        for (String assetPath : assetPaths) {

            Map<String, Object> properties = new HashMap<>();

            properties.put("assetPath", assetPath);

            jobManager.addJob(JOB_TOPIC, properties);
        }
    }

    @Deactivate
    protected void deactivate() {

        if (registration != null) {
            registration.unregister();
            registration = null;
        }
    }
}

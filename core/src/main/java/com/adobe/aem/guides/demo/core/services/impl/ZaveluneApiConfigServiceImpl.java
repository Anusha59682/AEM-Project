package com.adobe.aem.guides.demo.core.services.impl;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;

import com.adobe.aem.guides.demo.core.config.ZaveluneApiConfig;
import com.adobe.aem.guides.demo.core.services.ZaveluneApiConfigService;

@Component(service = ZaveluneApiConfigService.class)
@Designate(ocd = ZaveluneApiConfig.class)
public class ZaveluneApiConfigServiceImpl
        implements ZaveluneApiConfigService {

    private String clientId;
    private String apiToken;
    private String pagePath;

    @Activate
    protected void activate(ZaveluneApiConfig config) {

        clientId = config.clientId();
        apiToken = config.apiToken();
        pagePath = config.pagePath();
    }

    @Override
    public String getClientId() {
        return clientId;
    }

    @Override
    public String getApiToken() {
        return apiToken;
    }

    @Override
    public String getPagePath() {
        return pagePath;
    }
}
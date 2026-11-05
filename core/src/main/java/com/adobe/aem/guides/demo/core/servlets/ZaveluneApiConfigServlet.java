package com.adobe.aem.guides.demo.core.servlets;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.adobe.aem.guides.demo.core.services.ZavelunePageUpdateService;

@Component(service = Servlet.class)
@SlingServletPaths("/bin/zavelune/update-api-config")
public class ZaveluneApiConfigServlet
        extends SlingSafeMethodsServlet {

    private static final long serialVersionUID = 1L;

    @Reference
    private ZavelunePageUpdateService updateService;

    @Override
    protected void doGet(
            SlingHttpServletRequest request,
            SlingHttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        boolean result =
                updateService.updatePageProperties();

        if (result) {
            response.getWriter().write(
                "{\"status\":\"success\"," +
                "\"message\":\"Page properties updated\"}"
            );
        } else {
            response.setStatus(
                SlingHttpServletResponse.SC_BAD_REQUEST
            );

            response.getWriter().write(
                "{\"status\":\"error\"," +
                "\"message\":\"Page update failed\"}"
            );
        }
    }
}
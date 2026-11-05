package com.adobe.aem.guides.demo.core.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.osgi.service.component.annotations.Component;

import com.day.cq.dam.api.Asset;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component(service = Servlet.class)
@SlingServletPaths("/bin/demo/profiles")
public class ProfilesServlet extends SlingSafeMethodsServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(
            SlingHttpServletRequest request,
            SlingHttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        List<Map<String, String>> images = new ArrayList<>();

        ResourceResolver resolver = request.getResourceResolver();

        Resource folder = resolver.getResource("/content/dam/images");

        if (folder != null) {

            for (Resource child : folder.getChildren()) {

                Asset asset = child.adaptTo(Asset.class);

                if (asset != null) {

                    Map<String, String> image = new HashMap<>();

                    image.put("name", asset.getName());
                    image.put("path", asset.getPath());

                    images.add(image);
                }
            }
        }

        response.getWriter().write(
                new ObjectMapper().writeValueAsString(images));
    }
}
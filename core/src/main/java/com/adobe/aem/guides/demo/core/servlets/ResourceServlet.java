package com.adobe.aem.guides.demo.core.servlets;

import java.io.IOException;
import java.util.Iterator;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

@Component(
    service = Servlet.class,
    property = {
        "sling.servlet.resourceTypes=demo/components/anusha",
        "sling.servlet.selectors=recent",
        "sling.servlet.extensions=json",
        "sling.servlet.methods=GET"
    }
)
public class ResourceServlet extends SlingAllMethodsServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(
            SlingHttpServletRequest request,
            SlingHttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String rootPage = request.getParameter("RootPage");

        if (rootPage == null || rootPage.trim().isEmpty()) {
            rootPage = "/content/demo/us/en";
        }

        PageManager pageManager =
                request.getResourceResolver().adaptTo(PageManager.class);

        if (pageManager == null) {
            response.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(
                    "{\"error\":\"Unable to obtain PageManager\"}");
            return;
        }

        Page page = pageManager.getPage(rootPage);

        if (page == null) {
            response.setStatus(SlingHttpServletResponse.SC_NOT_FOUND);

            response.getWriter().write(
                    "{\"error\":\"Root page not found\"}");
            return;
        }

        Iterator<Page> childPages = page.listChildren();

        JsonArrayBuilder jsonArray = Json.createArrayBuilder();

        while (childPages.hasNext()) {
            Page childPage = childPages.next();

            JsonObjectBuilder objectBuilder = Json.createObjectBuilder();

            objectBuilder.add(
                    "text",
                    childPage.getTitle() != null
                            ? childPage.getTitle()
                            : "");

            objectBuilder.add("path", childPage.getPath());

            jsonArray.add(objectBuilder);
        }

        response.getWriter().write(jsonArray.build().toString());
    }
}
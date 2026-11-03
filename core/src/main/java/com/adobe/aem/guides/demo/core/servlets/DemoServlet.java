package com.adobe.aem.guides.demo.core.servlets;

import java.io.IOException;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;

@Component (service = Servlet.class,
    property = {
        "sling.servlet.paths=/bin/demo"
    }
)


public class DemoServlet extends SlingAllMethodsServlet{

    @Override
    protected void doGet( SlingHttpServletRequest request,  SlingHttpServletResponse response)
            throws ServletException, IOException {
            JsonObjectBuilder objectbuilder = Json.createObjectBuilder();
            objectbuilder.add("text","This is jsonobject");
            objectbuilder.add("type","online");   

            response.getWriter().write(objectbuilder.build().toString());
    }

    @Override
    protected void doPost( SlingHttpServletRequest request,  SlingHttpServletResponse response)
            throws ServletException, IOException {
        // TODO Auto-generated method stub
        super.doPost(request, response);
    }

    @Override
    protected void doPut(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {
        super.doPut(request, response);
    }
    
}

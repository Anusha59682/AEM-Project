
package com.adobe.aem.guides.demo.core.servlets;

import java.io.IOException;
import javax.servlet.Servlet;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;

@Component(
    service = Servlet.class,
    property = {
        "sling.servlet.methods=GET",
        "sling.servlet.resourceTypes=demo/components/testcomponent",
        "sling.servlet.extensions=json"
    }
)
public class Res extends SlingSafeMethodsServlet {

    private static final long serialVersionUID = 1L;

    private String escapeJson(String value) {
        if (value == null) {
            return "";
        }

        StringBuilder escaped = new StringBuilder();

        for (int i = 0; i < value.length(); i++) {
            char ch = value.charAt(i);

            switch (ch) {
                case '"':
                    escaped.append("\\\"");
                    break;
                case '\\':
                    escaped.append("\\\\");
                    break;
                case '\b':
                    escaped.append("\\b");
                    break;
                case '\f':
                    escaped.append("\\f");
                    break;
                case '\n':
                    escaped.append("\\n");
                    break;
                case '\r':
                    escaped.append("\\r");
                    break;
                case '\t':
                    escaped.append("\\t");
                    break;
                default:
                    if (ch < 0x20) {
                        escaped.append(String.format("\\u%04x", (int) ch));
                    } else {
                        escaped.append(ch);
                    }
                    break;
            }
        }

        return escaped.toString();
    }

    @Override
    protected void doGet(
            SlingHttpServletRequest request,
            SlingHttpServletResponse response)
            throws IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ValueMap properties = request.getResource().getValueMap();

        String json = "{"
            + "\"message\":\"Test Component Servlet Working\","
            + "\"resourceType\":\"" + escapeJson(
                request.getResource().getResourceType()) + "\","
            + "\"path\":\"" + escapeJson(
                request.getResource().getPath()) + "\","
            + "\"title\":\"" + escapeJson(
                properties.get("title", "")) + "\","
            + "\"mobileNumber\":\"" + escapeJson(
                properties.get("mobileNumber", "")) + "\","
            + "\"text\":\"" + escapeJson(
                properties.get("text", "")) + "\","
            + "\"range\":\"" + escapeJson(
                properties.get("range", "")) + "\","
            + "\"enabled\":" + properties.get("enabled", false) + ","
            + "\"pagePath\":\"" + escapeJson(
                properties.get("pagePath", "")) + "\","
            + "\"category\":\"" + escapeJson(
                properties.get("category", "")) + "\","
            + "\"color\":\"" + escapeJson(
                properties.get("color", "")) + "\""
            + "}";

        response.getWriter().write(json);
    }
}

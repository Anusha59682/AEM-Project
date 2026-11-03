
package com.adobe.aem.guides.demo.core.servlets;

import java.io.IOException;
import java.security.SecureRandom;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.osgi.service.component.annotations.Component;

@Component(service = Servlet.class,immediate = true)
@SlingServletPaths(value = "/bin/demo/random")
public class RandomGeneratorServlet extends SlingSafeMethodsServlet {

    private static final long serialVersionUID = 1L;

    private final SecureRandom random = new SecureRandom();

    @Override
    protected void doGet(
            SlingHttpServletRequest request,
            SlingHttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");

        String type = request.getParameter("type");

        if (type == null || type.trim().isEmpty()) {
            response.setStatus(
                SlingHttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(
                "Please provide the type parameter.");
            return;
        }

        String result;

        switch (type.trim()) {

            case "Number":
                result = generateNumbers();
                break;

            case "Letters":
                result = generateLetters();
                break;

            case "Random":
                result = generateMixed();
                break;

            default:
                response.setStatus(
                    SlingHttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(
                    "Invalid type. Use Number, Letters or Random.");
                return;
        }

        response.getWriter().write(result);
    }

    private String generateNumbers() {

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < 6; i++) {
            result.append(random.nextInt(10));
        }

        return result.toString();
    }

    private String generateLetters() {

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < 6; i++) {
            char letter = (char) ('A' + random.nextInt(26));
            result.append(letter);
        }

        return result.toString();
    }

    private String generateMixed() {

        StringBuilder result = new StringBuilder();

        // Generate 3 random numbers
        for (int i = 0; i < 3; i++) {
            result.append(random.nextInt(10));
        }

        // Generate 3 random letters
        for (int i = 0; i < 3; i++) {
            char letter = (char) ('A' + random.nextInt(26));
            result.append(letter);
        }

        return result.toString();
    }
}

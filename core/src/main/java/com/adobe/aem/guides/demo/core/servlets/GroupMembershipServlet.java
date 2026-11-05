package com.adobe.aem.guides.demo.core.servlets;

import java.io.IOException;

import javax.jcr.RepositoryException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.osgi.service.component.annotations.Component;

import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.UserManager;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

@Component(service = Servlet.class)
@SlingServletPaths("/bin/demo/check-group")
public class GroupMembershipServlet
        extends SlingSafeMethodsServlet {

    private static final long serialVersionUID = 1L;

    private static final String GROUP_ID = "zavelune-admin-group";
    @Override
    protected void doGet(
            SlingHttpServletRequest request,
            SlingHttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ResourceResolver resolver =
                request.getResourceResolver();

        String userId = resolver.getUserID();

        try {
            UserManager userManager =
                    resolver.adaptTo(UserManager.class);

            if (userManager == null) {
                response.setStatus(500);
                response.getWriter().write(
                    "{\"error\":\"UserManager unavailable\"}");
                return;
            }

            Authorizable user =
                    userManager.getAuthorizable(userId);

            Authorizable groupAuthorizable =
                    userManager.getAuthorizable(GROUP_ID);

            if (user == null ||
                    !(groupAuthorizable instanceof Group)) {

                response.getWriter().write(
                    "{\"user\":\"" + userId +
                    "\",\"isMember\":false}");
                return;
            }

            Group group = (Group) groupAuthorizable;

            boolean isMember = group.isMember(user);

            response.getWriter().write(
                "{\"user\":\"" + userId +
                "\",\"group\":\"" + GROUP_ID +
                "\",\"isMember\":" + isMember + "}");

        } catch (RepositoryException e) {

            response.setStatus(500);
            response.getWriter().write(
                "{\"error\":\"Unable to check group membership\"}");
        }
    }
}
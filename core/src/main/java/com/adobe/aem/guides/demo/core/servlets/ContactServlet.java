
package com.adobe.aem.guides.demo.core.servlets;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.jcr.Node;
import javax.jcr.Session;
import javax.servlet.Servlet;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Component(
    service = Servlet.class,
    property = {
        "sling.servlet.paths=/bin/demo/contacts",
        "sling.servlet.methods=GET",
        "sling.servlet.methods=POST"
    }
)
public class ContactServlet extends SlingAllMethodsServlet {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG =
        LoggerFactory.getLogger(ContactServlet.class);

    private static final String CONTACT_PATH = "/var/demo/contact-us";
    private static final String SUBSERVICE = "page-change-writer";

    private static final Pattern MOBILE_PATTERN =
        Pattern.compile("[0-9]{10}");

    private static final Pattern EMAIL_PATTERN =
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    @Reference
    private ResourceResolverFactory resolverFactory;

    private final ObjectMapper mapper = new ObjectMapper();

    private boolean isAuthorized(
        SlingHttpServletRequest request,
        SlingHttpServletResponse response
    ) throws IOException {

        String userId = request.getResourceResolver().getUserID();

        if (userId == null || "anonymous".equals(userId)) {
            writeJson(response, 401, "Please log in to manage contacts.");
            return false;
        }

        return true;
    }

    private ResourceResolver getServiceResolver() throws Exception {
        Map<String, Object> auth = new HashMap<>();
        auth.put(ResourceResolverFactory.SUBSERVICE, SUBSERVICE);
        return resolverFactory.getServiceResourceResolver(auth);
    }

    private void writeJson(
        SlingHttpServletResponse response,
        int status,
        String message
    ) throws IOException {

        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ObjectNode result = mapper.createObjectNode();
        result.put("message", message);
        mapper.writeValue(response.getWriter(), result);
    }

    /*
     * Get or create the contact storage folder using ResourceResolver.
     * This avoids calling session.getRootNode().
     */
    private Resource getContactRoot(ResourceResolver resolver)
        throws Exception {

        Resource root = resolver.getResource(CONTACT_PATH);

        if (root == null) {
            Resource varResource = resolver.getResource("/var");

            if (varResource == null) {
                throw new IllegalStateException(
                    "The /var folder does not exist or is not accessible."
                );
            }

            Map<String, Object> properties = new HashMap<>();
            properties.put("jcr:primaryType", "sling:Folder");

            Resource demoResource = resolver.getResource("/var/demo");

            if (demoResource == null) {
                demoResource = resolver.create(
                    varResource, "demo", properties
                );
            }

            Resource contactResource = resolver.getResource(CONTACT_PATH);

            if (contactResource == null) {
                contactResource = resolver.create(
                    demoResource, "contact-us", properties
                );
            }

            resolver.commit();
            root = contactResource;
        }

        return root;
    }

    // READ: Fetch all contacts.
    @Override
    protected void doGet(
        SlingHttpServletRequest request,
        SlingHttpServletResponse response
    ) throws IOException {

        if (!isAuthorized(request, response)) {
            return;
        }

        try (ResourceResolver resolver = getServiceResolver()) {

            Resource root = getContactRoot(resolver);
            Node rootNode = root.adaptTo(Node.class);

            if (rootNode == null) {
                throw new IllegalStateException(
                    "Unable to access contact storage node."
                );
            }

            ArrayNode result = mapper.createArrayNode();
            javax.jcr.NodeIterator iterator = rootNode.getNodes();

            while (iterator.hasNext()) {
                Node contact = iterator.nextNode();

                ObjectNode item = mapper.createObjectNode();
                item.put("id", contact.getName());
                item.put("name", contact.getProperty("name").getString());
                item.put("email", contact.getProperty("email").getString());
                item.put("mobile", contact.getProperty("mobile").getString());

                result.add(item);
            }

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            mapper.writeValue(response.getWriter(), result);

        } catch (Exception e) {
            LOG.error("Unable to read contacts.", e);
            response.reset();
            writeJson(response, 500, "Unable to read contacts.");
        }
    }

    // CREATE, UPDATE and DELETE.
    @Override
    protected void doPost(
        SlingHttpServletRequest request,
        SlingHttpServletResponse response
    ) throws IOException {

        if (!isAuthorized(request, response)) {
            return;
        }

        try {
            JsonNode data = mapper.readTree(request.getReader());

            if (data == null || !data.isObject()) {
                throw new IllegalArgumentException("Invalid request data.");
            }

            String action = data.path("action").asText("");
            String id = data.path("id").asText("");

            try (ResourceResolver resolver = getServiceResolver()) {

                Resource root = getContactRoot(resolver);
                Node rootNode = root.adaptTo(Node.class);

                if (rootNode == null) {
                    throw new IllegalStateException(
                        "Unable to access contact storage node."
                    );
                }

                switch (action) {
                    case "create":
                        createContact(rootNode, data);
                        break;

                    case "update":
                        updateContact(rootNode, id, data);
                        break;

                    case "delete":
                        deleteContact(rootNode, id);
                        break;

                    default:
                        throw new IllegalArgumentException("Invalid action.");
                }

                resolver.commit();

                LOG.info("Contact operation completed: {}", action);

                writeJson(
                    response, 200, "Contact " + action + " successful."
                );
            }

        } catch (IllegalArgumentException e) {
            writeJson(response, 400, e.getMessage());

        } catch (Exception e) {
            LOG.error("Contact operation failed.", e);
            writeJson(response, 500, "Contact operation failed.");
        }
    }

    private void createContact(Node root, JsonNode data)
        throws Exception {

        String name = required(data, "name");
        String email = required(data, "email");
        String mobile = required(data, "mobile");

        validateContact(email, mobile);

        String id = UUID.randomUUID().toString();
        Node contact = root.addNode(id, "nt:unstructured");

        contact.setProperty("id", id);
        contact.setProperty("name", name);
        contact.setProperty("email", email);
        contact.setProperty("mobile", mobile);

        LOG.info("Contact created: {}", id);
    }

    private void updateContact(
        Node root,
        String id,
        JsonNode data
    ) throws Exception {

        if (id.isBlank() || !root.hasNode(id)) {
            throw new IllegalArgumentException("Contact not found.");
        }

        String name = required(data, "name");
        String email = required(data, "email");
        String mobile = required(data, "mobile");

        validateContact(email, mobile);

        Node contact = root.getNode(id);
        contact.setProperty("name", name);
        contact.setProperty("email", email);
        contact.setProperty("mobile", mobile);

        LOG.info("Contact updated: {}", id);
    }

    private void deleteContact(Node root, String id)
        throws Exception {

        if (id.isBlank() || !root.hasNode(id)) {
            throw new IllegalArgumentException("Contact not found.");
        }

        root.getNode(id).remove();
        LOG.info("Contact deleted: {}", id);
    }

    private String required(JsonNode data, String field) {
        String value = data.path(field).asText("").trim();

        if (value.isEmpty()) {
            throw new IllegalArgumentException(field + " is required.");
        }

        return value;
    }

    private void validateContact(String email, String mobile) {
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException(
                "Enter a valid email address."
            );
        }

        if (!MOBILE_PATTERN.matcher(mobile).matches()) {
            throw new IllegalArgumentException(
                "Mobile number must contain exactly 10 digits."
            );
        }
    }
}

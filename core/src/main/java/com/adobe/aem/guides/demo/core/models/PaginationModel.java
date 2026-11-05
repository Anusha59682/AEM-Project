
package com.adobe.aem.guides.demo.core.models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

import org.apache.sling.models.annotations.Exporter;

@Model(
    adaptables = SlingHttpServletRequest.class,
    adapters = ComponentExporter.class,
    resourceType = "demo/components/pagination",
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
@Exporter(
    name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
    extensions = ExporterConstants.SLING_MODEL_EXTENSION
)
public class PaginationModel implements ComponentExporter {

    @Self
    private SlingHttpServletRequest request;

    @ValueMapValue
    private String parentPath;

    private List<String> pages = new ArrayList<>();

    private int pageSize = 1;
    private int pageLimit = 3;

    @PostConstruct
    protected void init() {

        // Read parameters directly from the request URL.
        pageSize = parsePositiveNumber(
            request.getParameter("pageSize"), 1
        );

        pageLimit = parsePositiveNumber(
            request.getParameter("pageLimit"), 3
        );

        if (parentPath == null || parentPath.trim().isEmpty()) {
            return;
        }

        ResourceResolver resolver = request.getResourceResolver();

        PageManager pageManager = resolver.adaptTo(PageManager.class);

        if (pageManager == null) {
            return;
        }

        Page parentPage = pageManager.getPage(parentPath);

        if (parentPage == null) {
            return;
        }

        List<String> allPages = new ArrayList<>();

        // Include parent page as the first result.
        allPages.add(parentPage.getPath());

        // Retrieve direct child pages.
        Iterator<Page> children = parentPage.listChildren();

        while (children.hasNext()) {
            Page child = children.next();
            allPages.add(child.getPath());
        }

        // Calculate pagination.
        int startIndex = (pageSize - 1) * pageLimit;

        int endIndex = Math.min(
            startIndex + pageLimit,
            allPages.size()
        );

        if (startIndex < allPages.size()) {
            pages = new ArrayList<>(
                allPages.subList(startIndex, endIndex)
            );
        }
    }

    private int parsePositiveNumber(String value, int defaultValue) {
        try {
            int number = Integer.parseInt(value);
            return number > 0 ? number : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public List<String> getPages() {
        return pages;
    }

    public String getParentPath() {
        return parentPath;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getPageLimit() {
        return pageLimit;
    }

    @Override
    public String getExportedType() {
        return "demo/components/pagination";
    }
}

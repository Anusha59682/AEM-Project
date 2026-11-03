package com.adobe.aem.guides.demo.core.models;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(adaptables = Resource.class,
defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)

public class PracticeComponentModel
{
    @ValueMapValue
    private String text;

    @ValueMapValue
    private Long number;

    @ValueMapValue
    private Boolean enabled;

    @ValueMapValue
    private String displayStyle;

    @ValueMapValue
    private String contentPath;

    @ValueMapValue
    private String backgroundColor;

    @ValueMapValue
    private Calendar date;

    @ValueMapValue 
    private String[] skills;

    public String getText() {
        return text;
    }

    public Long getNumber() {
        return number;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public String getDisplayStyle() {
        return displayStyle;
    }

    public String getContentPath() {
        return contentPath;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public String getDate() {
        if(date == null)
        {
            return "";
        }
        return new SimpleDateFormat("yyyy-MM-dd").format(date.getTime());
    }

    public String[] getSkills() {
        return skills;
    }
}
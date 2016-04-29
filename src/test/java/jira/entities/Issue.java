package jira.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by robertgates55 on 26/04/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Issue {
    public String id;
    public String self;
    public String key;
}

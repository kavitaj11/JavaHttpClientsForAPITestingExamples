package test.api.resource;

import lombok.Data;

@Data
public class DependentResource {
    private String firstName;
    private String lastName;
    private String relationship;
}

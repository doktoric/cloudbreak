package com.sequenceiq.environment.api.environment.requests;

import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.sequenceiq.environment.api.doc.ModelDescriptions;
import com.sequenceiq.environment.api.doc.ModelDescriptions.EnvironmentRequestModelDescription;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class EnvironmentRequest extends EnvironmentBaseRequest implements CredentialAwareEnvRequest {

    @Size(max = 100, min = 5, message = "The length of the environments's name has to be in range of 5 to 100")
    @Pattern(regexp = "(^[a-z][-a-z0-9]*[a-z0-9]$)",
            message = "The environments's name can only contain lowercase alphanumeric characters and hyphens and has start with an alphanumeric character")
    @ApiModelProperty(value = ModelDescriptions.NAME, required = true)
    private String name;

    @Size(max = 1000)
    @ApiModelProperty(ModelDescriptions.DESCRIPTION)
    private String description;

    @ApiModelProperty(EnvironmentRequestModelDescription.CREDENTIAL_NAME)
    private String credentialName;

    @ApiModelProperty(EnvironmentRequestModelDescription.CREDENTIAL)
    private Long credential;

    @ApiModelProperty(EnvironmentRequestModelDescription.REGIONS)
    private Set<String> regions = new HashSet<>();

    @ApiModelProperty(EnvironmentRequestModelDescription.LOCATION)
    @NotNull
    private LocationRequest location;

    @ApiModelProperty(EnvironmentRequestModelDescription.NETWORK)
    private EnvironmentNetworkRequest network;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getCredentialName() {
        return credentialName;
    }

    @Override
    public void setCredentialName(String credentialName) {
        this.credentialName = credentialName;
    }

    @Override
    public Long getCredential() {
        return credential;
    }

    @Override
    public void setCredential(Long credential) {
        this.credential = credential;
    }

    public Set<String> getRegions() {
        return regions;
    }

    public void setRegions(Set<String> regions) {
        this.regions = regions == null ? new HashSet<>() : regions;
    }

    public LocationRequest getLocation() {
        return location;
    }

    public void setLocation(LocationRequest location) {
        this.location = location;
    }

    public EnvironmentNetworkRequest getNetwork() {
        return network;
    }

    public void setNetwork(EnvironmentNetworkRequest network) {
        this.network = network;
    }

}

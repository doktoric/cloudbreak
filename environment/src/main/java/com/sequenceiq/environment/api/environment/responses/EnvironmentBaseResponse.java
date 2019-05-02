package com.sequenceiq.environment.api.environment.responses;

import java.util.Set;

import com.sequenceiq.environment.api.doc.ModelDescriptions;
import com.sequenceiq.environment.api.doc.ModelDescriptions.EnvironmentResponseModelDescription;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public abstract class EnvironmentBaseResponse {

    @ApiModelProperty(ModelDescriptions.ID)
    private Long id;

    @ApiModelProperty(ModelDescriptions.NAME)
    private String name;

    @ApiModelProperty(ModelDescriptions.DESCRIPTION)
    private String description;

    @ApiModelProperty(EnvironmentResponseModelDescription.REGIONS)
    private CompactRegionResponse regions;

    @ApiModelProperty(EnvironmentResponseModelDescription.CLOUD_PLATFORM)
    private String cloudPlatform;

    @ApiModelProperty(EnvironmentResponseModelDescription.CREDENTIAL_NAME)
    private String credentialName;

    @ApiModelProperty(EnvironmentResponseModelDescription.LOCATION)
    private LocationResponse location;

    private Long workspace;

    @ApiModelProperty(EnvironmentResponseModelDescription.SDX_RESOURCES_NAMES)
    private Set<String> datalakeResourcesNames;

    @ApiModelProperty(EnvironmentResponseModelDescription.SDX_CLUSTER_NAMES)
    private Set<String> datalakeClusterNames;

    @ApiModelProperty(EnvironmentResponseModelDescription.WORKLOAD_CLUSTER_NAMES)
    private Set<String> workloadClusterNames;

    @ApiModelProperty(EnvironmentResponseModelDescription.NETWORK)
    private EnvironmentNetworkResponse network;

    public Set<String> getDatalakeResourcesNames() {
        return datalakeResourcesNames;
    }

    public void setDatalakeResourcesNames(Set<String> datalakeResourcesNames) {
        this.datalakeResourcesNames = datalakeResourcesNames;
    }

    public Set<String> getDatalakeClusterNames() {
        return datalakeClusterNames;
    }

    public void setDatalakeClusterNames(Set<String> datalakeClusterNames) {
        this.datalakeClusterNames = datalakeClusterNames;
    }

    public Set<String> getWorkloadClusterNames() {
        return workloadClusterNames;
    }

    public void setWorkloadClusterNames(Set<String> workloadClusterNames) {
        this.workloadClusterNames = workloadClusterNames;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public CompactRegionResponse getRegions() {
        return regions;
    }

    public void setRegions(CompactRegionResponse regions) {
        this.regions = regions;
    }

    public Long getWorkspace() {
        return workspace;
    }

    public void setWorkspace(Long workspace) {
        this.workspace = workspace;
    }

    public String getCloudPlatform() {
        return cloudPlatform;
    }

    public void setCloudPlatform(String cloudPlatform) {
        this.cloudPlatform = cloudPlatform;
    }

    public String getCredentialName() {
        return credentialName;
    }

    public void setCredentialName(String credentialName) {
        this.credentialName = credentialName;
    }

    public LocationResponse getLocation() {
        return location;
    }

    public void setLocation(LocationResponse location) {
        this.location = location;
    }

    public EnvironmentNetworkResponse getNetwork() {
        return network;
    }

    public void setNetwork(EnvironmentNetworkResponse network) {
        this.network = network;
    }

}

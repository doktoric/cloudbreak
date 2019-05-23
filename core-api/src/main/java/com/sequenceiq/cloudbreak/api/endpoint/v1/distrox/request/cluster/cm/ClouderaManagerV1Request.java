package com.sequenceiq.cloudbreak.api.endpoint.v1.distrox.request.cluster.cm;

import java.util.List;

import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sequenceiq.cloudbreak.api.endpoint.v1.distrox.request.cluster.cm.product.ClouderaManagerProductV1Request;
import com.sequenceiq.cloudbreak.api.endpoint.v1.distrox.request.cluster.cm.repository.ClouderaManagerRepositoryV1Request;
import com.sequenceiq.cloudbreak.api.endpoint.v4.JsonEntity;
import com.sequenceiq.cloudbreak.doc.ModelDescriptions.ClusterModelDescription;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class ClouderaManagerV1Request implements JsonEntity {

    @Valid
    @ApiModelProperty(ClusterModelDescription.CM_REPO_DETAILS)
    private ClouderaManagerRepositoryV1Request repository;

    @Valid
    @ApiModelProperty(ClusterModelDescription.CM_PRODUCT_DETAILS)
    private List<ClouderaManagerProductV1Request> products;

    public ClouderaManagerRepositoryV1Request getRepository() {
        return repository;
    }

    public void setRepository(ClouderaManagerRepositoryV1Request repository) {
        this.repository = repository;
    }

    public List<ClouderaManagerProductV1Request> getProducts() {
        return products;
    }

    public void setProducts(List<ClouderaManagerProductV1Request> products) {
        this.products = products;
    }

    public ClouderaManagerV1Request withRepository(ClouderaManagerRepositoryV1Request repository) {
        setRepository(repository);
        return this;
    }

    public ClouderaManagerV1Request withProducts(List<ClouderaManagerProductV1Request> products) {
        setProducts(products);
        return this;
    }
}

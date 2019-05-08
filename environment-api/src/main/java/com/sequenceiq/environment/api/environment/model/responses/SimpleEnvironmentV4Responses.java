package com.sequenceiq.environment.api.environment.model.responses;

import java.util.Set;

import com.google.common.collect.Sets;
import com.sequenceiq.environment.api.GeneralCollectionV4Response;

import io.swagger.annotations.ApiModel;

@ApiModel
public class SimpleEnvironmentV4Responses extends GeneralCollectionV4Response<SimpleEnvironmentV4Response> {
    public SimpleEnvironmentV4Responses(Set<SimpleEnvironmentV4Response> responses) {
        super(responses);
    }

    public SimpleEnvironmentV4Responses() {
        super(Sets.newHashSet());
    }
}

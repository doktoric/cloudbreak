package com.sequenceiq.it.cloudbreak.mock.ambari;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sequenceiq.it.cloudbreak.mock.model.Requests;
import com.sequenceiq.it.cloudbreak.mock.ITResponse;

import spark.Request;
import spark.Response;

public class AmbariClusterRequestsResponse extends ITResponse {
    @Override
    public Object handle(Request request, Response response) {
        response.type("text/plain");
        ObjectNode rootNode = JsonNodeFactory.instance.objectNode();
        Requests requests = new Requests(66, "SUCCESSFUL", 100);
        rootNode.set("Requests", new ObjectMapper().valueToTree(requests));
        return rootNode.toString();
    }
}

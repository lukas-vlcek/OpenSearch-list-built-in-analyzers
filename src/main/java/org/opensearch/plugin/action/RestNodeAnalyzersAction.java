/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */
package org.opensearch.plugin.action;

import org.opensearch.client.node.NodeClient;
import org.opensearch.indices.analysis.AnalysisModule;
import org.opensearch.rest.BaseRestHandler;
import org.opensearch.rest.RestRequest;
import org.opensearch.rest.RestResponse;
import org.opensearch.rest.action.RestResponseListener;

import java.io.IOException;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;
import static org.opensearch.rest.RestRequest.Method.GET;

/**
 * Rest Action for Node Analyzers action.
 */
public class RestNodeAnalyzersAction extends BaseRestHandler {

    /**
     * TODO
     */
    @Override
    public String getName() {
        return "node_analyzers_action";
    }

    /**
     * TODO
     */
    @Override
    public List<Route> routes() {
        return unmodifiableList(
            asList(
                new Route(GET, "/_nodes/analyzers"),
                new Route(GET, "/_nodes/{nodeId}/analyzers")
            )
        );
    }

    /**
     * TODO
     */
    @Override
    protected RestChannelConsumer prepareRequest(RestRequest request, NodeClient nodeClient) throws IOException {
        String nodesFilter = request.param("nodeId");

        NodeAnalyzersRequest nodeAnalyzersRequest = new NodeAnalyzersRequest();

        return channel -> nodeClient.execute(NodeAnalyzersAction.INSTANCE, nodeAnalyzersRequest,
                new RestResponseListener<NodeAnalyzersResponse>(channel) {

                    @Override
                    public RestResponse buildResponse(NodeAnalyzersResponse nodeAnalyzersResponse) throws Exception {
                        return null;
                    }
                }
        );
    }
}

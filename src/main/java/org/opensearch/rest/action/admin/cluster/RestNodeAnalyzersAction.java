/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */
package org.opensearch.rest.action.admin.cluster;

import org.opensearch.client.node.NodeClient;
import org.opensearch.common.Strings;
import org.opensearch.common.xcontent.XContentBuilder;
import org.opensearch.plugin.action.NodeAnalyzersAction;
import org.opensearch.plugin.action.NodesAnalyzersRequest;
import org.opensearch.plugin.action.NodesAnalyzersResponse;
import org.opensearch.rest.BaseRestHandler;
import org.opensearch.rest.BytesRestResponse;
import org.opensearch.rest.RestRequest;
import org.opensearch.rest.RestResponse;
import org.opensearch.rest.RestStatus;
import org.opensearch.rest.action.RestActions;
import org.opensearch.rest.action.RestBuilderListener;

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
    protected RestChannelConsumer prepareRequest(final RestRequest request, final NodeClient nodeClient) throws IOException {
        String[] nodesIds = Strings.splitStringByCommaToArray(request.param("nodeId"));
        NodesAnalyzersRequest nodesAnalyzersRequest = new NodesAnalyzersRequest(nodesIds);

        return channel -> nodeClient.execute(NodeAnalyzersAction.INSTANCE, nodesAnalyzersRequest,
                new RestBuilderListener<NodesAnalyzersResponse>(channel) {
                    @Override
                    public RestResponse buildResponse(NodesAnalyzersResponse response, XContentBuilder builder) throws Exception {
                        builder.startObject();
                        RestActions.buildNodesHeader(builder, channel.request(), response);
                        builder.field("cluster_name", response.getClusterName().value());
                        response.toXContent(builder, channel.request());
                        builder.endObject();
                        return new BytesRestResponse(RestStatus.OK, builder);
                    }
                }
        );
    }
}

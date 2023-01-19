/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */
package org.opensearch.plugin.action;

import org.opensearch.action.FailedNodeException;
import org.opensearch.action.support.nodes.BaseNodesResponse;
import org.opensearch.cluster.ClusterName;
import org.opensearch.common.io.stream.StreamInput;
import org.opensearch.common.io.stream.StreamOutput;
import org.opensearch.common.xcontent.ToXContentFragment;
import org.opensearch.common.xcontent.XContentBuilder;

import java.io.IOException;
import java.util.List;

/**
 * Transport response to NodeAnalyzers
 */
public class NodesAnalyzersResponse extends BaseNodesResponse<NodeAnalyzersInfo> implements ToXContentFragment {

    public NodesAnalyzersResponse(StreamInput in) throws IOException {
        super(in);
    }

    public NodesAnalyzersResponse(ClusterName clusterName, List<NodeAnalyzersInfo> nodes, List<FailedNodeException> failures) {
        super(clusterName, nodes, failures);
    }

    @Override
    protected List<NodeAnalyzersInfo> readNodesFrom(StreamInput in) throws IOException {
        return in.readList(NodeAnalyzersInfo::new);
    }

    @Override
    protected void writeNodesTo(StreamOutput out, List<NodeAnalyzersInfo> nodes) throws IOException {
        out.writeList(nodes);
    }

    /**
     * @param builder
     * @param params
     * @return
     * @throws IOException
     */
    @Override
    public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
        builder.startObject("nodes");
        for (NodeAnalyzersInfo nodeInfo: getNodes()) {
            builder.startObject(nodeInfo.getNode().getId());
            builder.field("analyzers").value(nodeInfo.getAnalyzersKeySet());
            builder.field("tokenizers").value(nodeInfo.getTokenizersKeySet());
            builder.field("tokenFilters").value(nodeInfo.getTokenFiltersKeySet());
            builder.field("charFilters").value(nodeInfo.getCharFiltersKeySet());
            builder.field("normalizers").value(nodeInfo.getNormalizersKeySet());

            builder.startArray("plugins");
            for (String plugin : nodeInfo.getNodeAnalysisPlugins().keySet()) {
                NodeAnalyzersInfo.AnalysisPluginComponents pluginComponents = nodeInfo.getNodeAnalysisPlugins().get(plugin);
                builder.startObject();
                builder.field("name", pluginComponents.getPluginName());
                builder.field("analyzers").value(pluginComponents.getAnalyzersKeySet());
                builder.field("tokenizers").value(pluginComponents.getTokenizersKeySet());
                builder.field("tokenFilters").value(pluginComponents.getTokenFiltersKeySet());
                builder.field("charFilters").value(pluginComponents.getCharFiltersKeySet());
                builder.field("hunspellDictionaries").value(pluginComponents.getHunspellDictionaries());
                builder.endObject();
            }
            builder.endArray();

            builder.endObject();
        }
        builder.endObject();

        return builder;
    }

    /**
     * @return
     */
    @Override
    public boolean isFragment() {
        return ToXContentFragment.super.isFragment();
    }
}

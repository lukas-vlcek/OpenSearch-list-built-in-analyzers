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
            builder.array("analyzers", nodeInfo.getAnalyzersKeySet());
            builder.array("tokenizers", nodeInfo.getTokenizersKeySet());
            builder.array("tokenFilters", nodeInfo.getTokenFiltersKeySet());
            builder.array("charFilters", nodeInfo.getCharFiltersKeySet());
            builder.array("normalizers", nodeInfo.getNormalizersKeySet());

            builder.startObject("plugins");
            for (String plugin : nodeInfo.getNodeAnalysisPlugins().keySet()) {
                NodeAnalyzersInfo.AnalysisPluginComponents pluginComponents = nodeInfo.getNodeAnalysisPlugins().get(plugin);
                builder.startObject("plugin");
                builder.field("name", pluginComponents.getPluginName());
                builder.array("analyzers", pluginComponents.getAnalyzersKeySet());
                builder.array("tokenizers", pluginComponents.getTokenizersKeySet());
                builder.array("tokenFilters", pluginComponents.getTokenFiltersKeySet());
                builder.array("charFilters", pluginComponents.getCharFiltersKeySet());
                builder.array("hunspellDictionaries", pluginComponents.getHunspellDictionaries());
                builder.endObject();
            }
            builder.endObject();

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

/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */
package org.opensearch.plugin.node.analyzers;

import org.opensearch.action.ActionRequest;
import org.opensearch.cluster.metadata.IndexNameExpressionResolver;
import org.opensearch.cluster.node.DiscoveryNodes;
import org.opensearch.common.settings.ClusterSettings;
import org.opensearch.common.settings.IndexScopedSettings;
import org.opensearch.common.settings.Settings;
import org.opensearch.common.settings.SettingsFilter;
import org.opensearch.core.action.ActionResponse;
import org.opensearch.plugin.action.NodeAnalyzersAction;
import org.opensearch.plugin.action.TransportNodeAnalyzersAction;
import org.opensearch.plugins.ActionPlugin;
import org.opensearch.plugins.Plugin;
import org.opensearch.rest.RestController;
import org.opensearch.rest.RestHandler;
import org.opensearch.rest.action.admin.cluster.RestNodeAnalyzersAction;

import java.util.List;
import java.util.function.Supplier;

import static java.util.Collections.singletonList;

/**
 * After installing this plugin client can pull list of all built-in
 * analyzers from OpenSearch node. The list of analyzers is node-specific.
 */
public class NodeAnalyzersPlugin extends Plugin implements ActionPlugin {

    /**
     * TODO
     */
    @Override
    public List<ActionHandler<? extends ActionRequest, ? extends ActionResponse>> getActions() {
        return singletonList(
                new ActionHandler<>(NodeAnalyzersAction.INSTANCE, TransportNodeAnalyzersAction.class)
        );
    }

    /**
     * TODO
     */
    @Override
    public List<RestHandler> getRestHandlers(
            final Settings settings,
            final RestController restController,
            final ClusterSettings clusterSettings,
            final IndexScopedSettings indexScopedSettings,
            final SettingsFilter settingsFilter,
            final IndexNameExpressionResolver indexNameExpressionResolver,
            final Supplier<DiscoveryNodes> nodesInCluster
    ) {
        return singletonList(new RestNodeAnalyzersAction());
    }
}

/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */
package org.opensearch.plugin.action;

import org.opensearch.OpenSearchException;
import org.opensearch.SpecialPermission;
import org.opensearch.action.FailedNodeException;
import org.opensearch.action.support.ActionFilters;
import org.opensearch.action.support.nodes.TransportNodesAction;
import org.opensearch.cluster.service.ClusterService;
import org.opensearch.common.SuppressForbidden;
import org.opensearch.common.inject.Inject;
import org.opensearch.common.io.stream.StreamInput;
import org.opensearch.common.io.stream.StreamOutput;
import org.opensearch.index.analysis.AnalysisRegistry;
import org.opensearch.plugins.AnalysisPlugin;
import org.opensearch.plugins.PluginsService;
import org.opensearch.threadpool.ThreadPool;
import org.opensearch.transport.TransportRequest;
import org.opensearch.transport.TransportService;

import java.io.IOException;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The transport layer level of Node Analyzers Action has access to AnalysisRegistry (via binding) which can be used
 * to get access to its internal registries of analysis components, such as: analyzers, tokenizers, tokenFilters and
 * charFilters (and normalizers).
 * But these are all private members thus it is necessary to use Reflection API and PrivilegedAction. This is the
 * reason why the plugin requires appropriate security policy grants.
 * On top of that we use PluginsService to get all plugins that implement AnalysisPlugin interface to provide more
 * detailed information about which plugin is the provider of specific analysis component.
 */
public class TransportNodeAnalyzersAction extends TransportNodesAction<
        NodesAnalyzersRequest,
        NodesAnalyzersResponse,
        TransportNodeAnalyzersAction.NodeRequest,
        NodeAnalyzersInfo> {
    private PluginsService pluginsService;
    private AnalysisRegistry analysisRegistry;

    /**
     * A constructor.
     * @param transportService  TransportService
     * @param actionFilters     ActionFilters
     * @param pluginsService    PluginsService
     * @param analysisRegistry  AnalysisRegistry
     */
    @Inject
    public TransportNodeAnalyzersAction(
            ThreadPool threadPool,
            ClusterService clusterService,
            TransportService transportService,
            ActionFilters actionFilters,
            PluginsService pluginsService,
            AnalysisRegistry analysisRegistry
    ) {
        super(
                NodeAnalyzersAction.NAME,
                threadPool,
                clusterService,
                transportService,
                actionFilters,
                NodesAnalyzersRequest::new,
                NodeRequest::new,
                ThreadPool.Names.GENERIC,
                NodeAnalyzersInfo.class
        );
        this.pluginsService = pluginsService;
        this.analysisRegistry = analysisRegistry;
    }

    /**
     * @param nodeRequest
     * @return
     */
    @Override
    @SuppressForbidden(reason = "We have to use reflection API")
    protected NodeAnalyzersInfo nodeOperation(NodeRequest nodeRequest) {
        final KeySetHolder keySetHolder;
        SpecialPermission.check();
        keySetHolder = AccessController.doPrivileged((PrivilegedAction<KeySetHolder>) () -> {
            KeySetHolder holder = new KeySetHolder();
            try {
                Field privateAnalyzers;
                privateAnalyzers = AnalysisRegistry.class.getDeclaredField("analyzers");
                privateAnalyzers.setAccessible(true);
                holder.setAnalyzersKeySet(((Map<String, Object>) privateAnalyzers.get(analysisRegistry)).keySet());

                Field privateTokenizers;
                privateTokenizers = AnalysisRegistry.class.getDeclaredField("tokenizers");
                privateTokenizers.setAccessible(true);
                holder.setTokenizersKeySet(((Map<String, Object>) privateTokenizers.get(analysisRegistry)).keySet());

                Field privateTokenFilters;
                privateTokenFilters = AnalysisRegistry.class.getDeclaredField("tokenFilters");
                privateTokenFilters.setAccessible(true);
                holder.setTokenFiltersKeySet(((Map<String, Object>) privateTokenFilters.get(analysisRegistry)).keySet());

                Field privateCharFilters;
                privateCharFilters = AnalysisRegistry.class.getDeclaredField("charFilters");
                privateCharFilters.setAccessible(true);
                holder.setCharFiltersKeySet(((Map<String, Object>) privateCharFilters.get(analysisRegistry)).keySet());

                Field privateNormalizers;
                privateNormalizers = AnalysisRegistry.class.getDeclaredField("normalizers");
                privateNormalizers.setAccessible(true);
                holder.setNormalizersKeySet(((Map<String, Object>) privateNormalizers.get(analysisRegistry)).keySet());

            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new OpenSearchException(e);
            }
            return holder;
        });
        Map<String, NodeAnalyzersInfo.AnalysisPluginComponents> pluginComponents= new HashMap<>();
        List<AnalysisPlugin> analysisPlugins = pluginsService.filterPlugins(AnalysisPlugin.class);
        for (AnalysisPlugin plugin: analysisPlugins) {
            String pluginName = plugin.getClass().getCanonicalName();
            // TODO: getCanonicalName() ^^ can lead to null for anonymous inner class. We need to find something better.
            pluginComponents.put(pluginName,
                    new NodeAnalyzersInfo.AnalysisPluginComponents(
                            pluginName,
                            plugin.getAnalyzers().keySet(),
                            plugin.getTokenizers().keySet(),
                            plugin.getTokenFilters().keySet(),
                            plugin.getCharFilters().keySet(),
                            plugin.getHunspellDictionaries().keySet()
                    ));
        }
        return new NodeAnalyzersInfo(
                clusterService.localNode(),
                keySetHolder.analyzersKeySet,
                keySetHolder.tokenizersKeySet,
                keySetHolder.tokenFiltersKeySet,
                keySetHolder.charFiltersKeySet,
                keySetHolder.normalizersKeySet,
                pluginComponents
        );
    }

    /**
     * @param nodesRequest
     * @param nodeResponses
     * @param nodeFailures
     * @return
     */
    @Override
    protected NodesAnalyzersResponse newResponse(
            NodesAnalyzersRequest nodesRequest,
            List<NodeAnalyzersInfo> nodeResponses,
            List<FailedNodeException> nodeFailures
    ) {
        return new NodesAnalyzersResponse(clusterService.getClusterName(), nodeResponses, nodeFailures);
    }

    /**
     * @param nodesRequest
     * @return
     */
    @Override
    protected NodeRequest newNodeRequest(NodesAnalyzersRequest nodesRequest) {
        return new NodeRequest(nodesRequest);
    }

    /**
     * @param in
     * @return
     * @throws IOException
     */
    @Override
    protected NodeAnalyzersInfo newNodeResponse(StreamInput in) throws IOException {
        return new NodeAnalyzersInfo(in);
    }

    /**
     * Inner node request.
     */
    public static class NodeRequest extends TransportRequest {
        NodesAnalyzersRequest request;

        public NodeRequest(StreamInput in) throws IOException {
            super(in);
            this.request = new NodesAnalyzersRequest(in);
        }

        NodeRequest(NodesAnalyzersRequest request) {
            this.request = request;
        }

        @Override
        public void writeTo(StreamOutput out) throws IOException {
            super.writeTo(out);
            request.writeTo(out);
        }
    }

    private class KeySetHolder {
        Set<String> analyzersKeySet;
        Set<String> tokenizersKeySet;
        Set<String>  tokenFiltersKeySet;
        Set<String> charFiltersKeySet;
        Set<String> normalizersKeySet;

        void setAnalyzersKeySet(final Set<String> analyzersKeySet) {
            this.analyzersKeySet = analyzersKeySet;
        }

        Set<String> getAnalyzersKeySet() {
            return this.analyzersKeySet;
        }

        void setTokenizersKeySet(final Set<String> tokenizersKeySet) {
            this.tokenizersKeySet = tokenizersKeySet;
        }

        Set<String> getTokenizersKeySet() {
            return this.tokenizersKeySet;
        }

        void setTokenFiltersKeySet(final Set<String> tokenFiltersKeySet) {
            this.tokenFiltersKeySet = tokenFiltersKeySet;
        }

        Set<String> getTokenFiltersKeySet() {
            return tokenFiltersKeySet;
        }

        void setCharFiltersKeySet(final Set<String> charFiltersKeySet) {
            this.charFiltersKeySet = charFiltersKeySet;
        }

        Set<String> getCharFiltersKeySet() {
            return charFiltersKeySet;
        }

        void setNormalizersKeySet(final Set<String> normalizersKeySet) {
            this.normalizersKeySet = normalizersKeySet;
        }

        Set<String> getNormalizersKeySet() {
            return normalizersKeySet;
        }
    }
}

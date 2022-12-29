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
import org.opensearch.action.ActionListener;
import org.opensearch.action.support.ActionFilters;
import org.opensearch.action.support.HandledTransportAction;
import org.opensearch.client.node.NodeClient;
import org.opensearch.common.SuppressForbidden;
import org.opensearch.common.inject.Inject;
import org.opensearch.index.analysis.AnalysisRegistry;
import org.opensearch.plugins.AnalysisPlugin;
import org.opensearch.plugins.PluginsService;
import org.opensearch.tasks.Task;
import org.opensearch.transport.TransportService;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
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
public class TransportNodeAnalyzersAction extends HandledTransportAction<NodeAnalyzersRequest, NodeAnalyzersResponse> {
    private PluginsService pluginsService;
    private AnalysisRegistry analysisRegistry;
    private NodeClient nodeClient;

    /**
     * A constructor.
     * @param transportService TransportService
     * @param actionFilters ActionFilters
     * @param pluginsService PluginsService
     * @param analysisRegistry AnalysisRegistry
     * @param nodeClient NodeClient
     */
    @Inject
    public TransportNodeAnalyzersAction(TransportService transportService, ActionFilters actionFilters,
                                        PluginsService pluginsService, AnalysisRegistry analysisRegistry,
                                        NodeClient nodeClient) {
        super(NodeAnalyzersAction.NAME, transportService, actionFilters, NodeAnalyzersRequest::new);
        this.pluginsService = pluginsService;
        this.analysisRegistry = analysisRegistry;
        this.nodeClient = nodeClient;
    }

    /**
     * TODO
     * @param task Task
     * @param request Request
     * @param actionListener ActionListener
     */
    @Override
    @SuppressForbidden(reason = "System.out just for now")
    protected void doExecute(Task task, NodeAnalyzersRequest request, ActionListener<NodeAnalyzersResponse> actionListener) {
        System.out.println("=================");
        System.out.println("Local node Id:");
        System.out.println(nodeClient.getLocalNodeId());

        System.out.println("--------");
        List<AnalysisPlugin> analysisPlugins = pluginsService.filterPlugins(AnalysisPlugin.class);
        for (AnalysisPlugin plugin: analysisPlugins) {
            System.out.println(" - " + plugin.toString());
            System.out.println(" - analyzers: " + plugin.getAnalyzers().keySet());
            System.out.println(" - tokenizers: " + plugin.getTokenizers().keySet());
            System.out.println(" - tokenFilters: " + plugin.getTokenFilters().keySet());
            System.out.println(" - charFilters: " + plugin.getCharFilters().keySet());
            System.out.println(" - hunspellDictionaries: " + plugin.getHunspellDictionaries().keySet());
        }
        System.out.println("--------");

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

        System.out.println("analyzers:");
        System.out.println(" - " +  keySetHolder.getAnalyzersKeySet());

        System.out.println("tokenizers:");
        System.out.println(" - " + keySetHolder.getTokenizersKeySet());

        System.out.println("tokenFilters:");
        System.out.println(" - " + keySetHolder.getTokenFiltersKeySet());

        System.out.println("charFilters:");
        System.out.println(" - " + keySetHolder.getCharFiltersKeySet());

        System.out.println("normalizers:");
        System.out.println(" - " + keySetHolder.getNormalizersKeySet());

        System.out.println("=================");
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

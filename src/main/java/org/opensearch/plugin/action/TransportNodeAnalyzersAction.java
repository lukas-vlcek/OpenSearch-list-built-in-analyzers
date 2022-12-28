/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */
package org.opensearch.plugin.action;

import org.opensearch.SpecialPermission;
import org.opensearch.action.ActionListener;
import org.opensearch.action.support.ActionFilters;
import org.opensearch.action.support.HandledTransportAction;
import org.opensearch.client.node.NodeClient;
import org.opensearch.common.SuppressForbidden;
import org.opensearch.common.inject.Inject;
import org.opensearch.index.analysis.AnalysisRegistry;
import org.opensearch.index.analysis.AnalyzerProvider;
import org.opensearch.index.analysis.CharFilterFactory;
import org.opensearch.index.analysis.TokenFilterFactory;
import org.opensearch.index.analysis.TokenizerFactory;
import org.opensearch.indices.analysis.AnalysisModule;
import org.opensearch.plugins.AnalysisPlugin;
import org.opensearch.plugins.PluginsService;
import org.opensearch.tasks.Task;
import org.opensearch.transport.TransportService;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.List;
import java.util.Map;

/**
 * TODO
 */
public class TransportNodeAnalyzersAction extends HandledTransportAction<NodeAnalyzersRequest, NodeAnalyzersResponse> {
    private PluginsService pluginsService;
    private AnalysisRegistry analysisRegistry;
    private NodeClient nodeClient;

    /**
     * TODO
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

        final PriviledgedHolder privsHolder;

        SpecialPermission.check();
        privsHolder = AccessController.doPrivileged(new PrivilegedAction<PriviledgedHolder>() {
            @Override
            @SuppressForbidden(reason = "We are using reflections")
            public PriviledgedHolder run() {
                PriviledgedHolder holder = new PriviledgedHolder();
                try {

                    Field privateAnalyzers;
                    privateAnalyzers = AnalysisRegistry.class.getDeclaredField("analyzers");
                    privateAnalyzers.setAccessible(true);
                    holder.setAnalyzers((Map<String, AnalysisModule.AnalysisProvider<AnalyzerProvider<?>>>) privateAnalyzers.get(analysisRegistry));

                    Field privateTokenizers;
                    privateTokenizers = AnalysisRegistry.class.getDeclaredField("tokenizers");
                    privateTokenizers.setAccessible(true);
                    holder.setTokenizers((Map<String, AnalysisModule.AnalysisProvider<TokenizerFactory>>) privateTokenizers.get(analysisRegistry));

                    Field privateTokenFilters;
                    privateTokenFilters = AnalysisRegistry.class.getDeclaredField("tokenFilters");
                    privateTokenFilters.setAccessible(true);
                    holder.setTokenFilters((Map<String, AnalysisModule.AnalysisProvider<TokenFilterFactory>>) privateTokenFilters.get(analysisRegistry));

                    Field privateCharFilters;
                    privateCharFilters = AnalysisRegistry.class.getDeclaredField("charFilters");
                    privateCharFilters.setAccessible(true);
                    holder.setCharFilters((Map<String, AnalysisModule.AnalysisProvider<CharFilterFactory>>) privateCharFilters.get(analysisRegistry));

                    Field privateNormalizers;
                    privateNormalizers = AnalysisRegistry.class.getDeclaredField("normalizers");
                    privateNormalizers.setAccessible(true);
                    holder.setNormalizers((Map<String, AnalysisModule.AnalysisProvider<AnalyzerProvider<?>>>) privateNormalizers.get(analysisRegistry));

                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                return holder;
            }
        });

        System.out.println("analyzers:");
        System.out.println(" - " +  privsHolder.getAnalyzers().keySet());

        System.out.println("tokenizers:");
        System.out.println(" - " + privsHolder.getTokenizers().keySet());

        System.out.println("tokenFilters:");
        System.out.println(" - " + privsHolder.getTokenFilters().keySet());

        System.out.println("charFilters:");
        System.out.println(" - " + privsHolder.getCharFilters().keySet());

        System.out.println("normalizers:");
        System.out.println(" - " + privsHolder.getNormalizers().keySet());

        System.out.println("=================");
    }

    private class PriviledgedHolder {
        Map<String, AnalysisModule.AnalysisProvider<AnalyzerProvider<?>>> analyzers;
        Map<String, AnalysisModule.AnalysisProvider<TokenizerFactory>> tokenizers;
        Map<String, AnalysisModule.AnalysisProvider<TokenFilterFactory>> tokenFilters;
        Map<String, AnalysisModule.AnalysisProvider<CharFilterFactory>> charFilters;
        Map<String, AnalysisModule.AnalysisProvider<AnalyzerProvider<?>>> normalizers;

        public void setAnalyzers(Map<String, AnalysisModule.AnalysisProvider<AnalyzerProvider<?>>> analyzers) {
            this.analyzers = analyzers;
        }

        public Map<String, AnalysisModule.AnalysisProvider<AnalyzerProvider<?>>> getAnalyzers() {
            return analyzers;
        }

        public void setTokenizers(Map<String, AnalysisModule.AnalysisProvider<TokenizerFactory>> tokenizers) {
            this.tokenizers = tokenizers;
        }

        public Map<String, AnalysisModule.AnalysisProvider<TokenizerFactory>> getTokenizers() {
            return tokenizers;
        }

        public void setTokenFilters(Map<String, AnalysisModule.AnalysisProvider<TokenFilterFactory>> tokenFilters) {
            this.tokenFilters = tokenFilters;
        }

        public Map<String, AnalysisModule.AnalysisProvider<TokenFilterFactory>> getTokenFilters() {
            return tokenFilters;
        }

        public void setCharFilters(Map<String, AnalysisModule.AnalysisProvider<CharFilterFactory>> charFilters) {
            this.charFilters = charFilters;
        }

        public Map<String, AnalysisModule.AnalysisProvider<CharFilterFactory>> getCharFilters() {
            return charFilters;
        }

        public void setNormalizers(Map<String, AnalysisModule.AnalysisProvider<AnalyzerProvider<?>>> normalizers) {
            this.normalizers = normalizers;
        }

        public Map<String, AnalysisModule.AnalysisProvider<AnalyzerProvider<?>>> getNormalizers() {
            return normalizers;
        }
    }
}

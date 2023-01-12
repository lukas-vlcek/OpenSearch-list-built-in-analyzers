/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */
package org.opensearch.plugin.action;

import org.opensearch.action.support.nodes.BaseNodeResponse;
import org.opensearch.cluster.node.DiscoveryNode;
import org.opensearch.common.io.stream.StreamInput;
import org.opensearch.common.io.stream.StreamOutput;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import static java.util.Collections.unmodifiableMap;
import static java.util.Collections.unmodifiableSortedSet;

/**
 * A class used to transfer node analyzers info.
 *
 * Every node has a set of analyzers, tokenizers, tokenFilter, charFilter and analyzers.
 * For every AnalysisPlugin installed on the node we also get list of components provided
 * by individual plugins.
 */
public class NodeAnalyzersInfo extends BaseNodeResponse {

    private final SortedSet<String> analyzersKeySet;
    private final SortedSet<String> tokenizersKeySet;
    private final SortedSet<String> tokenFiltersKeySet;
    private final SortedSet<String> charFiltersKeySet;
    private final SortedSet<String> normalizersKeySet;

    private final Map<String, AnalysisPluginComponents> nodeAnalysisPlugins;

    public static class AnalysisPluginComponents implements Comparable<AnalysisPluginComponents> {
        private final String pluginName;
        private final SortedSet<String> analyzersKeySet;
        private final SortedSet<String> tokenizersKeySet;
        private final SortedSet<String> tokenFiltersKeySet;
        private final SortedSet<String> charFiltersKeySet;
        private final SortedSet<String> hunspellDictionaries;

        public AnalysisPluginComponents(
                final String pluginName,
                final Set<String> analyzersKeySet,
                final Set<String> tokenizersKeySet,
                final Set<String> tokenFiltersKeySet,
                final Set<String> charFiltersKeySet,
                final Set<String> hunspellDictionaries
        ) {
            this.pluginName = pluginName;
            this.analyzersKeySet = unmodifiableSortedSet(new TreeSet<>(analyzersKeySet));
            this.tokenizersKeySet = unmodifiableSortedSet(new TreeSet<>(tokenizersKeySet));
            this.tokenFiltersKeySet = unmodifiableSortedSet(new TreeSet<>(tokenFiltersKeySet));
            this.charFiltersKeySet = unmodifiableSortedSet(new TreeSet<>(charFiltersKeySet));
            this.hunspellDictionaries = unmodifiableSortedSet(new TreeSet<>(hunspellDictionaries));
        }

        public AnalysisPluginComponents(StreamInput in) throws IOException {
            this.pluginName = in.readString();
            this.analyzersKeySet = unmodifiableSortedSet(new TreeSet<>(in.readSet(StreamInput::readString)));
            this.tokenizersKeySet = unmodifiableSortedSet(new TreeSet<>(in.readSet(StreamInput::readString)));
            this.tokenFiltersKeySet = unmodifiableSortedSet(new TreeSet<>(in.readSet(StreamInput::readString)));
            this.charFiltersKeySet = unmodifiableSortedSet(new TreeSet<>(in.readSet(StreamInput::readString)));
            this.hunspellDictionaries = unmodifiableSortedSet(new TreeSet<>(in.readSet(StreamInput::readString)));
        }

        public void writeTo(StreamOutput out) throws IOException {
            out.writeString(this.pluginName);
            out.writeStringCollection(this.analyzersKeySet);
            out.writeStringCollection(this.tokenizersKeySet);
            out.writeStringCollection(this.tokenFiltersKeySet);
            out.writeStringCollection(this.charFiltersKeySet);
            out.writeStringCollection(this.hunspellDictionaries);
        }

        public String getPluginName() {
            return pluginName;
        }
        public Set<String> getAnalyzersKeySet() {
            return analyzersKeySet;
        }
        public Set<String> getTokenizersKeySet() {
            return tokenizersKeySet;
        }
        public Set<String> getTokenFiltersKeySet() {
            return tokenFiltersKeySet;
        }
        public Set<String> getCharFiltersKeySet() {
            return charFiltersKeySet;
        }
        public Set<String> getHunspellDictionaries() {
            return hunspellDictionaries;
        }

        /**
         * @param o the object to be compared.
         * @return
         */
        @Override
        public int compareTo(AnalysisPluginComponents o) {
            // TODO: Define better compare function.
            return this.pluginName.compareTo(o.pluginName);
        }
    }

    protected NodeAnalyzersInfo(StreamInput in) throws IOException {
        super(in);
        this.analyzersKeySet = unmodifiableSortedSet(new TreeSet<>(in.readSet(StreamInput::readString)));
        this.tokenizersKeySet = unmodifiableSortedSet(new TreeSet<>(in.readSet(StreamInput::readString)));
        this.tokenFiltersKeySet = unmodifiableSortedSet(new TreeSet<>(in.readSet(StreamInput::readString)));
        this.charFiltersKeySet = unmodifiableSortedSet(new TreeSet<>(in.readSet(StreamInput::readString)));
        this.normalizersKeySet = unmodifiableSortedSet(new TreeSet<>(in.readSet(StreamInput::readString)));
        this.nodeAnalysisPlugins = unmodifiableMap(in.readMap(StreamInput::readString, AnalysisPluginComponents::new));
    }

    public NodeAnalyzersInfo(
            final DiscoveryNode node,
            final Set<String> analyzersKeySet,
            final Set<String> tokenizersKeySet,
            final Set<String> tokenFiltersKeySet,
            final Set<String> charFiltersKeySet,
            final Set<String> normalizersKeySet,
            final Map<String, AnalysisPluginComponents> nodeAnalysisPlugins
            ) {
        super(node);
        this.analyzersKeySet = unmodifiableSortedSet(new TreeSet<>(analyzersKeySet));
        this.tokenizersKeySet = unmodifiableSortedSet(new TreeSet<>(tokenizersKeySet));
        this.tokenFiltersKeySet = unmodifiableSortedSet(new TreeSet<>(tokenFiltersKeySet));
        this.charFiltersKeySet = unmodifiableSortedSet(new TreeSet<>(charFiltersKeySet));
        this.normalizersKeySet = unmodifiableSortedSet(new TreeSet<>(normalizersKeySet));
        this.nodeAnalysisPlugins = unmodifiableMap(nodeAnalysisPlugins);
    }

    public Set<String> getAnalyzersKeySet() {
        return this.analyzersKeySet;
    }
    public Set<String> getTokenizersKeySet() {
        return this.tokenizersKeySet;
    }
    public Set<String> getTokenFiltersKeySet() {
        return this.tokenFiltersKeySet;
    }
    public Set<String> getCharFiltersKeySet() {
        return this.charFiltersKeySet;
    }
    public Set<String> getNormalizersKeySet() {
        return this.normalizersKeySet;
    }

    public Map<String, AnalysisPluginComponents> getNodeAnalysisPlugins() {
        return nodeAnalysisPlugins;
    }

    @Override
    public void writeTo(StreamOutput out) throws IOException {
        super.writeTo(out);
        out.writeStringCollection(this.analyzersKeySet);
        out.writeStringCollection(this.tokenizersKeySet);
        out.writeStringCollection(this.tokenFiltersKeySet);
        out.writeStringCollection(this.charFiltersKeySet);
        out.writeStringCollection(this.normalizersKeySet);
        out.writeMap(this.nodeAnalysisPlugins, StreamOutput::writeString, (o, s) -> s.writeTo(o));
    }
}

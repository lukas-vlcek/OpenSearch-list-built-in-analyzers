/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */
package org.opensearch.plugin.action;

import org.opensearch.action.support.nodes.BaseNodesRequest;
import org.opensearch.common.io.stream.StreamInput;

import java.io.IOException;

/**
 * A request to get a node level analyzers.
 */
public class NodesAnalyzersRequest extends BaseNodesRequest<NodesAnalyzersRequest> {

    /**
     * A constructor.
     */
    public NodesAnalyzersRequest(String... nodesIds) {
        super(nodesIds);
    }

    /**
     * A constructor from {@link }StreamInput}
     * @param in StreamInput
     * @throws IOException When things go wrong
     */
    public NodesAnalyzersRequest(StreamInput in) throws IOException {
        super(in);
    }
}

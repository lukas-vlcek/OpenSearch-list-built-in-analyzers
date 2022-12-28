/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */
package org.opensearch.plugin.action;

import org.opensearch.action.ActionResponse;
import org.opensearch.common.io.stream.StreamInput;
import org.opensearch.common.io.stream.StreamOutput;

import java.io.IOException;

/**
 * TODO
 */
public class NodeAnalyzersResponse extends ActionResponse {

    /**
     * TODO
     * @param in StreamInput
     * @throws IOException TODO
     */
    public NodeAnalyzersResponse(StreamInput in) throws IOException {
        super(in);
    }

    /**
     * TODO
     */
    public NodeAnalyzersResponse() {

    }

    /**
     * TODO
     * @param streamOutput StreamOutput
     * @throws IOException TODO
     */
    @Override
    public void writeTo(StreamOutput streamOutput) throws IOException {
        // TODO
    }
}

/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */
package org.opensearch.plugin.action;

import org.opensearch.action.ActionRequest;
import org.opensearch.action.ActionRequestValidationException;
import org.opensearch.common.io.stream.StreamInput;

import java.io.IOException;

/**
 * TODO
 */
public class NodeAnalyzersRequest extends ActionRequest {

    /**
     * TODO
     */
    public NodeAnalyzersRequest() {
        super();
    }

    /**
     * TODO
     * @param in StreamInput
     * @throws IOException TODO
     */
    public NodeAnalyzersRequest(StreamInput in) throws IOException {
        super(in);
    }

    /**
     * TODO
     */
    @Override
    public ActionRequestValidationException validate() {
        return null;
    }
}

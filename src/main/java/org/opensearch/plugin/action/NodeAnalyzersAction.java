/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */
package org.opensearch.plugin.action;

import org.opensearch.action.ActionType;

/**
 * TODO
 */
public class NodeAnalyzersAction extends ActionType<NodeAnalyzersResponse> {

    /**
     * TODO
     */
    public static final NodeAnalyzersAction INSTANCE = new NodeAnalyzersAction();

    /**
     * TODO
     */
    public static final String NAME = "cluster:monitor/node/analyzers";

    /**
     * TODO
     */
    public NodeAnalyzersAction() {
        super(NAME, NodeAnalyzersResponse::new);
    }
}

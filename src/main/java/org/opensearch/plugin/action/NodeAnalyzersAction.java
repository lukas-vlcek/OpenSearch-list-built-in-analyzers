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
 * ActionType for retrieving a analyzer components.
 */
public class NodeAnalyzersAction extends ActionType<NodesAnalyzersResponse> {

    /**
     * A node level singleton.
     */
    public static final NodeAnalyzersAction INSTANCE = new NodeAnalyzersAction();

    /**
     * The name of the action type.
     */
    public static final String NAME = "cluster:monitor/node/analyzers";

    private NodeAnalyzersAction() {
        super(NAME, NodesAnalyzersResponse::new);
    }
}

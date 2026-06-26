package com.leonoretech.dragonicexplorer.data.model

/**
 * Request body for POST /repos/{owner}/{repo}/actions/workflows/{workflow_id}/dispatches
 * `inputs` keys must match the workflow_dispatch.inputs defined in the YAML workflow file.
 */
data class WorkflowDispatchRequest(
    val ref: String,
    val inputs: Map<String, String>
)

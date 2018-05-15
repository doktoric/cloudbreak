package com.sequenceiq.cloudbreak.core.flow2.chain;

public class FlowChainTriggers {
    public static final String FULL_PROVISION_TRIGGER_EVENT = "FULL_PROVISION_TRIGGER_EVENT";

    public static final String CLUSTER_RESET_CHAIN_TRIGGER_EVENT = "CLUSTER_RESET_CHAIN_TRIGGER_EVENT";

    public static final String CLUSTER_UPGRADE_CHAIN_TRIGGER_EVENT = "CLUSTER_UPGRADE_CHAIN_TRIGGER_EVENT";

    public static final String FULL_UPSCALE_TRIGGER_EVENT = "FULL_UPSCALE_TRIGGER_EVENT";

    public static final String FULL_DOWNSCALE_TRIGGER_EVENT = "FULL_DOWNSCALE_TRIGGER_EVENT";

    public static final String FULL_START_TRIGGER_EVENT = "FULL_START_TRIGGER_EVENT";

    public static final String FULL_STOP_TRIGGER_EVENT = "FULL_STOP_TRIGGER_EVENT";

    public static final String FULL_SYNC_TRIGGER_EVENT = "FULL_SYNC_TRIGGER_EVENT";

    public static final String STACK_REPAIR_TRIGGER_EVENT = "STACK_REPAIR_TRIGGER_EVENT";

    public static final String CLUSTER_REPAIR_TRIGGER_EVENT = "CLUSTER_REPAIR_TRIGGER_EVENT";

    public static final String TERMINATION_TRIGGER_EVENT = "TERMINATION_TRIGGER_EVENT";

    public static final String PROPER_TERMINATION_TRIGGER_EVENT = "PROPER_TERMINATION_TRIGGER_EVENT";

    public static final String EPHEMERAL_CLUSTERS_UPDATE_TRIGGER_EVENT = "EPHEMERAL_CLUSTERS_UPDATE_TRIGGER_EVENT";

    private FlowChainTriggers() {
    }
}
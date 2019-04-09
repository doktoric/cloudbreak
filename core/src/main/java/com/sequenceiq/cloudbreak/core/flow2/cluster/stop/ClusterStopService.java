package com.sequenceiq.cloudbreak.core.flow2.cluster.stop;

import java.time.Duration;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.sequenceiq.cloudbreak.api.endpoint.v4.common.DetailedStackStatus;
import com.sequenceiq.cloudbreak.api.endpoint.v4.common.Status;
import com.sequenceiq.cloudbreak.message.NotificationEventType;
import com.sequenceiq.cloudbreak.core.flow2.stack.CloudbreakFlowMessageService;
import com.sequenceiq.cloudbreak.domain.stack.cluster.Cluster;
import com.sequenceiq.cloudbreak.domain.view.StackView;
import com.sequenceiq.cloudbreak.exception.NotFoundException;
import com.sequenceiq.cloudbreak.message.Msg;
import com.sequenceiq.cloudbreak.service.StackUpdater;
import com.sequenceiq.cloudbreak.service.cluster.ClusterService;
import com.sequenceiq.cloudbreak.util.StackUtil;

@Service
public class ClusterStopService {
    @Inject
    private ClusterService clusterService;

    @Inject
    private StackUpdater stackUpdater;

    @Inject
    private CloudbreakFlowMessageService flowMessageService;

    @Inject
    private StackUtil stackUtil;

    public void stoppingCluster(long stackId) {
        updateClusterUptime(stackId);
        flowMessageService.fireEventAndLog(stackId, Msg.CLUSTER_STOPPING, NotificationEventType.UPDATE_IN_PROGRESS);
        clusterService.updateClusterStatusByStackId(stackId, Status.STOP_IN_PROGRESS);
    }

    private void updateClusterUptime(long stackId) {
        Cluster cluster = clusterService.retrieveClusterByStackIdWithoutAuth(stackId)
                .orElseThrow(NotFoundException.notFound("cluster", stackId));
        cluster.setUptime(Duration.ofMillis(stackUtil.getUptimeForCluster(cluster, true)).toString());
        clusterService.updateCluster(cluster);
    }

    public void clusterStopFinished(long stackId) {
        clusterService.updateClusterStatusByStackId(stackId, Status.STOPPED);
        flowMessageService.fireEventAndLog(stackId, Msg.CLUSTER_STOPPED, NotificationEventType.STOPPED);
    }

    public void handleClusterStopFailure(StackView stackView, String errorReason) {
        clusterService.updateClusterStatusByStackId(stackView.getId(), Status.STOP_FAILED);
        stackUpdater.updateStackStatus(stackView.getId(), DetailedStackStatus.AVAILABLE, "The cluster could not be stopped: " + errorReason);
        flowMessageService.fireEventAndLog(stackView.getId(), Msg.CLUSTER_STOP_FAILED, NotificationEventType.STOP_FAILED, errorReason);
    }
}

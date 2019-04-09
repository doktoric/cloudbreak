package com.sequenceiq.cloudbreak.core.flow2.cluster.reset;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.sequenceiq.cloudbreak.api.endpoint.v4.common.DetailedStackStatus;
import com.sequenceiq.cloudbreak.api.endpoint.v4.common.Status;
import com.sequenceiq.cloudbreak.message.NotificationEventType;
import com.sequenceiq.cloudbreak.core.flow2.stack.CloudbreakFlowMessageService;
import com.sequenceiq.cloudbreak.domain.view.StackView;
import com.sequenceiq.cloudbreak.message.Msg;
import com.sequenceiq.cloudbreak.service.CloudbreakException;
import com.sequenceiq.cloudbreak.service.StackUpdater;
import com.sequenceiq.cloudbreak.service.cluster.ClusterService;

@Service
public class ClusterResetService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClusterResetService.class);

    @Inject
    private CloudbreakFlowMessageService flowMessageService;

    @Inject
    private ClusterService clusterService;

    @Inject
    private StackUpdater stackUpdater;

    public void resetCluster(long stackId) {
        flowMessageService.fireEventAndLog(stackId, Msg.CLUSTER_RESET, NotificationEventType.UPDATE_IN_PROGRESS);
    }

    public void handleResetClusterFailure(StackView stackView, Exception exception) {
        String errorMessage = exception instanceof CloudbreakException && exception.getCause() != null
                ? exception.getCause().getMessage() : exception.getMessage();
        clusterService.updateClusterStatusByStackId(stackView.getId(), Status.CREATE_FAILED, errorMessage);
        stackUpdater.updateStackStatus(stackView.getId(), DetailedStackStatus.AVAILABLE);
        flowMessageService.fireEventAndLog(stackView.getId(), Msg.CLUSTER_CREATE_FAILED, NotificationEventType.CREATE_FAILED, errorMessage);
    }
}

package com.sequenceiq.periscope.service;

import java.util.Collection;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.sequenceiq.cloudbreak.api.endpoint.v4.common.Status;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.response.AutoscaleStackV4Response;
import com.sequenceiq.cloudbreak.client.CloudbreakIdentityClient;
import com.sequenceiq.periscope.monitor.context.ClusterCreationEvaluatorContext;
import com.sequenceiq.periscope.monitor.evaluator.ClusterCreationEvaluator;
import com.sequenceiq.periscope.monitor.executor.ExecutorServiceWithRegistry;
import com.sequenceiq.periscope.service.configuration.CloudbreakClientConfiguration;

@Service
public class StackCollectorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StackCollectorService.class);

    private static final Lock LOCK = new ReentrantLock();

    @Inject
    private ApplicationContext applicationContext;

    @Inject
    private ExecutorServiceWithRegistry executorServiceWithRegistry;

    @Inject
    private CloudbreakClientConfiguration cloudbreakClientConfiguration;

    @Inject
    private RejectedThreadService rejectedThreadService;

    public void collectStackDetails() {
        if (LOCK.tryLock()) {
            try {
                CloudbreakIdentityClient cloudbreakClient = cloudbreakClientConfiguration.cloudbreakClient();
                Collection<AutoscaleStackV4Response> allStacks = cloudbreakClient.autoscaleEndpoint().getAllForAutoscale().getResponses();
                for (AutoscaleStackV4Response stack : allStacks) {
                    Status clusterStatus = stack.getClusterStatus();
                    try {
                        LOGGER.debug("Evaluate cluster management for stack: {} (ID:{})", stack.getName(), stack.getStackId());
                        ClusterCreationEvaluator clusterCreationEvaluator = applicationContext.getBean(ClusterCreationEvaluator.class);
                        clusterCreationEvaluator.setContext(new ClusterCreationEvaluatorContext(stack));
                        executorServiceWithRegistry.submitIfAbsent(clusterCreationEvaluator, stack.getStackId());
                        LOGGER.debug("Succesfully submitted, the stack id: {}.", stack.getStackId());
                        rejectedThreadService.remove(stack);
                    } catch (RejectedExecutionException ignore) {

                    }
                }
            } catch (Exception ex) {
                LOGGER.error("New clusters could not be synchronized from Cloudbreak.", ex);
            } finally {
                LOCK.unlock();
            }
        }
    }
}

package com.sequenceiq.cloudbreak.controller.v4;

import java.util.Set;

import javax.inject.Inject;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;

import com.sequenceiq.cloudbreak.api.endpoint.v4.mpacks.ManagementPackV4Endpoint;
import com.sequenceiq.cloudbreak.api.endpoint.v4.mpacks.request.ManagementPackV4Request;
import com.sequenceiq.cloudbreak.api.endpoint.v4.mpacks.response.ManagementPackV4Response;
import com.sequenceiq.cloudbreak.api.endpoint.v4.mpacks.response.ManagementPackV4Responses;
import com.sequenceiq.cloudbreak.api.util.ConverterUtil;
import com.sequenceiq.cloudbreak.common.type.ResourceEvent;
import com.sequenceiq.cloudbreak.domain.ManagementPack;
import com.sequenceiq.cloudbreak.service.RestRequestThreadLocalService;
import com.sequenceiq.cloudbreak.service.mpack.ManagementPackService;
import com.sequenceiq.cloudbreak.service.user.UserService;
import com.sequenceiq.cloudbreak.workspace.controller.WorkspaceEntityType;
import com.sequenceiq.cloudbreak.workspace.model.User;

@Controller
@WorkspaceEntityType(ManagementPack.class)
public class ManagementPackV4Controller extends NotificationController implements ManagementPackV4Endpoint {

    @Inject
    private ManagementPackService mpackService;

    @Inject
    private UserService userService;

    @Inject
    private RestRequestThreadLocalService restRequestThreadLocalService;

    @Inject
    private ConverterUtil converterUtil;

    @Override
    public ManagementPackV4Responses listByWorkspace(Long workspaceId) {
        return new ManagementPackV4Responses(converterUtil.convertAllAsSet(mpackService.findAllByWorkspaceId(workspaceId), ManagementPackV4Response.class));
    }

    @Override
    public ManagementPackV4Response getByNameInWorkspace(Long workspaceId, String name) {
        ManagementPack managementPack = mpackService.getByNameForWorkspaceId(name, workspaceId);
        return converterUtil.convert(managementPack, ManagementPackV4Response.class);
    }

    @Override
    public ManagementPackV4Response createInWorkspace(Long workspaceId, @Valid ManagementPackV4Request request) {
        ManagementPack managementPack = converterUtil.convert(request, ManagementPack.class);
        User user = userService.getOrCreate(restRequestThreadLocalService.getCloudbreakUser());
        managementPack = mpackService.create(managementPack, workspaceId, user);
        ManagementPackV4Response response = converterUtil.convert(managementPack, ManagementPackV4Response.class);
        notify(ResourceEvent.MANAGEMENT_PACK_CREATED);
        return response;
    }

    @Override
    public ManagementPackV4Response deleteInWorkspace(Long workspaceId, String name) {
        ManagementPack deleted = mpackService.deleteByNameFromWorkspace(name, workspaceId);
        ManagementPackV4Response response = converterUtil.convert(deleted, ManagementPackV4Response.class);
        notify(ResourceEvent.MANAGEMENT_PACK_DELETED);
        return response;
    }

    @Override
    public ManagementPackV4Responses deleteMultipleInWorkspace(Long workspaceId, Set<String> names) {
        Set<ManagementPack> response = mpackService.deleteMultipleByNameFromWorkspace(names, workspaceId);
        notify(ResourceEvent.MANAGEMENT_PACK_DELETED);
        return new ManagementPackV4Responses(converterUtil.convertAllAsSet(response, ManagementPackV4Response.class));
    }
}

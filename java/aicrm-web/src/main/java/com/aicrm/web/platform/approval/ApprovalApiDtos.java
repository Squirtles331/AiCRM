package com.aicrm.web.platform.approval;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

final class ApprovalApiDtos {
    private ApprovalApiDtos() { }
    record Node(@NotBlank String name, @NotBlank String decisionMode, @NotEmpty List<@NotNull Long> approverUserIds, JsonNode condition) { }
    record CreateDefinitionRequest(@NotBlank String code, @NotBlank String name, @NotBlank String resourceType, @NotEmpty List<@Valid Node> nodes) { }
    record VersionRequest(@NotNull Long version) { }
    record TaskActionRequest(@NotNull Long version, String comment) { }
    record TransferRequest(@NotNull Long version, @NotNull Long toUserId, String comment) { }
    record DefinitionView(String id, String code, String name, String resourceType, int definitionVersion, String status, long version) { }
    record TaskView(String id, String instanceId, int nodeNo, String nodeName, String decisionMode, String approverUserId, String status, long version) { }
    record ApprovalActionView(String instanceId, String status, long instanceVersion, String quoteId, String quoteStatus, Long quoteVersion) { }
}

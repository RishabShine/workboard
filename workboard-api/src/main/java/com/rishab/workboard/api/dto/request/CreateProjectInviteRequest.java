package com.rishab.workboard.api.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateProjectInviteRequest {

    private Long recipientUserId;

}

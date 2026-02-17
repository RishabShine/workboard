package com.rishab.workboard.api.dto.request;

import lombok.Getter;
import lombok.Setter;

/*
projectId is sent in the url
sender ID retrieved from JWT
 */
@Getter
@Setter
public class InviteRequest {

    private Long recipientId;

}

package com.payflow.app.dto.request;

import com.payflow.app.enums.Status;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatusUpdateRequest {
	private Status status;
}

package com.naph.startup.dto.mpesa;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Body {

	@JsonProperty("stkCallback")
	private StkCallback stkCallback;
}
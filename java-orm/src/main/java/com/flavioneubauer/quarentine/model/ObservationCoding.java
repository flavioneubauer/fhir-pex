package com.flavioneubauer.quarentine.model;

import lombok.Data;

@Data
public class ObservationCoding {
	private String system;
	private String code;
	private String display;
}

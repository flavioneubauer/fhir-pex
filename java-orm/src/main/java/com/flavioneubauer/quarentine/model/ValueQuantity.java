package com.flavioneubauer.quarentine.model;

import lombok.Data;

@Data
public class ValueQuantity {
	private Double value;
	private String unit;
	private String system;
	private String code;
}

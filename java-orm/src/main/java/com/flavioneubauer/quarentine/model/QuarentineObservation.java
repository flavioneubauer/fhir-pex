package com.flavioneubauer.quarentine.model;

import lombok.Data;

@Data
public class QuarentineObservation {

	private String id;
	private String status;
	private ObservationCode code;
	private Subject subject;
	private ValueQuantity valueQuantity;
	private ObservationCode valueCodeableConcept;
}

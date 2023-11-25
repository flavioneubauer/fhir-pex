package com.flavioneubauer.patient.service;

import com.flavioneubauer.patient.model.Observation;
import com.flavioneubauer.quarentine.model.QuarentineObservation;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDateTime;

@ApplicationScoped
public class ObservationMapperService {

	public Observation fromQuarentine(QuarentineObservation quarentineObservation) {
		var observation = new Observation();
		observation.setType(quarentineObservation.getCode()
				.getCoding()
				.iterator()
				.next()
				.getCode());
		observation.setMeasure(quarentineObservation.getValueQuantity().getValue().toString());
		observation.setTimestamp(LocalDateTime.now());
		return observation;
	}
}

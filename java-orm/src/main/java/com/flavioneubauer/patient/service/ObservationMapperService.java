package com.flavioneubauer.patient.service;

import com.flavioneubauer.patient.model.Observation;
import com.flavioneubauer.quarentine.model.QuarentineObservation;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDateTime;

@ApplicationScoped
public class ObservationMapperService {

	public Observation fromQuarentine(QuarentineObservation quarentineObservation) {
		var observation = new Observation();
		observation.setName(quarentineObservation.getCode().getText());
		observation.setCode(quarentineObservation.getCode()
				.getCoding()
				.iterator()
				.next()
				.getCode());
		if(quarentineObservation.getValueQuantity() != null){
			observation.setValue(quarentineObservation.getValueQuantity().getValue().toString());
		}else if(quarentineObservation.getValueCodeableConcept() != null){
			observation.setValue(quarentineObservation.getValueCodeableConcept()
					.getCoding()
					.iterator()
					.next()
					.getDisplay());
		}
		observation.setTimestamp(LocalDateTime.now());
		return observation;
	}
}

package com.flavioneubauer.patient.service;

import java.util.Optional;

import com.flavioneubauer.patient.model.Patient;
import com.flavioneubauer.quarentine.model.QuarentineObservation;

import io.vertx.core.eventbus.EventBus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class PatientService {

	@Inject
	ObservationMapperService observationMapperService;

	@Transactional
	public Patient addObservation(String id, QuarentineObservation quarentineObservation) {
		Optional<Patient> patientOptional = Patient.find("reference = ?1", quarentineObservation.getSubject()
						.getReference())
				.firstResultOptional();
		if (patientOptional.isPresent()) {
			Patient patient = patientOptional.get();
			patient.getObservationList()
					.add(observationMapperService.fromQuarentine(quarentineObservation));
			patient.persistAndFlush();
			return patient;
		}
		return null;
	}
}

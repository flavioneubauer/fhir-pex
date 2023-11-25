package com.flavioneubauer.patient.service;

import com.flavioneubauer.patient.model.Patient;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class DataInitialization {

	@Transactional
	public void insertPatients(@Observes StartupEvent ev){
		var count = Patient.count();
		if(count == 0){
			Patient patient = new Patient();
			patient.setReference("urn:uuid:274f5452-2a39-44c4-a7cb-f36de467762e");
			patient.setDrinks(true);
			patient.setSmoke(true);
			patient.setWeight(77.2);
			patient.setPhisicalActivitiesPerWeek(3);
			patient.setBloodPreasure("15:9");
			patient.persistAndFlush();
		}
	}
}

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
			Patient patient1 = new Patient();
			patient1.setReference("urn:uuid:274f5452-2a39-44c4-a7cb-f36de467762e");
			patient1.setDrinks(true);
			patient1.setSmoke(true);
			patient1.setWeight(77.2);
			patient1.setPhisicalActivitiesPerWeek(3);
			patient1.setBloodPreasure("15:9");
			patient1.persistAndFlush();

			Patient patient2 = new Patient();
			patient2.setReference("Patient/12345");
			patient2.setDrinks(false);
			patient2.setSmoke(true);
			patient2.setWeight(100.2);
			patient2.setPhisicalActivitiesPerWeek(1);
			patient2.setBloodPreasure("10:9");
			patient2.persistAndFlush();
		}
	}
}

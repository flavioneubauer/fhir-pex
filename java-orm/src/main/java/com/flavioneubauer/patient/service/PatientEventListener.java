package com.flavioneubauer.patient.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.*;

@ApplicationScoped
public class PatientEventListener {

	@Inject
	@Channel("patient-incoming")
	Emitter<String> emitter;

	@Incoming("patient-incoming")
	@Outgoing("patient-outgoing")
	public Message<String> onIncomingMessage(Message<String> message) {
		return message.withPayload(message.getPayload().toUpperCase());
	}

}

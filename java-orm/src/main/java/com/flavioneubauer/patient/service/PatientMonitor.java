package com.flavioneubauer.patient.service;

import com.flavioneubauer.quarentine.model.MonitorEventDto;
import io.quarkus.vertx.ConsumeEvent;
import io.vertx.core.Vertx;
import io.vertx.ext.bridge.PermittedOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.sockjs.SockJSBridgeOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class PatientMonitor {

	private static final String ADDRESS = "monitor";
	private final Vertx vertx;

	public PatientMonitor(Vertx vertx){
		this.vertx = vertx;
	}

	@ConsumeEvent(value = ADDRESS)
	public void listen(MonitorEventDto monitorEventDto){
		log.info("new message! + " + monitorEventDto);
	}

	public void start(@Observes Router router){
		SockJSBridgeOptions sockJSBridgeOptions = new SockJSBridgeOptions()
				.addInboundPermitted(new PermittedOptions().setAddress(ADDRESS))
				.addOutboundPermitted(new PermittedOptions().setAddress(ADDRESS))
				.setPingTimeout(5000L);
		router.route("/eventbus/*").subRouter(SockJSHandler.create(vertx).bridge(sockJSBridgeOptions));
	}
}

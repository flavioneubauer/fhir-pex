package com.flavioneubauer.patient.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@EqualsAndHashCode(of = {"id"}, callSuper = true)
@Entity
public class Patient extends PanacheEntityBase {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	private Double weight;

	@Column(nullable = false)
	private String reference;

	private boolean smoke;
	private boolean drinks;
	private Integer phisicalActivitiesPerWeek;
	private String bloodPreasure;

	@OneToMany(cascade = CascadeType.PERSIST)
	private List<Observation> observationList = new ArrayList<>();
}

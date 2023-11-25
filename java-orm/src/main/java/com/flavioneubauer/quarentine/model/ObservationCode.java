package com.flavioneubauer.quarentine.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ObservationCode {
	private List<ObservationCoding> coding = new ArrayList<>();
	private String text;
}

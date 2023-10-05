package com.wipro.entity;

import java.time.LocalDate;
import java.time.Year;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Vehicle {
	
	@Id @GeneratedValue
	private Integer vehicleId;
	
	@NotNull
	private String vehicleName;
	
	private Integer vehicleNumber, seatingCapacity, vehicleCost, showroomPrice;
	private String vehicleMake, vehicleModel, stateOfRegistration, cityOfRegistration, fuelType;
	
	@DateTimeFormat(pattern = "yyyy")
	private Year yearOfPurchase, yearOfManufacture;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate dateOfRegistration;
	
	@JsonIgnore
	@OneToOne(cascade = CascadeType.ALL) @JoinColumn(name = "quote_id", referencedColumnName = "quoteId")
	private Quote quote;
}

package com.wipro.entity;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Quote {

	@Id @GeneratedValue
	private Integer quoteId;
	
	@JsonIgnore
	@OneToOne @JoinColumn(name = "vehicle_id", referencedColumnName = "vehicleId")
	private Vehicle vehicleId;
	
	@JsonIgnore
	@ManyToOne @JoinColumn(name = "insurance_provider", referencedColumnName = "insuranceProviderName")
	private InsuranceProvider insuranceProvider;
	
	@JsonIgnore
	@ManyToOne @JoinColumn(name = "credit_card", referencedColumnName = "creditCardNumber")
	private CreditCard creditCard;
	
	@NotNull @DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate policyStartDate, previousPolicyExpiryDate, validityPeriod;
	
	private Integer insuranceCoverInMonths;
	private String validityStatus, policyType; 		//new //renewal
}

package com.wipro.entity;

import org.hibernate.annotations.ColumnDefault;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class InsuranceProvider {

	@Id
	private String insuranceProviderName;
	
	@ColumnDefault(value = "95")
	private Integer insuranceDeclaredValue;
	private Integer discount;
}

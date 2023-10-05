package com.wipro.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Driver {

	@Id @Min(10000)
	private Integer licenseNumber;
	
	@OneToOne @JoinColumn(name = "user_id", referencedColumnName = "userId")
	private UserProfile userId;
	
	@JsonIgnore
	@OneToOne(fetch = FetchType.LAZY) @JoinColumn(name = "driver_name", referencedColumnName = "userName")
	private UserProfile driverName;
	
	@ManyToOne @JoinColumn(name = "vehicle_id", referencedColumnName = "vehicleId")
	private Vehicle vehicleId;
}

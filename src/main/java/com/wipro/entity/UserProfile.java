package com.wipro.entity;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class UserProfile {
	
	@Id @GeneratedValue
	private Integer userId;
	
	@NotNull @Column(unique = true)
	private String userName;
	
	@NotNull @DateTimeFormat(pattern = "yyy-MM-dd")
	private LocalDate DOB;
	private String gender, profession, permanentAddress, presentAddress;
	
	@Min(1000000000)
	private Long contactNumber;
	
	@Email @NotNull @Column(unique = true) 
	private String emailId;
	
	@Size(min = 4) @Column(unique = true)
	private String password;
	
	private String role;
		
}

package com.wipro.entity;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class CreditCard {
	
	@Id @NotNull @Min(1000000000) @Column(unique = true)
	private Long creditCardNumber;
	
	@ManyToOne
	@JoinColumn(name = "user_id", referencedColumnName = "userId")
 	private UserProfile userId;
	
	@Min(10000)
	private Long balance;
	
	@NotNull
	private String cardName;
	
	@DateTimeFormat(pattern = "MM-yyyy")
	private LocalDate validFrom, validTo;
}

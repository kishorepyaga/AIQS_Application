package com.wipro.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wipro.entity.Vehicle;

public interface VehicleRepository extends JpaRepository<Vehicle, Integer>{
	
}

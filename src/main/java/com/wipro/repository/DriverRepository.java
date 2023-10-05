package com.wipro.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wipro.entity.Driver;
import com.wipro.entity.UserProfile;
import com.wipro.entity.Vehicle;

public interface DriverRepository extends JpaRepository<Driver, Integer>{
	Optional<Driver> findByDriverName(UserProfile userName);
	
	Optional<Driver> findByVehicleId(Vehicle vehicle);
	
	List<Driver> findAllByVehicleId(Vehicle vehicle);
}

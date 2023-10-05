package com.wipro.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wipro.entity.InsuranceProvider;

public interface InsuranceProviderRepository extends JpaRepository<InsuranceProvider, String>{

}

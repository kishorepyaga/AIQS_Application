package com.wipro.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import com.wipro.entity.CreditCard;
import com.wipro.entity.UserProfile;

import jakarta.transaction.Transactional;

public interface CreditCardRepository extends JpaRepository<CreditCard, Long>{
	
	@Transactional @Modifying
	void deleteAllByUserId(UserProfile user);
	
	List<CreditCard> findAllByUserId(UserProfile user);
}

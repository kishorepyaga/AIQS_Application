package com.wipro.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.wipro.entity.CreditCard;
import com.wipro.entity.Quote;

import jakarta.transaction.Transactional;

public interface QuoteRepository extends JpaRepository<Quote, Integer>{

	//Expected to fire the query without Entities relations but coudn't
	@Transactional
	@Modifying @Query("update Quote q set q.vehicleId = :vehicleId where q.quoteId = :quoteId")
	void updateQuote(@Param("quoteId") Integer quoteId, @Param("vehicleId") Integer vehicleId);
	
	Optional<Quote> findByCreditCard(CreditCard creditCard);
	
	List<Quote> findAllByCreditCard(CreditCard creditCard);
}

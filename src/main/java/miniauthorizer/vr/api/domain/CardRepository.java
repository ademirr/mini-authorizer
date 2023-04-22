package miniauthorizer.vr.api.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import jakarta.persistence.EntityNotFoundException;

public interface CardRepository extends JpaRepository<Card, Long> {
	
	Card findByNumeroCartao(String cardNumber) throws EntityNotFoundException;

}

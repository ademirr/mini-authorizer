package miniauthorizer.vr.api.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<Card, Long> {
	
	Card findByNumeroCartao(String cardNumber);

}

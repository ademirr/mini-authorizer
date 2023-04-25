package miniauthorizer.vr.api.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class CardRepositoryTest {

	@Autowired
	private CardRepository repository;
	
	@Autowired
	private TestEntityManager em;
	
	@Test
	void test() {
		saveCard("1234567890", "1234", new BigDecimal("500.00").setScale(2, RoundingMode.UP));
		Card card = repository.findByNumeroCartao("1234567890");
		assertEquals("1234567890", card.getNumeroCartao());
		Card card2 = repository.findByNumeroCartao("1234567891");
		assertNull(card2);
	}
	
	private Card saveCard(String numeroCartao, String senha, BigDecimal saldoInicial) {
		Card card = new Card(numeroCartao, senha, saldoInicial);
		em.persist(card);
		return card;
	}

}

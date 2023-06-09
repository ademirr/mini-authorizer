package miniauthorizer.vr.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import miniauthorizer.vr.api.domain.Card;
import miniauthorizer.vr.api.domain.CardRepository;
import miniauthorizer.vr.api.domain.TransactionDTO;
import miniauthorizer.vr.api.infra.InvalidPasswordException;
import miniauthorizer.vr.api.infra.TransactionCardNotFoundException;

@SpringBootTest
@AutoConfigureMockMvc
class TransactionServiceTest {
	
	@Autowired
	private TransactionService transactionService;
	
	@MockBean
    private CardRepository repository;
	
	static final Map<String, Card> cards = new HashMap<String, Card>();

	@BeforeAll
	static void initialize() {
		Card card = new Card("1234567890", "1234", new BigDecimal("500.00").setScale(2, RoundingMode.UP));
		Card card2 = new Card("1234567891", "1235", new BigDecimal("500.00").setScale(2, RoundingMode.UP));
		Card card3 = new Card("1234567892", "1236", new BigDecimal("500.00").setScale(2, RoundingMode.UP));
		
		cards.put("1234567890", card);
		cards.put("1234567891", card2);
		cards.put("1234567892", card3);
	}
	
	@Test
	@DisplayName("Deveria concluir a transação com sucesso, subtraindo o valor da transação do saldo do cartão")
	void validateTransactionOK() {
		TransactionDTO dto = new TransactionDTO("1234567890", "1234", new BigDecimal("100.00"));
		when(repository.findByNumeroCartao(any())).thenReturn(cards.get(dto.numeroCartao()));
		Card card = transactionService.executeTransaction(dto);
		assertEquals(card.getSaldo(), new BigDecimal("400.00").setScale(2, RoundingMode.UP));
	}
	
	@Test
	@DisplayName("Deveria lançar exceção de cartão não encontrado")
	void validateTransactionCardNumberNotOK() {
		TransactionDTO dto = new TransactionDTO("123456789", "1234", new BigDecimal("100.00"));
		when(repository.findByNumeroCartao(any())).thenReturn(cards.get(dto.numeroCartao()));
		assertThrows(TransactionCardNotFoundException.class, () -> transactionService.executeTransaction(dto));
	}
	
	@Test
	@DisplayName("Deveria lançar exceção de senha inválida")
	void validateTransactionPasswordNotOK() {
		TransactionDTO dto = new TransactionDTO("1234567891", "123", new BigDecimal("100.00"));
		when(repository.findByNumeroCartao(any())).thenReturn(cards.get(dto.numeroCartao()));
		assertThrows(InvalidPasswordException.class, () -> transactionService.executeTransaction(dto));
	}

}

package miniauthorizer.vr.api.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import jakarta.validation.ConstraintViolationException;
import miniauthorizer.vr.api.domain.Card;
import miniauthorizer.vr.api.domain.CardDTO;
import miniauthorizer.vr.api.domain.CardDTOResponse;
import miniauthorizer.vr.api.domain.TransactionDTO;
import miniauthorizer.vr.api.infra.InvalidPasswordException;
import miniauthorizer.vr.api.infra.TransactionCardNotFoundException;
import miniauthorizer.vr.api.service.MiniAuthorizerService;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
class MiniAuthorizerControllerTest {

	@Autowired
	private MockMvc mvc;

    @Autowired
    private JacksonTester<CardDTO> cardDTOJson;

    @Autowired
    private JacksonTester<CardDTOResponse> cardDTOResponseJson;

    @Autowired
    private JacksonTester<String> numeroCartaoJson;

    @Autowired
    private JacksonTester<BigDecimal> saldoJson;
    
    @Autowired
    private JacksonTester<TransactionDTO> transactionDTOJson;
	
	@MockBean
	private MiniAuthorizerService service;
	
	private static final BigDecimal SALDO_INICIAL = new BigDecimal("500.00");

	@Test
	@DisplayName("Deveria devolver 400 quando está sem corpo")
	void testCreateCard1() throws Exception {
		var response = mvc.perform(post("/cartoes"))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}
	
	@Test
	@DisplayName("Deveria devolver 400 quando está com o número do cartão em branco")
	void testCreateCard2() throws Exception {
		var cardDTO = new CardDTO(
                "",
                "1234");

        when(service.createCard(any(), any())).thenReturn(new Card(cardDTO.numeroCartao(), cardDTO.senha(), SALDO_INICIAL));

        var response = mvc
                .perform(post("/cartoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cardDTOJson.write(cardDTO).getJson()))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}
	
	@Test
	@DisplayName("Deveria devolver codigo http 422 quando o cartao já existente")
	void testCreateCard3() throws Exception {
		var cardDTO = new CardDTO(
                "12345",
                "1234");

        when(service.createCard(any(), any())).thenThrow(new DataIntegrityViolationException(""));

        var response = mvc
                .perform(post("/cartoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cardDTOJson.write(cardDTO).getJson()))
                .andReturn().getResponse();

        var cardDTOResponse = new CardDTOResponse(
                "1234",
                "12345"
        		);
        var jsonEsperado = cardDTOResponseJson.write(cardDTOResponse).getJson();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
        assertThat(response.getContentAsString()).isEqualTo(jsonEsperado);
	}
	
	@Test
	@DisplayName("Deveria devolver codigo http 201 quando informacoes estao validas")
	void testCreateCard4() throws Exception {
		var cardDTO = new CardDTO(
                "12345",
                "1234");

        when(service.createCard(any(), any())).thenReturn(new Card(cardDTO.numeroCartao(), cardDTO.senha(), SALDO_INICIAL));

        var response = mvc
                .perform(post("/cartoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cardDTOJson.write(cardDTO).getJson()))
                .andReturn().getResponse();

        var cardDTOResponse = new CardDTOResponse(
                "1234",
                "12345"
        		);
        var jsonEsperado = cardDTOResponseJson.write(cardDTOResponse).getJson();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.getContentAsString()).isEqualTo(jsonEsperado);
	}

	@Test
	@DisplayName("Deveria devolver codigo http 200 quando informacoes estao validas")
	void testConsultBalance() throws Exception {
		when(service.findCard(any())).thenReturn(new Card("12345", "1234", new BigDecimal("100.00")));
		
		var response = mvc
                .perform(get("/cartoes/numeroCartao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(numeroCartaoJson.write("").getJson()))
                .andReturn().getResponse();
		
        var jsonEsperado = saldoJson.write(new BigDecimal("100.00")).getJson();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(jsonEsperado);
	}

	@Test
	@DisplayName("Deveria devolver codigo http 404 quando cartao já existente")
	void testConsultBalance2() throws Exception {
		when(service.findCard(any())).thenReturn(null);
		
		var response = mvc
                .perform(get("/cartoes/numeroCartao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(numeroCartaoJson.write("").getJson()))
                .andReturn().getResponse();
		
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
	}

	@Test
	@DisplayName("Deveria devolver codigo http 201 quando informacoes estao validas")
	void testTransaction() throws Exception {
		var transactionDTO = new TransactionDTO(
                "12345",
                "1234",
                new BigDecimal("100.00"));
		
		when(service.findCard(any())).thenReturn(new Card("12345", "1234", new BigDecimal("200.00")));

		when(service.executeTransaction(any())).thenReturn(new Card(transactionDTO.numeroCartao(), transactionDTO.senha(), new BigDecimal("300.00")));

        var response = mvc
                .perform(post("/cartoes/transacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(transactionDTOJson.write(transactionDTO).getJson()))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.getContentAsString()).isEqualTo("OK");
	}

	@Test
	@DisplayName("Deveria devolver codigo http 422 quando cartão inexistente")
	void testTransaction2() throws Exception {
		var transactionDTO = new TransactionDTO(
                "12345",
                "1234",
                new BigDecimal("100.00"));
		
		when(service.findCard(any())).thenReturn(new Card("12345", "1234", new BigDecimal("200.00")));
		
		when(service.executeTransaction(any())).thenThrow(new TransactionCardNotFoundException());

        var response = mvc
                .perform(post("/cartoes/transacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(transactionDTOJson.write(transactionDTO).getJson()))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
        assertThat(response.getContentAsString()).isEqualTo("CARTAO_INEXISTENTE");
	}

	@Test
	@DisplayName("Deveria devolver codigo http 422 quando cartão inexistente")
	void testTransaction3() throws Exception {
		var transactionDTO = new TransactionDTO(
                "12345",
                "1234",
                new BigDecimal("100.00"));
		
		when(service.findCard(any())).thenReturn(new Card("12345", "1234", new BigDecimal("200.00")));
		
		when(service.executeTransaction(any())).thenThrow(new InvalidPasswordException());

        var response = mvc
                .perform(post("/cartoes/transacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(transactionDTOJson.write(transactionDTO).getJson()))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
        assertThat(response.getContentAsString()).isEqualTo("SENHA_INVALIDA");
	}

	@Test
	@DisplayName("Deveria devolver codigo http 422 quando saldo insuficiente")
	void testTransaction4() throws Exception {
		var transactionDTO = new TransactionDTO(
                "12345",
                "1234",
                new BigDecimal("100.00"));
		
		when(service.findCard(any())).thenReturn(new Card("12345", "1234", new BigDecimal("200.00")));
		
		when(service.executeTransaction(any())).thenThrow(new ConstraintViolationException(null));

        var response = mvc
                .perform(post("/cartoes/transacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(transactionDTOJson.write(transactionDTO).getJson()))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
        assertThat(response.getContentAsString()).isEqualTo("SALDO_INSUFICIENTE");
	}

}

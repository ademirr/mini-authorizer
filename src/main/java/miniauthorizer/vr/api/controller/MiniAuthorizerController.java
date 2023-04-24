package miniauthorizer.vr.api.controller;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import miniauthorizer.vr.api.domain.Card;
import miniauthorizer.vr.api.domain.CardDTO;
import miniauthorizer.vr.api.domain.CardRepository;
import miniauthorizer.vr.api.domain.DetalhesCartaoDTO;
import miniauthorizer.vr.api.domain.TransactionDTO;
import miniauthorizer.vr.api.service.MiniAuthorizerService;

@RestController
@RequestMapping("cartoes")
public class MiniAuthorizerController {
	
	@Autowired
	private CardRepository repository;
	
	@Autowired
	private MiniAuthorizerService service;
	
	DetalhesCartaoDTO detalhesDTO;
	
	@PostMapping
	public ResponseEntity<DetalhesCartaoDTO> createCard(@RequestBody CardDTO cardDTO, UriComponentsBuilder uriBuilder) {
		
		Card cartao = new Card(cardDTO.numeroCartao(), cardDTO.senha(), new BigDecimal(500.00));
		
		try {
			repository.save(cartao);
			var uri = uriBuilder.path("/cartoes/{numeroCartao}").buildAndExpand(cardDTO.numeroCartao()).toUri();
			detalhesDTO = new DetalhesCartaoDTO(cartao.getNumeroCartao(), cartao.getSaldo(), "Cartão cadastrado com sucesso!");
			return ResponseEntity.created(uri).body(detalhesDTO);
		} catch (DataIntegrityViolationException e) {
			detalhesDTO = new DetalhesCartaoDTO(cartao.getNumeroCartao(), cartao.getSaldo(), "Esse cartão já estava cadastrado em nosso sistema!");
			return ResponseEntity.unprocessableEntity().body(detalhesDTO);
		}
	}
	
	@GetMapping("/{numeroCartao}")
	public ResponseEntity<BigDecimal> consultarSaldo(@PathVariable String numeroCartao) {
		Card cartao = repository.findByNumeroCartao(numeroCartao);
		if (cartao == null) {
			System.out.println("Cartão não cadastrado!");
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(cartao.getSaldo());
	}
	
	@PostMapping(value = "/transacoes")
	@Transactional
	public ResponseEntity<String> transaction(@RequestBody TransactionDTO transactionDTO, UriComponentsBuilder uriBuilder) {
		Card card = service.executeTransaction(transactionDTO);
		return ResponseEntity.created(uriBuilder.path("/cartoes/{numeroCartao}").buildAndExpand(card.getNumeroCartao()).toUri()).body("OK");
	}
}

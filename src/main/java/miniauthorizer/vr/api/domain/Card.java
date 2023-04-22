package miniauthorizer.vr.api.domain;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Table(name = "Card")
@Entity(name = "Card")
public class Card {

	public Card() {
		
	}

	public Card(String numeroCartao, String senha, BigDecimal saldo) {
		super();
		this.numeroCartao = numeroCartao;
		this.senha = senha;
		this.saldo = saldo;
	}
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	private String numeroCartao;
	private String senha;
	private BigDecimal saldo;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNumeroCartao() {
		return numeroCartao;
	}

	public void setNumeroCartao(String numeroCartao) {
		this.numeroCartao = numeroCartao;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public BigDecimal getSaldo() {
		return saldo;
	}

	public void setSaldo(BigDecimal saldo) {
		this.saldo = saldo;
	}

}

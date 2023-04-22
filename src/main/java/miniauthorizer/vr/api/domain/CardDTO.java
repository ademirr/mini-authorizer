package miniauthorizer.vr.api.domain;

import java.math.BigDecimal;

public record CardDTO(String numeroCartao, String senha, BigDecimal saldo, String mensagem) {

}

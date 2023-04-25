package miniauthorizer.vr.api.domain;

import jakarta.validation.constraints.NotBlank;

public record CardDTO(@NotBlank String numeroCartao, @NotBlank String senha) {

}

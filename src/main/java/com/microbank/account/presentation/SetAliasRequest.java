package com.microbank.account.presentation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class SetAliasRequest {

    @NotBlank(message = "El alias no puede estar vacío")
    @Size(min = 3, max = 30, message = "El alias debe tener entre 3 y 30 caracteres")
    @Pattern(regexp = "^[a-z0-9\\-]+$", message = "El alias solo puede contener letras minúsculas, números y guiones")
    private String alias;

    public SetAliasRequest() {}

    public String getAlias() { return alias; }
    public void setAlias(String alias) { this.alias = alias; }
}

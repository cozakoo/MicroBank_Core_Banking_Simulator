package com.microbank.account.presentation;

import com.microbank.account.domain.AccountStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(
    name = "UpdateAccountStatusRequest",
    description = "Solicitud para cambiar el estado de una cuenta (activar/desactivar)",
    example = "{\"newStatus\":\"INACTIVO\"}"
)
public class UpdateAccountStatusRequest {

    @NotNull(message = "El estado de la cuenta es obligatorio")
    @Schema(description = "Nuevo estado de la cuenta (ACTIVO o INACTIVO)", example = "INACTIVO")
    private AccountStatus newStatus;

    public UpdateAccountStatusRequest() {
    }

    public UpdateAccountStatusRequest(AccountStatus newStatus) {
        this.newStatus = newStatus;
    }

    public AccountStatus getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(AccountStatus newStatus) {
        this.newStatus = newStatus;
    }
}

package com.microbank.account.presentation;

import com.microbank.account.domain.AccountStatus;
import jakarta.validation.constraints.NotNull;

public class UpdateAccountStatusRequest {

    @NotNull(message = "El estado de la cuenta es obligatorio")
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

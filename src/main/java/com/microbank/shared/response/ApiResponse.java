package com.microbank.shared.response;

import java.time.LocalDateTime;

/**
 * WRAPPER GENÉRICO DE RESPUESTAS API (Martín)
 *
 * PATRÓN: Builder + Generic<T>
 * Por qué:
 * - Consistencia: TODAS las respuestas tienen la misma estructura (success, data, error, timestamp)
 * - Type-safe: ApiResponse<AccountResponse> sabe qué tipo contiene
 * - Sin try-catch en frontend: El cliente ve siempre {success: true/false, data: {...}, error: "..."}
 * - Auditable: timestamp en CADA respuesta (útil para logs distribuidos)
 *
 * Uso:
 *   - Éxito: ApiResponse.success(newAccount)
 *   - Error: ApiResponse.error("Descripción del problema")
 */
public class ApiResponse<T> {
    private final boolean success;
    private final T data;
    private final String error;
    private final LocalDateTime timestamp;

    // Constructor privado para el Builder
    private ApiResponse(Builder<T> builder) {
        this.success = builder.success;
        this.data = builder.data;
        this.error = builder.error;
        this.timestamp = LocalDateTime.now();
    }

    public static <T> ApiResponse<T> success(T data) {
        return new Builder<T>().success(true).data(data).build();
    }

    public static <T> ApiResponse<?> error(String message) {
        return new Builder<>().success(false).error(message).build();
    }

    // Getters
    public boolean isSuccess() { return success; }
    public T getData() { return data; }
    public String getError() { return error; }
    public LocalDateTime getTimestamp() { return timestamp; }
    
    public static class Builder<T> {
        private boolean success;
        private T data;
        private String error;

        public Builder<T> success(boolean success) {
            this.success = success;
            return this;
        }

        public Builder<T> data(T data) {
            this.data = data;
            return this;
        }

        public Builder<T> error(String error) {
            this.error = error;
            return this;
        }

        public ApiResponse<T> build() {
            return new ApiResponse<>(this);
        }
    }
}
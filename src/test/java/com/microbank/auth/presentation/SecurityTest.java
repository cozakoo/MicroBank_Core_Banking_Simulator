package com.microbank.auth.presentation;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class SecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void whenUnauthenticated_thenAccountsIsForbidden() throws Exception {
        // Sin JWT, los endpoints de cuentas deben requerir autenticación
        mockMvc.perform(get("/api/v1/accounts"))
                .andExpect(status().isForbidden());
    }

    @Test
    void authEndpoints_shouldNotRequireAuthentication() throws Exception {
        // Los endpoints de auth son públicos — no devuelven 401 ni 403
        int status = mockMvc.perform(get("/api/v1/auth/login"))
                .andReturn().getResponse().getStatus();
        // Puede ser 405 (GET en endpoint POST) o 500, pero NO 401/403
        assert status != 401 && status != 403 : "El endpoint de auth no debe requerir autenticación";
    }

    @Test
    @WithMockUser(roles = "USER")
    void whenUserRole_thenAdminAuditIsForbidden() throws Exception {
        // Un usuario común NO puede ver la auditoría de admin
        mockMvc.perform(get("/api/v1/admin/audit"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void whenAdminRole_thenAdminAuditIsAllowed() throws Exception {
        // Un ADMIN sí puede acceder a la auditoría
        mockMvc.perform(get("/api/v1/admin/audit"))
                .andExpect(status().isOk());
    }

    @Test
    void publicEndpoints_shouldBeAccessible() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk());
    }
}

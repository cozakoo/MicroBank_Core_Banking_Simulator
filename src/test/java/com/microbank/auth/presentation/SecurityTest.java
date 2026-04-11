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
        // Un usuario sin loguear no debería poder ver las cuentas
        mockMvc.perform(get("/api/v1/accounts"))
                .andExpect(status().isUnauthorized());
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
        // Un ADMIN sí debería poder entrar a la auditoría
        mockMvc.perform(get("/api/v1/admin/audit"))
                .andExpect(status().isOk());
    }

    @Test
    void publicEndpoints_shouldBeAccessible() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk());


    }
}
package com.nakamas.hatfieldbackend;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc()
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY, connection = EmbeddedDatabaseConnection.H2)
@ActiveProfiles("test")
public class AccessControlTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void adminRoleShouldHaveAccessToShops() throws Exception {
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.options("/api/shop/admin/all"));
        resultActions.andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = "CLIENT")
    public void clientRoleShouldNotHaveAccessToShops() throws Exception {
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.options("/api/shop/admin/all"));
        resultActions.andExpect(status().isForbidden());
    }
}
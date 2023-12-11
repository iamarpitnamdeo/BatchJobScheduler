package com.example.userBatchService.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.userBatchService.model.BatchUser;
import org.mockito.ArgumentMatcher;

import com.example.userBatchService.service.UserService;

import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @MockBean
    private Logger logger;

    @Test
    public void testAddUsers() throws Exception {
        // Mock the userService.addUser() method to avoid actually saving users
        Mockito.doNothing().when(userService).addUser(Mockito.any(BatchUser.class));

        // Perform the POST request
        mockMvc.perform(post("/user")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Users added successfully"));
        
        // Verify userService.addUser() was called 1000 times
        Mockito.verify(userService, times(1000)).addUser(Mockito.any(BatchUser.class));
    }

    @Test
    public void testDeleteUsers() throws Exception {
        // Mock the userService.deleteUsersInBatch() method to avoid actual deletion
        Mockito.doNothing().when(userService).deleteUsersInBatch(10);

        // Perform the DELETE request
        mockMvc.perform(delete("/user/delete")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Users deleted successfully"));

        // Verify userService.deleteUsersInBatch() was called once
        Mockito.verify(userService, times(1)).deleteUsersInBatch(10);
    }
}

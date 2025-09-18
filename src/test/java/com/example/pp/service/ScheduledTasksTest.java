package com.example.pp.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScheduledTasksTest {
    @Mock
    private ClientService userService;
    @InjectMocks
    private ScheduledTasks scheduledTasks;

    @Test
    void TestProcessClients() {
        when(userService.getAllClients()).thenReturn(List.of());

        scheduledTasks.processClients();

        verify(userService, times(1)).getAllClients();
    }

}
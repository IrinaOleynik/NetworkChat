package org.example;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.io.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class ServerTest {

    @Mock
    private ClientHandler mockClientHandler1;
    @Mock
    private ClientHandler mockClientHandler2;
    private Server server;

    @BeforeEach
    public void setUp() {
        server = spy(new Server());
        server.addNewConnection(mockClientHandler1);
        server.addNewConnection(mockClientHandler2);

    }

    @AfterEach
    public void tearDown() throws IOException {
        server.stopServer();
        Server.connectionList.remove(mockClientHandler1);
        Server.connectionList.remove(mockClientHandler2);
    }

    @Test
    public void testConnectionListSizeAfterConnection() {
        assertEquals(2, server.connectionList.size());
    }

    @Test
    public void stopServer() throws IOException {
        doNothing().when(mockClientHandler1).stopHandler();
        doNothing().when(mockClientHandler2).stopHandler();

        server.stopServer();

        assertFalse(server.isRunning);
        verify(mockClientHandler1, times(1)).stopHandler();
        verify(mockClientHandler2, times(1)).stopHandler();
    }
}
package org.example;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import org.mockito.*;
import java.io.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;

class ClientHandlerTest {
    @Mock
    private Socket mockClient;
    @Mock
    private PrintWriter mockWriter;
    @Mock
    private PrintWriter mockErrorWriter;
    @Mock
    private BufferedReader mockReader;

    @Test
    void send() throws IOException {
        MockitoAnnotations.openMocks(this);

        when(mockClient.getOutputStream()).thenReturn(Mockito.mock(OutputStream.class));
        when(mockClient.getInputStream()).thenReturn(Mockito.mock(InputStream.class));

        ClientHandler clientHandler1 = new ClientHandler(mockClient);
        clientHandler1.writer = mockErrorWriter;
        ClientHandler clientHandler2 = new ClientHandler(mockClient);
        clientHandler2.writer = mockWriter;

        Server.connectionList.add(clientHandler1);
        Server.connectionList.add(clientHandler2);

        clientHandler1.send("Test message");

        verify(clientHandler2.writer).println("Test message");
        verify(clientHandler1.writer, times(0)).println(anyString());
    }

    @Test
    void stopHandler() throws IOException {
        MockitoAnnotations.openMocks(this);
        when(mockClient.getOutputStream()).thenReturn(Mockito.mock(OutputStream.class));
        when(mockClient.getInputStream()).thenReturn(Mockito.mock(InputStream.class));

        ClientHandler clientHandler = new ClientHandler(mockClient);
        clientHandler.writer = mockWriter;
        clientHandler.reader = mockReader;
        Server.connectionList.add(clientHandler);

        clientHandler.stopHandler();

        verify(clientHandler.reader, times(1)).close();
        verify(clientHandler.writer, times(1)).close();
        verify(mockClient, times(1)).close();
        Assertions.assertEquals(0, Server.connectionList.size());
    }
}
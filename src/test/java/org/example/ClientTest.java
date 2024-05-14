package org.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class ClientTest {
    @Mock
    Socket mockClient;
    @Mock
    BufferedReader mockReader;
    @Mock
    PrintWriter mockWriter;
    @Mock
    Scanner mockScanner;

    @Test
    void exit() throws IOException {
        when(mockClient.getInputStream()).thenReturn(Mockito.mock(InputStream.class));
        when(mockClient.getOutputStream()).thenReturn(Mockito.mock(OutputStream.class));

        Client clientSpy = spy(new Client(mockClient));

        clientSpy.reader = mockReader;
        clientSpy.writer = mockWriter;
        clientSpy.scanner = mockScanner;

        clientSpy.exit();

        verify(mockWriter).close();
        verify(mockReader).close();
        verify(mockClient).close();
        verify(mockScanner).close();
    }

    @Test
    void setNickNameName() throws IOException {
        when(mockClient.getInputStream()).thenReturn(Mockito.mock(InputStream.class));
        when(mockClient.getOutputStream()).thenReturn(Mockito.mock(OutputStream.class));

        Client clientSpy = spy(new Client(mockClient));

        clientSpy.reader = mockReader;
        clientSpy.writer = mockWriter;
        clientSpy.scanner = mockScanner;

        when(mockReader.readLine()).thenReturn("Введите имя", "TestUser");
        when(mockScanner.nextLine()).thenReturn("TestUser");
        clientSpy.setNickNameName();

        verify(mockReader, times(2)).readLine();
        verify(mockWriter).println("TestUser");
        verify(mockScanner).nextLine();
    }
}
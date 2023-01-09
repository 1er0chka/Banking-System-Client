package com.app.client;

import com.app.DTO.AccountDTO;
import com.app.DTO.CardDTO;
import com.app.DTO.OperationDTO;
import com.app.DTO.UserDTO;
import com.app.cryptography.RSA;
import com.app.mapper.JSON;
import com.app.model.Request;
import java.io.*;
import java.net.Socket;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

public class ClientSocket {
    private static ClientSocket instance;
    private static Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private UserDTO user;
    private PublicKey publicKey;
    private PrivateKey privateKey;

    private ClientSocket() {
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    // Получение полных данных пользователя
    public void refreshUser() throws IOException {
        user = (new JSON<UserDTO>()).fromJson(get().getRequestMessage(), UserDTO.class);
        send(new Request());
        user.setAccounts((new JSON<AccountDTO>()).fromJsonArray(get().getRequestMessage(), AccountDTO.class));
        send(new Request());
        for (AccountDTO account : user.getAccounts()) {
            account.setCards((new JSON<CardDTO>()).fromJsonArray(get().getRequestMessage(), CardDTO.class));
            send(new Request());
            for (CardDTO card : account.getCards()) {
                card.setOperations((new JSON<OperationDTO>()).fromJsonArray(get().getRequestMessage(), OperationDTO.class));
                send(new Request());
            }
        }
    }

    // Соединение с сервером
    public boolean connect() throws InterruptedException, IOException, ClassNotFoundException {
        boolean isConnect = false;
        int trying = 0;
        while (!isConnect && trying < 5) {
            try {
                socket = new Socket("localhost", 1234);
                in = new DataInputStream(socket.getInputStream());
                out = new DataOutputStream(socket.getOutputStream());
                isConnect = true;
            } catch (IOException e) {
                trying++;
                Thread.sleep(1000);
            }
        }
        if (isConnect) {
            createKeys();
        }
        return isConnect;
    }

    // Генерация ключей
    private void createKeys() throws IOException, ClassNotFoundException {
        KeyPair kp = RSA.getKeys();
        privateKey = kp.getPrivate(); // ключ для расшифровки сообщений
        publicKey = (PublicKey) (new ObjectInputStream(in)).readObject();
        (new ObjectOutputStream(out)).writeObject(kp.getPublic()); // отправляем клиенту ключ, которым он будет зашифровывать сообщения

    }

    // Отключение от сервера
    public void close() throws IOException {
        send(new Request(Request.RequestType.Exit, ""));
        socket.close();
    }

    // Отправка данных
    public void send(Request request) throws IOException {
        String data = (new JSON<Request>().toJson(request, Request.class));
        out.writeUTF(RSA.encoding(data, publicKey));
    }

    // Получение данных
    public Request get() throws IOException {
        String data = in.readUTF();
        return (new JSON<Request>()).fromJson(RSA.decoding(data, privateKey), Request.class);
    }

    public static ClientSocket getInstance() {
        if (instance == null) {
            instance = new ClientSocket();
        }
        return instance;
    }
}

package com.example;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class MyThread extends Thread {

    Socket s;

    MyThread(Socket s) {
        this.s = s;
    }

    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            DataOutputStream out = new DataOutputStream(s.getOutputStream());

            String[] request = in.readLine().split(" ");
            String header;
            String method = request[0];
            String resourse = request[1];
            String version = request[2];
            String resopnseBody;

            do {
                header = in.readLine();
                System.out.println(header);

            } while (!header.isEmpty());
            System.out.println("Richiesta terminata");

            File file = new File("htdocs/index.html");
            InputStream input = new FileInputStream(file);
            byte[] buf = new byte[3451];
            int n;
            while ((n = input.read(buf) != -1)) {
                out.write(buf, 0, n);
            }
            input.close();


            switch (resourse) {
                case "/":
                    out.writeBytes("HTTP/1.1 200 ok\n");
                    out.writeBytes("Content-Type: text/HTML\n");
                    out.writeBytes("Content-Length: " + file.length() + " \n");
                    out.writeBytes("\r\n");
                    break;

                case "/index.html":
                    out.writeBytes("HTTP/1.1 200 ok\n");
                    out.writeBytes("Content-Type: text/HTML\n");
                    out.writeBytes("Content-Length: " + file.length() + " \n");
                    out.writeBytes("\r\n");
                    break;

                case "/file.txt":
                    out.writeBytes("HTTP/1.1 200 ok\n");
                    out.writeBytes("Content-Type: text/plain\n");
                    out.writeBytes("Content-Length: " + file.length() + " \n");
                    out.writeBytes("\r\n");
                    break;

                default:
                    out.writeBytes("HTTP/1.1 404 not found\n");
                    out.writeBytes("Content-Length: " + file.length() + " \n");
                    out.writeBytes("\r\n");
                    break;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

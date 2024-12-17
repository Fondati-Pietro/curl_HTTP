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

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            DataOutputStream out = new DataOutputStream(s.getOutputStream());

            String[] request = in.readLine().split(" ");
            String header;
            String method = request[0];
            String resource = request[1];
            String version = request[2];
            String error = "";

            do {
                header = in.readLine();
                System.out.println(header);

            } while (!header.isEmpty()); // se c'è la riga vuota è finito HTTP

            if (resource.equals("/")) {
                resource = "/index.html";
            }
            if (resource.endsWith("/")) {
                resource = resource + "index.html";
            }

            File file = new File("htdocs" + resource);
            if (file.isDirectory()) {
                out.writeBytes("HTTP/1.1 301 Moved Permanently");
                out.writeBytes("Content-Length: 0\n");
                out.writeBytes("Location: " + resource + "/\n");
                out.writeBytes("\n");
            } else if (file.exists()) {
                out.writeBytes("HTTP/1.1 200 ok\n");
                out.writeBytes("Content-Type: " + file.length() + "\n");
                out.writeBytes("Content-Length: " + getContentType(file) + " \n");
                out.writeBytes("\n");

                InputStream input = new FileInputStream(file);
                byte[] buf = new byte[8192];
                int n;
                while (((n = input.read(buf)) != -1)) {
                    out.write(buf, 0, n);
                }
                input.close();
            } else {
                error = "<h1> Not Foud Error 404</h1>";
                out.writeBytes("HTTP/1.1 404 not found\n");
                out.writeBytes("Content-Length: " + error.length() + " \n");
                out.writeBytes("Content-Type: type/plain\n");
                out.writeBytes("\r\n");

            }
            s.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static String getContentType(File f) {
        String[] s = f.getName().split("\\.");
        String ext = s[s.length - 1];
        switch (ext) {
            case "html":
                return "text/html";
            case "txt":
                return "text/txt";
            case "png":
                return "image/png";
            case "css":
                return "text/css";
            default:
                return "";
        }
    }
}

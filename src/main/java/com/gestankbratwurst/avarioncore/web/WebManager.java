package com.gestankbratwurst.avarioncore.web;

import com.gestankbratwurst.avarioncore.AvarionCore;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import org.bukkit.Bukkit;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of AvarionCore and was created at the 04.07.2020
 *
 * AvarionCore can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
public class WebManager {

  private static final int PORT = 11017;

  public WebManager(final AvarionCore avarionCore) {
    this.avarionCore = avarionCore;
    CompletableFuture.runAsync(this::listenForRestart);
  }

  private final AvarionCore avarionCore;
  private final String responseOk = "HTTP/1.1 204 OK\r\n\r\n";
  private final String responseDeny = "HTTP/1.1 204 Wrong_Password\r\n\r\n";
  private final String password = "043tgnnga9e8hz6imujhhnuhnady94wzhnrzfh09m4w5hsnrnh89er";

  private void listenForRestart() {
    try {
      final ServerSocket serverSocket = new ServerSocket(PORT);
      while (true) {

        final Socket socket = serverSocket.accept();

        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        final OutputStream outputStream = socket.getOutputStream();
        final String line = bufferedReader.readLine();
        final String[] args = line.split(" ");

        if (args[0].equals("POST") && args[1].equals("/" + this.password)) {
          System.out.println("§aHTTP shutdown initialised.");
          Bukkit.getScheduler().runTask(this.avarionCore, Bukkit::shutdown);
          Bukkit.getServer().shutdown();
          outputStream.write(this.responseOk.getBytes(StandardCharsets.UTF_8));
          break;
        }

        socket.getOutputStream().write(this.responseDeny.getBytes(StandardCharsets.UTF_8));
        socket.close();
      }
    } catch (final IOException e) {
      e.printStackTrace();
    }
  }

}

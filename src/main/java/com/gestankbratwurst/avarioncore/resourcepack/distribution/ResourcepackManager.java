package com.gestankbratwurst.avarioncore.resourcepack.distribution;

import com.gestankbratwurst.avarioncore.AvarionCore;
import com.gestankbratwurst.avarioncore.resourcepack.distribution.ResourcepackServer.ResourceServerConnection;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class ResourcepackManager {

  public static final long SERVER_TIMESTAMP = System.currentTimeMillis();
  public static final String RESOURCEPACK_FILE_NAME = "serverpack.zip";

  // "176.9.38.108"
  private final int port = 9555;
  private final String host = "localhost";
  private final String hash;

  private ResourcepackServer server;
  private final File pack;

  public ResourcepackManager(final AvarionCore plugin) {
    File stampFolder = new File(plugin.getDataFolder() + File.separator + SERVER_TIMESTAMP);
    pack = new File(stampFolder, RESOURCEPACK_FILE_NAME);
    hash = getFileHashChecksum(pack, plugin);
    CompletableFuture.runAsync(() -> this.startServer(plugin));
  }

  public String getResourceHash() {
    return hash;
  }

  public String getDownloadURL() {
    return "http://" + host + ":" + port + "/" + SERVER_TIMESTAMP + "/" + RESOURCEPACK_FILE_NAME;
  }

  public void shutdown() {
    server.terminate();
  }


  private void startServer(AvarionCore plugin) {
    plugin.getLogger().info("Starting async HTTP Server");
    try {
      server = new ResourcepackServer(port) {

        @Override
        public File requestFileCallback(final ResourceServerConnection connection,
            final String request) {
          final Player player = getAddress(connection);

          if (player == null) {
            // Connection from unknown IP, refuse connection.
            return null;
          }
          plugin.getLogger().info(
              "Connection " + connection.getClient().getInetAddress() + " is requesting + "
                  + request);
          // Return the .zip file
          return pack;
        }

        @Override
        public void onSuccessfulRequest(final ResourceServerConnection connection,
            final String request) {
          plugin.getLogger().info(
              "Successfully served " + request + " to " + connection.getClient().getInetAddress()
                  .getHostAddress());
        }

        @Override
        public void onClientRequest(final ResourceServerConnection connection,
            final String request) {
          plugin.getLogger()
              .info(connection.getClient().getInetAddress() + " is requesting the resourcepack");
        }

        @Override
        public void onRequestError(final ResourceServerConnection connection, final int code) {
          plugin.getLogger().info(
              "Error " + code + " while attempting to serve " + connection.getClient()
                  .getInetAddress().getHostAddress());
        }
      };

      server.start();
      plugin.getLogger().info("Successfully started the HTTP Server");
    } catch (final IOException ex) {
      plugin.getLogger().severe("Failed to start HTTP ResourceServer!");
      ex.printStackTrace();
    }
  }

  private String getFileHashChecksum(final File input, JavaPlugin plugin) {
    try {
      return Files.hash(input, Hashing.sha1()).toString();
    } catch (final IOException e) {
      plugin.getLogger().severe("Failed to calculate resourcepack hashcode - " + e.getMessage());
      return null;
    }
  }


  private Player getAddress(final ResourceServerConnection connection) {
    final byte[] ip = connection.getClient().getInetAddress().getAddress();

    for (final Player player : Bukkit.getOnlinePlayers()) {
      if (Arrays.equals(player.getAddress().getAddress().getAddress(), ip)) {
        return player;
      }
    }
    return null;
  }


}

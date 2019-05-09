package com.zeroturnaround.cloudmonitor;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import hudson.Extension;
import hudson.model.Computer;
import hudson.model.TaskListener;
import hudson.slaves.ComputerListener;
import hudson.slaves.OfflineCause;

@Extension
public class CloudNodeListener extends ComputerListener {

  private static final Logger log = Logger.getLogger(CloudNodeListener.class.getName());

  @Override
  public void onOnline(Computer c, TaskListener listener) {
    try {
      logStatus(c, "online");
    }
    catch (Exception e) {
      log.log(Level.SEVERE, "logging node status" + c.getName(), e);
    }
  }

  @Override
  public void onOffline(Computer c, OfflineCause cause) {
    try {
      logStatus(c, "offline");
    }
    catch (IOException e) {
      log.log(Level.SEVERE, "logging node status" + c.getName(), e);
    }
  }

  private void logStatus(Computer c, String status) throws IOException {
    Path logFile = StorageConfig.getLogFile("node-status.log");
    if (logFile == null)
      return;

    String name = c.getName().trim();
    if (name.isEmpty())
      name = c.getClass().getSimpleName();

    String timestamp = String.valueOf(System.currentTimeMillis());
    byte[] logLine = (encodeStatus(name, timestamp, status) + "\n").getBytes(UTF_8);
    synchronized (this) {
      Files.write(logFile, logLine, CREATE, APPEND);
    }
  }

  private String encodeStatus(String name, String timestamp, String status) {
    Map<String, String> values = new HashMap<>();
    values.put("name", name);
    values.put("timestamp", timestamp);
    values.put("status", status);
    return Helper.urlEncode(values);
  }
}

package com.zeroturnaround.cloudmonitor;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
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
      logStatus(c.getName(), "online");
    }
    catch (Exception e) {
      log.log(Level.SEVERE, "logging node status" + c.getName(), e);
    }
  }

  @Override
  public void onOffline(Computer c, OfflineCause cause) {
    try {
      logStatus(c.getName(), "offline");
    }
    catch (IOException e) {
      log.log(Level.SEVERE, "logging node status" + c.getName(), e);
    }
  }

  private void logStatus(String name, String status) throws IOException {
    Path logFile = StorageConfig.getLogFile("node-status.log");
    if (logFile == null)
      return;

    String timestamp = String.valueOf(System.currentTimeMillis());
    byte[] logLine = (String.join(",", Arrays.asList(name, status, timestamp)) + "\n").getBytes(UTF_8);
    synchronized (this) {
      Files.write(logFile, logLine, CREATE, APPEND);
    }
  }
}

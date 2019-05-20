package com.zeroturnaround.cloudmonitor;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;

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
    logStatus(c, "online");
  }

  @Override
  public void onOffline(Computer c, OfflineCause cause) {
    logStatus(c, "offline");
  }

  private void logStatus(Computer c, String status) {
    String name = c.getName().trim();
    if (name.isEmpty())
      name = c.getClass().getSimpleName();

    try {
      Path logFile = StorageConfig.getLogFile("node-status.log");
      if (logFile == null)
        return;

      Map<String, String> values = new HashMap<>();
      values.put("name", name);
      values.put("timestamp", String.valueOf(System.currentTimeMillis()));
      values.put("status", status);
      byte[] logLine = (Helper.urlEncode(values) + "\n").getBytes(UTF_8);
      synchronized (this) {
        Files.write(logFile, logLine, CREATE, APPEND);
      }
    }
    catch (Exception e) {
      log.log(Level.SEVERE, "logging node status: " + name, e);
    }
  }

}

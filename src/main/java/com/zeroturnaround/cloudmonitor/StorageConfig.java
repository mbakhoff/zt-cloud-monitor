package com.zeroturnaround.cloudmonitor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

import org.kohsuke.stapler.StaplerRequest;

import hudson.Extension;
import jenkins.model.GlobalConfiguration;
import net.sf.json.JSONObject;

@Extension
public class StorageConfig extends GlobalConfiguration {

  private static final Logger log = Logger.getLogger(StorageConfig.class.getName());

  private String logDirectory;

  public StorageConfig() {
    load();
  }

  public String getLogDirectory() {
    return logDirectory;
  }

  public void setLogDirectory(String logDirectory) {
    this.logDirectory = logDirectory;
  }

  @Override
  public boolean configure(StaplerRequest req, JSONObject json) {
    req.bindJSON(this, json);
    save();
    return true;
  }

  public static Path getLogFile(String name) throws IOException {
    StorageConfig config = GlobalConfiguration.all().get(StorageConfig.class);
    if (config == null) {
      log.warning("plugin not configured");
      return null;
    }
    String logDir = config.getLogDirectory();
    if (logDir == null || logDir.isEmpty()) {
      log.warning("log directory is not set");
      return null;
    }
    Path dir = Paths.get(logDir);
    Files.createDirectories(dir);
    return dir.resolve(name);
  }
}

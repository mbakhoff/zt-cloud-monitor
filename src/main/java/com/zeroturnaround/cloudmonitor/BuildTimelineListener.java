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
import hudson.model.Result;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.model.listeners.RunListener;

@Extension
public class BuildTimelineListener extends RunListener<Run<?, ?>> {

  private static final Logger log = Logger.getLogger(BuildTimelineListener.class.getName());

  @Override
  public void onCompleted(Run run, TaskListener listener) {
    try {
      logBuildInfo(run);
    }
    catch (Exception e) {
      log.log(Level.SEVERE, "logging run completion " + run, e);
    }
  }

  private void logBuildInfo(Run run) throws IOException {
    Path logFile = StorageConfig.getLogFile("build-timeline.log");
    if (logFile == null)
      return;

    String node = getNode();
    String start = String.valueOf(run.getStartTimeInMillis());
    String end = String.valueOf(System.currentTimeMillis());
    String job = run.getParent().getName();
    String build = String.valueOf(run.getNumber());
    String result = getResult(run);

    byte[] logLine = (String.join(",", Arrays.asList(node, start, end, job, build, result)) + "\n").getBytes(UTF_8);
    synchronized (this) {
      Files.write(logFile, logLine, CREATE, APPEND);
    }
  }

  private String getResult(Run run) {
    Result result = run.getResult();
    return result != null ? result.toString() : "unknown";
  }

  private String getNode() {
    Computer computer = Computer.currentComputer();
    if (computer == null)
      throw new IllegalStateException("computer is null");
    String name = computer.getName();
    if (name == null)
      return computer.getClass().getSimpleName();
    name = name.trim();
    if (name.isEmpty())
      return computer.getClass().getSimpleName();
    return name;
  }
}

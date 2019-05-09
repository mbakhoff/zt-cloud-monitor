package com.zeroturnaround.cloudmonitor;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

public class Helper {

  public static String urlEncode(Map<String, String> values) {
    try {
      StringBuilder sb = new StringBuilder();
      for (Map.Entry<String, String> e : values.entrySet()) {
        if (sb.length() > 0)
          sb.append('&');
        sb.append(URLEncoder.encode(e.getKey(), "UTF-8"));
        sb.append('=');
        sb.append(URLEncoder.encode(e.getValue(), "UTF-8"));
      }
      return sb.toString();
    }
    catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }
}

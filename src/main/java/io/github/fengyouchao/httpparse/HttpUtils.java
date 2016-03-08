package io.github.fengyouchao.httpparse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The class <code></code>
 *
 * @author Youchao Feng
 * @version 1.0
 * @date Mar 03,2016 2:25 PM
 */
public class HttpUtils {

  private static Pattern headerCharsetPattern = Pattern.compile("charset=([\\w-]+)");
  private static Pattern htmlCharsetPattern = Pattern
      .compile("(?i)<meta\\s+http-equiv=\"Content-Type\"\\s+content=\"text/html; charset=([\\w-]+)\"\\s+/>");
  private static Pattern htmlCharsetPattern2 = Pattern
      .compile("(?i)<meta\\s+charset=\"([\\w-]+)\"");
  private static Pattern urlPattern =
      Pattern.compile("(https|http)://([\\w\\d\\.-]+)(:\\d+)?(/[\\w/]*)?");

  public static void main(String[] args) {
    Matcher matcher = urlPattern.matcher("http://baidu.com:80/wom/en");
    if (matcher.matches()) {
      for (int i = 0; i < matcher.groupCount(); i++) {
        System.out.println(matcher.group(i));
      }
    } else {
      System.out.println("====");
    }
  }

  public static int findStatus(HttpResponse response) {
    String statusLine = response.getStatusLine();
    return Integer.parseInt(statusLine.split("\\s")[1]);
  }

  public static String findCharset(HttpResponse httpResponse) {
    return findCharset(httpResponse, null);
  }

  public static String findHtmlCharset(String content) {
    Matcher matcher = htmlCharsetPattern.matcher(content);
    if (matcher.find()) {
      return matcher.group(1);
    }else{
      matcher = htmlCharsetPattern2.matcher(content);
      if (matcher.find()){
        return matcher.group(1);
      }
    }
    return null;
  }

  public static String findCharset(HttpMessage httpMessage, String defaultCharset) {
    String value = httpMessage.getContentType();
    if (value == null) {
      return defaultCharset;
    }
    Matcher matcher = headerCharsetPattern.matcher(value);
    if (matcher.find()) {
      return matcher.group(1);
    }
    return defaultCharset;
  }

}

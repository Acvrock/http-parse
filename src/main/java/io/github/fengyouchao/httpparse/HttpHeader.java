package io.github.fengyouchao.httpparse;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * The class <code></code>
 *
 * @author Youchao Feng
 * @version 1.0
 * @date Mar 03,2016 9:28 AM
 */
public class HttpHeader {
  private String name;
  private String value;

  public HttpHeader(String name, String value){
    this.name = name;
    this.value = value;
  }


  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public byte[] getBytes(){
    return (name + ": " + value + "/r/n").getBytes(Charset.forName("iso-8859-1"));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;

    HttpHeader that = (HttpHeader) o;

    return name != null ? name.equalsIgnoreCase(that.name) : that.name == null;

  }

  @Override
  public int hashCode() {
    return name != null ? name.toLowerCase().hashCode() : 0;
  }

  @Override
  public String toString() {
    return "HttpHeader{" +
        "name='" + name + '\'' +
        ", value='" + value + '\'' +
        '}';
  }

  public String originalString(){
    return name + ": " + value + "\r\n";
  }
}

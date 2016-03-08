package io.github.fengyouchao.httpparse;

import java.util.ArrayList;
import java.util.List;

/**
 * The class <code></code>
 *
 * @author Youchao Feng
 * @version 1.0
 * @date Mar 03,2016 3:31 PM
 */
public class ByteCache {
  private List<Byte> cache = new ArrayList<>();

  public void put(byte b) {
    cache.add(b);
  }

  public boolean hasCRLF() {
    return cache.size() >= 2 && cache.get(cache.size() - 1) == '\n'
        && cache.get(cache.size() - 2) == '\r';
  }

  public byte[] getBytes() {
    byte[] bytes = new byte[cache.size()];
    for (int i = 0; i < bytes.length; i++) {
      bytes[i] = cache.get(i);
    }
    return bytes;
  }

  public String getLineWithoutCRLF() {
    return new String(getBytes(), 0, cache.size() - 2);
  }

  public void clear() {
    cache.clear();
  }

}

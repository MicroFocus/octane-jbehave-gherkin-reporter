/*
 * MIT License
 *
 * Copyright (c) 2020 Micro Focus
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.microfocus.adm.almoctane.jbehave;


import java.io.*;
import java.net.URL;

class ResultFileOutputStream extends OutputStream {

  private final FileOutputStream fileOutputStream;

  public ResultFileOutputStream(URL url) throws Exception {
    File file = new File(url.getFile());
    ensureParentDirExists(file);
    fileOutputStream = new FileOutputStream(file);
  }

  @Override
  public void write(int b) throws IOException {
    fileOutputStream.write(b);
  }

  @Override
  public void write(byte[] b) throws IOException {
    fileOutputStream.write(b);
  }

  @Override
  public void write(byte[] b, int off, int len) throws IOException {
    fileOutputStream.write(b, off, len);
  }

  @Override
  public void flush() throws IOException {
    fileOutputStream.flush();
  }

  @Override
  public void close() throws IOException {
    fileOutputStream.close();
  }

  private static void ensureParentDirExists(File file) {
    if (file.getParentFile() != null && !file.getParentFile().isDirectory()) {
      boolean success = file.getParentFile().mkdirs() || file.getParentFile().isDirectory();
      if (!success) {
        ErrorHandler.error("Failed to create directory " + file.getParentFile().getPath());
      }
    }
  }
}

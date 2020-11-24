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

import org.w3c.dom.Document;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;

import java.io.OutputStream;
import java.net.URL;

class OutputFile {
  URL url;

  public OutputFile(URL url) {
    this.url = url;
  }

  public void write(Document doc) {
    try (OutputStream outputStream = new ResultFileOutputStream(url)) {
      DOMImplementationRegistry reg = DOMImplementationRegistry.newInstance();
      DOMImplementationLS impl = (DOMImplementationLS) reg.getDOMImplementation("LS");
      LSSerializer serializer = impl.createLSSerializer();
      serializer.getDomConfig().setParameter("format-pretty-print", Boolean.TRUE);
      LSOutput output = impl.createLSOutput();
      output.setByteStream(outputStream);
      serializer.write(doc, output);
    } catch (Exception e) {
      ErrorHandler.error("Failed to write the result XML to the file system.", e);
    }
  }

}

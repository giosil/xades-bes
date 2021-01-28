package org.dew.test;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;

import java.net.URL;

import java.security.cert.X509Certificate;

import org.dew.xades.XAdESSigner;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestXAdES extends TestCase {

  public TestXAdES(String testName) {
    super(testName);
  }
  
  public static Test suite() {
    return new TestSuite(TestXAdES.class);
  }
  
  public void testApp() throws Exception {
    String xml = "<document><id>1</id></document>";
    
    System.out.println(xml);
    
    XAdESSigner signer = new XAdESSigner("keystore.jks", "password", "selfsigned");
    
    String signed = signer.sign(xml);
    
    System.out.println(signed);
    
    X509Certificate cert = signer.validate(signed);
    
    System.out.println("cert=" + cert);
  }
  
  protected static
  String readFile(String sFile)
    throws Exception
  {
    int iFileSep = sFile.indexOf('/');
    if(iFileSep < 0) iFileSep = sFile.indexOf('\\');
    InputStream is = null;
    if(iFileSep < 0) {
      URL url = Thread.currentThread().getContextClassLoader().getResource(sFile);
      is = url.openStream();
    }
    else {
      is = new FileInputStream(sFile);
    }
    try {
      int n;
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      byte[] buff = new byte[1024];
      while((n = is.read(buff)) > 0) baos.write(buff, 0, n);
      return new String(baos.toByteArray());
    }
    finally {
      if(is != null) try{ is.close(); } catch(Exception ex) {}
    }
  }
}
package org.dew.test;

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
    String xml = "<document><id>001</id></document>";
    
    System.out.println(xml);
    
    XAdESSigner signer = new XAdESSigner();
    
    String signed = signer.sign(xml);
    
    System.out.println(signed);
  }
}
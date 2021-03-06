package org.dew.xades;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

import java.net.URL;

import java.security.Key;
import java.security.KeyException;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;

import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.xml.crypto.AlgorithmMethod;
import javax.xml.crypto.KeySelector;
import javax.xml.crypto.KeySelectorException;
import javax.xml.crypto.KeySelectorResult;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.dom.DOMStructure;

import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLObject;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.KeyValue;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;

public 
class XAdESSigner
{
  protected String keystoreFile    = "keystore.jks";
  protected String keystorePass    = "password";
  protected String keystoreAlias   = "selfsigned";
  protected String privateKeyPass  = "password";
  protected String privateKeyFile  = "privatekey.pem";
  protected String certificateFile = "certificate.crt";
  
  protected PrivateKey      privateKey;
  protected X509Certificate certificate;
  
  protected boolean omitDec = true;
  protected boolean indent  = false;
  
  protected String typAlgorithm = "http://www.w3.org/2000/09/xmldsig#enveloped-signature";
  protected String canAlgorithm = "http://www.w3.org/2006/12/xml-c14n11#WithComments";
  protected String digAlgorithm = "http://www.w3.org/2001/04/xmlenc#sha256";
  protected String sigAlgorithm = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256";
  
  public XAdESSigner()
  {
  }
  
  public XAdESSigner(Map<String, Object> options)
  {
    this.setOptions(options);
  }
  
  public XAdESSigner(String keystoreFile, String password, String alias)
  {
    this.keystoreFile   = keystoreFile;
    this.keystorePass   = password;
    this.privateKeyPass = password;
    this.keystoreAlias  = alias;
  }
  
  public XAdESSigner(String keystoreFile, String password, String alias, String privateKeyPass)
  {
    this.keystoreFile   = keystoreFile;
    this.keystorePass   = password;
    this.privateKeyPass = privateKeyPass;
    this.keystoreAlias  = alias;
  }
  
  public XAdESSigner(X509Certificate certificate, PrivateKey privateKey)
  {
    this.certificate = certificate;
    this.privateKey  = privateKey;
  }
  
  public 
  void setOptions(Map<String, Object> options) 
  {
    if(options == null || options.isEmpty()) {
      return;
    }
    
    Object oPrivateKey = options.get("privatekey");
    if(oPrivateKey instanceof PrivateKey) {
      this.privateKey = (PrivateKey) oPrivateKey;
    }
    
    Object oCertificate = options.get("certificate");
    if(oCertificate instanceof X509Certificate) {
      this.certificate = (X509Certificate) oCertificate;
    }
    
    Object oOmitXmlDeclaration = options.get("omit_xml_declaration");
    if(oOmitXmlDeclaration instanceof Boolean) {
      this.omitDec = ((Boolean) oOmitXmlDeclaration).booleanValue();
    }
    else if(oOmitXmlDeclaration != null) {
      String sOmitXmlDeclaration = oOmitXmlDeclaration.toString();
      if(sOmitXmlDeclaration != null && sOmitXmlDeclaration.length() > 0) {
        char c0 = sOmitXmlDeclaration.charAt(0);
        this.omitDec = "1TYStys".indexOf(c0) >= 0;
      }
    }
    
    Object oIdent = options.get("indent");
    if(oIdent instanceof Boolean) {
      this.indent = ((Boolean) oIdent).booleanValue();
    }
    else if(oIdent != null) {
      String sIdent = oIdent.toString();
      if(sIdent != null && sIdent.length() > 0) {
        char c0 = sIdent.charAt(0);
        this.indent = "1TYStys".indexOf(c0) >= 0;
      }
    }
    
    this.keystoreFile    = toString(options.get("keystore_file"),    keystoreFile);
    this.keystorePass    = toString(options.get("keystore_pass"),    keystorePass);
    this.keystoreAlias   = toString(options.get("keystore_alias"),   keystoreAlias);
    this.privateKeyPass  = toString(options.get("privatekey_pass"),  privateKeyPass);
    this.privateKeyFile  = toString(options.get("privatekey_file"),  privateKeyFile);
    this.certificateFile = toString(options.get("certificate_file"), certificateFile);
    
    this.typAlgorithm = toString(options.get("type_algorithm"), typAlgorithm);
    this.canAlgorithm = toString(options.get("can_algorithm"),  canAlgorithm);
    this.digAlgorithm = toString(options.get("dig_algorithm"),  digAlgorithm);
    this.sigAlgorithm = toString(options.get("sign_algorithm"), sigAlgorithm);
  }
  
  public 
  String sign(byte[] content) 
    throws Exception 
  {
    if(this.privateKey == null) loadPrivateKey();
    
    if(this.privateKey == null) {
      throw new Exception("privatekey unavailable");
    }
    if(this.certificate == null) {
      throw new Exception("certificate unavailable");
    }
    if(content == null || content.length == 0) {
      throw new Exception("Invalid content");
    }
    
    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
    Document document = documentBuilder.parse(new ByteArrayInputStream(content));
    
    XMLSignature xmlSignature = createXMLSignature(document);
    
    Element rootNode = document.getDocumentElement();
    
    DOMSignContext domSignContext = new DOMSignContext(privateKey, rootNode);
    
    xmlSignature.sign(domSignContext);
    
    return nodeToString(rootNode);
  }
  
  public 
  String sign(String content) 
    throws Exception 
  {
    if(content == null || content.length() == 0) {
      return null;
    }
    
    return sign(content.getBytes());
  }
  
  public 
  X509Certificate validate(byte[] signed) 
    throws Exception 
  {
    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
    
    documentBuilderFactory.setNamespaceAware(true);
    
    DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
    
    Document document = documentBuilder.parse(new ByteArrayInputStream(signed));
    
    NodeList nodeListQP = document.getElementsByTagName("QualifyingProperties");
    if (nodeListQP != null && nodeListQP.getLength() > 0) {
      for (int i = 0; i < nodeListQP.getLength(); ++i) {
        Element elementQP = (Element) nodeListQP.item(i);
        if (elementQP != null) {
          elementQP.setIdAttribute("Id", true);
        }
      }
    }
    
    NodeList nodeListSignature = document.getElementsByTagNameNS(XMLSignature.XMLNS, "Signature");
    if (nodeListSignature.getLength() == 0) {
      return null;
    }
    
    DOMValidateContext domValidateContext = new DOMValidateContext(new KeyValueKeySelector(), nodeListSignature.item(0));
    
    XMLSignatureFactory xmlSignatureFactory = XMLSignatureFactory.getInstance("DOM");
    
    XMLSignature xmlSignature = xmlSignatureFactory.unmarshalXMLSignature(domValidateContext);
    
    boolean isValid = false;
    try {
      isValid = xmlSignature.validate(domValidateContext);
    }
    catch(Exception ex) {
      System.err.println("validate: " + ex);
    }
    if(isValid) {
      return getCertificate(xmlSignature.getKeyInfo());
    }
    return null;
  }
  
  public 
  X509Certificate validate(String signed) 
    throws Exception 
  {
    if(signed == null || signed.length() == 0) {
      return null;
    }
    
    return validate(signed.getBytes());
  }
  
  protected 
  XMLSignature createXMLSignature(Document document)
    throws Exception
  {
    // Defaults:
    //
    // typAlgorithm = "http://www.w3.org/2000/09/xmldsig#enveloped-signature";
    // canAlgorithm = "http://www.w3.org/2006/12/xml-c14n11#WithComments";
    // digAlgorithm = "http://www.w3.org/2001/04/xmlenc#sha256";
    // sigAlgorithm = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256";
    
    XMLSignatureFactory xmlSignatureFactory = XMLSignatureFactory.getInstance("DOM", new org.apache.jcp.xml.dsig.internal.dom.XMLDSigRI());
    CanonicalizationMethod c14nMethod = xmlSignatureFactory.newCanonicalizationMethod(canAlgorithm, (C14NMethodParameterSpec) null);
    DigestMethod         digestMethod = xmlSignatureFactory.newDigestMethod(digAlgorithm, null);
    SignatureMethod        signMethod = xmlSignatureFactory.newSignatureMethod(sigAlgorithm, null);
    Transform            sigTransform = xmlSignatureFactory.newTransform(typAlgorithm, (TransformParameterSpec) null);
    Transform            canTransform = xmlSignatureFactory.newTransform(canAlgorithm, (TransformParameterSpec) null);
    
    List<Transform> transforms = new ArrayList<Transform>();
    transforms.add(sigTransform);
    transforms.add(canTransform);
    
    String sQualifyingPropertiesId = "QualifyingProperties_ID";
    
    Reference referenceDoc = xmlSignatureFactory.newReference("", digestMethod, transforms, null, null);
    Reference referenceQuP = xmlSignatureFactory.newReference("#" + sQualifyingPropertiesId, xmlSignatureFactory.newDigestMethod(digAlgorithm, null));
    
    List<Reference> references = new ArrayList<Reference>();
    references.add(referenceDoc);
    references.add(referenceQuP);
    
    SignedInfo signedInfo = xmlSignatureFactory.newSignedInfo(c14nMethod, signMethod, references);
    
    KeyInfoFactory keyInfoFactory = xmlSignatureFactory.getKeyInfoFactory();
    X509Data x509Data = keyInfoFactory.newX509Data(Collections.singletonList(certificate));
    KeyInfo keyInfo   = keyInfoFactory.newKeyInfo(Collections.singletonList(x509Data));
    
    Element qualifyingPropertiesElement = buildQualifyingProperties(document, sQualifyingPropertiesId);
    DOMStructure qualifyingPropertiesObject = new DOMStructure(qualifyingPropertiesElement);
    XMLObject qualifyingPropertiesXMLObject = xmlSignatureFactory.newXMLObject(Collections.singletonList(qualifyingPropertiesObject), null, null, null);
    
    List<XMLObject> objects = new ArrayList<XMLObject>();
    objects.add(qualifyingPropertiesXMLObject);
    
    return xmlSignatureFactory.newXMLSignature(signedInfo, keyInfo, objects, "xmldsig-" + UUID.randomUUID(), null);
  }
  
  protected 
  Element buildQualifyingProperties(Document document, String sId)
    throws Exception
  {
    String signingTime = toISO8601Timestamp_Z(Calendar.getInstance());
    
    Element qualifyingPropertiesElement = document.createElement("QualifyingProperties");
    if(sId != null && sId.length() > 0) {
      qualifyingPropertiesElement.setAttribute("Id", sId);
      qualifyingPropertiesElement.setIdAttribute("Id", true);
    }
    
    Element signedPropertiesElement = document.createElement("SignedProperties");
    Element signedSignaturePropertiesElement = document.createElement("SignedSignatureProperties");
    Element signingTimeElement = document.createElement("SigningTime");
    signingTimeElement.setTextContent(signingTime);
    
    qualifyingPropertiesElement.appendChild(signedPropertiesElement);
    signedPropertiesElement.appendChild(signedSignaturePropertiesElement);
    signedSignaturePropertiesElement.appendChild(signingTimeElement);
    
    return qualifyingPropertiesElement;
  }
  
  protected
  String nodeToString(Node node)
    throws Exception
  {
    StringWriter stringWriter = new StringWriter();
    try {
      Transformer transformer = TransformerFactory.newInstance().newTransformer();
      transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, omitDec ? "yes" : "no");
      transformer.setOutputProperty(OutputKeys.INDENT, indent ? "yes" : "no");
      transformer.transform(new DOMSource(node), new StreamResult(stringWriter));
    } 
    catch (Exception ex) {
      ex.printStackTrace();
      throw ex;
    }
    return stringWriter.toString();
  }
  
  protected
  void loadPrivateKey()
    throws Exception
  {
    certificate = null;
    privateKey  = null;
    
    if(keystoreAlias != null && keystoreAlias.length() > 0) {
      KeyStore keyStore = loadKeyStore();
      
      if(keyStore != null) {
        if(keystorePass == null) keystorePass = "";
        if(privateKeyPass == null || privateKeyPass.length() == 0) {
          if(keystorePass != null && keystorePass.length() > 0) {
            privateKeyPass = keystorePass;
          }
        }
        
        Certificate aliasCertificate = keyStore.getCertificate(keystoreAlias);
        if(aliasCertificate instanceof X509Certificate) {
          certificate = (X509Certificate) aliasCertificate;
        }
        
        Key aliasKey = keyStore.getKey(keystoreAlias, privateKeyPass.toCharArray());
        if(aliasKey instanceof PrivateKey) {
          privateKey = (PrivateKey) aliasKey;
        }
      }
    }
    
    if(certificate == null) {
      certificate = loadCertificateFile();
    }
    if(privateKey == null) {
      privateKey = loadPrivateKeyFile();
    }
  }
  
  protected
  KeyStore loadKeyStore()
    throws Exception
  {
    KeyStore keyStore = null;
    
    if(keystoreFile == null || keystoreFile.length() == 0) {
      return keyStore;
    }
    
    InputStream is = openResource(keystoreFile);
    if(is == null) return keyStore;
    
    if(keystorePass == null) keystorePass = "";
    
    Security.addProvider(new BouncyCastleProvider());
    
    if(keystoreFile.endsWith(".p12")) {
      keyStore = KeyStore.getInstance("PKCS12", "BC");
    }
    else {
      keyStore = KeyStore.getInstance("JKS");
    }
    keyStore.load(is, keystorePass.toCharArray());
    
    return keyStore;
  }
  
  protected
  X509Certificate loadCertificateFile()
    throws Exception
  {
    if(certificateFile == null || certificateFile.length() == 0) {
      return null;
    }
    
    InputStream is = openResource(certificateFile);
    if(is == null) return null;
    
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try {
      int n;
      byte[] buff = new byte[1024];
      while((n = is.read(buff)) > 0) baos.write(buff, 0, n);
    }
    finally {
      if(is != null) try{ is.close(); } catch(Exception ex) {}
    }
    byte[] content = baos.toByteArray();
    if(content == null || content.length < 4) {
      throw new Exception("Invalid file");
    }
    if(content[0] == 45 && content[1] == 45 && content[2] == 45) {
      String sContent = new String(content);
      int iStart = sContent.indexOf("ATE-----");
      if(iStart > 0) {
        int iEnd = sContent.indexOf("-----END");
        if(iEnd > 0) {
          String sBase64 = sContent.substring(iStart+8, iEnd).trim();
          content = Base64Coder.decodeLines(sBase64);
        }
      }
    }
    ByteArrayInputStream bais = new ByteArrayInputStream(content);
    CertificateFactory cf = CertificateFactory.getInstance("X.509");
    return (X509Certificate) cf.generateCertificate(bais);
  }
  
  protected
  PrivateKey loadPrivateKeyFile()
    throws Exception
  {
    if(privateKeyFile == null || privateKeyFile.length() == 0) {
      return null;
    }
    
    InputStream is = openResource(privateKeyFile);
    if(is == null) return null;
    
    PEMParser pemParser = null;
    try {
      Security.addProvider(new BouncyCastleProvider());
      
      pemParser = new PEMParser(new InputStreamReader(is));
      
      Object object = pemParser.readObject();
      
      if(object instanceof PEMKeyPair) {
        
        PEMKeyPair pemKeyPair = (PEMKeyPair) object;
        
        KeyPair keyPair = new JcaPEMKeyConverter().getKeyPair(pemKeyPair);
        
        return keyPair.getPrivate();
      }
      
      throw new Exception("Invalid pem file " + privateKeyFile);
    }
    finally {
      if(is != null) try{ is.close(); } catch(Exception ex) {}
      if(pemParser != null) try{ pemParser.close(); } catch(Exception ex) {}
    }
  }
  
  protected static
  InputStream openResource(String fileName)
    throws Exception
  {
    if(fileName == null || fileName.length() == 0) {
      return null;
    }
    if(fileName.startsWith("/") || fileName.indexOf(':') > 0) {
      return new FileInputStream(fileName);
    }
    URL url = Thread.currentThread().getContextClassLoader().getResource(fileName);
    if(url == null) return null;
    return url.openStream();
  }
  
  protected static
  String toString(Object value, String sDefaultValue)
  {
    if(value == null) return sDefaultValue;
    String result = value.toString();
    if(result == null || result.length() == 0) {
      return sDefaultValue;
    }
    return result;
  }
  
  protected static
  String toISO8601Timestamp_Z(Calendar c)
  {
    if(c == null) return null;
    
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.YEAR,        c.get(Calendar.YEAR));
    cal.set(Calendar.MONTH,       c.get(Calendar.MONTH));
    cal.set(Calendar.DATE,        c.get(Calendar.DATE));
    cal.set(Calendar.HOUR_OF_DAY, c.get(Calendar.HOUR_OF_DAY));
    cal.set(Calendar.MINUTE,      c.get(Calendar.MINUTE));
    cal.set(Calendar.SECOND,      c.get(Calendar.SECOND));
    
    int iZoneOffset = cal.get(Calendar.ZONE_OFFSET);
    cal.add(Calendar.MILLISECOND, -iZoneOffset);
    int iDST_Offset = cal.get(Calendar.DST_OFFSET);
    cal.add(Calendar.MILLISECOND, -iDST_Offset);
    
    int iYear  = cal.get(Calendar.YEAR);
    int iMonth = cal.get(Calendar.MONTH) + 1;
    int iDay   = cal.get(Calendar.DATE);
    int iHour  = cal.get(Calendar.HOUR_OF_DAY);
    int iMin   = cal.get(Calendar.MINUTE);
    int iSec   = cal.get(Calendar.SECOND);
    String sYear   = String.valueOf(iYear);
    String sMonth  = iMonth < 10 ? "0" + iMonth : String.valueOf(iMonth);
    String sDay    = iDay   < 10 ? "0" + iDay   : String.valueOf(iDay);
    String sHour   = iHour  < 10 ? "0" + iHour  : String.valueOf(iHour);
    String sMin    = iMin   < 10 ? "0" + iMin   : String.valueOf(iMin);
    String sSec    = iSec   < 10 ? "0" + iSec   : String.valueOf(iSec);
    if(iYear < 10) {
      sYear = "000" + sYear;
    }
    else if(iYear < 100) {
      sYear = "00" + sYear;
    }
    else if(iYear < 1000) {
      sYear = "0" + sYear;
    }
    return sYear + "-" + sMonth + "-" + sDay + "T" + sHour + ":" + sMin + ":" + sSec + "Z";
  }
  
  protected static
  X509Certificate getCertificate(KeyInfo keyInfo)
  {
    if (keyInfo == null) return null;
    
    List<?> keyInfoContent = keyInfo.getContent();
    
    for (int i = 0; i < keyInfoContent.size(); i++) {
      Object keyInfoItem = keyInfoContent.get(i);
      
      if(keyInfoItem instanceof X509Data) {
        List<?> x509DataContent = ((X509Data) keyInfoItem).getContent();
        for (int j = 0; j < x509DataContent.size(); j++) {
          Object x509Item = x509DataContent.get(i);
          if(x509Item instanceof X509Certificate) {
            return ((X509Certificate) x509Item);
          }
        }
      }
    }
    
    return null;
  }
  
  private static 
  class KeyValueKeySelector extends KeySelector 
  {
    public 
    KeySelectorResult select(KeyInfo keyInfo, KeySelector.Purpose purpose, AlgorithmMethod method, XMLCryptoContext context)
      throws KeySelectorException 
    {
      if (keyInfo == null) {
        throw new KeySelectorException("KeyInfo is null");
      }
      List<?> keyInfoContent = keyInfo.getContent();
      PublicKey publicKey = null;
      for (int i = 0; i < keyInfoContent.size(); i++) {
        Object keyInfoItem = keyInfoContent.get(i);
        
        if (keyInfoItem instanceof KeyValue) {
          try {
            publicKey = ((KeyValue) keyInfoItem).getPublicKey();
            break;
          } 
          catch (KeyException ke) {
            throw new KeySelectorException(ke);
          }
        }
        else if(keyInfoItem instanceof X509Data) {
          List<?> x509DataContent = ((X509Data) keyInfoItem).getContent();
          for (int j = 0; j < x509DataContent.size(); j++) {
            Object x509Item = x509DataContent.get(i);
            if(x509Item instanceof Certificate) {
              publicKey = ((Certificate) x509Item).getPublicKey();
              break;
            }
          }
        }
      }
      if(publicKey != null) {
        return new SimpleKeySelectorResult(publicKey);
      }
      throw new KeySelectorException("No PublicKey found");
    }
  }
  
  private static
  class SimpleKeySelectorResult implements KeySelectorResult 
  {
    PublicKey publicKey;
    
    public SimpleKeySelectorResult(PublicKey publicKey)
    {
      this.publicKey = publicKey;
    }
    
    public
    Key getKey()
    {
      return publicKey;
    }
  }
}


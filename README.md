# XAdES-BES

Utility class to sign xml width XAdES-BES (XML Advanced Electronic Signatures) digital signature.

## Example

```java
String xml = "<document><id>1</id></document>";

XAdESSigner signer = new XAdESSigner("keystore.jks", "password", "selfsigned");

String signed = signer.sign(xml);

boolean isValid = signer.validate(signed);
```

**Output:**

```xml
<document>
  <id>1</id>
  <Signature xmlns="http://www.w3.org/2000/09/xmldsig#" Id="xmldsig-f500d752-2926-4c0b-ab78-bf039dc47e54">
    <SignedInfo>
      <CanonicalizationMethod Algorithm="http://www.w3.org/2006/12/xml-c14n11#WithComments"/>
      <SignatureMethod Algorithm="http://www.w3.org/2001/04/xmldsig-more#rsa-sha256"/>
      <Reference URI="">
        <Transforms>
          <Transform Algorithm="http://www.w3.org/2000/09/xmldsig#enveloped-signature"/>
          <Transform Algorithm="http://www.w3.org/2006/12/xml-c14n11#WithComments"/>
        </Transforms>
        <DigestMethod Algorithm="http://www.w3.org/2001/04/xmlenc#sha256"/>
        <DigestValue>zPneqKXvQX7LCLt38TtAHsw3IJSMwPObQgVCqv5ENbY=</DigestValue>
      </Reference>
      <Reference URI="#QualifyingProperties_ID">
        <DigestMethod Algorithm="http://www.w3.org/2001/04/xmlenc#sha256"/>
        <DigestValue>dELwQV/Re3WROkb1FZqEsUU1OgoG4bDclquMhB4pEqU=</DigestValue>
      </Reference>
    </SignedInfo>
    <SignatureValue>
      rbde0iq+wsPIVgEPgCu4sWK1wYikG3/A1hnT/X2ensvmQmFjrBrvbjwuF8hHOBq3BWQ1InTZiFPO
      3pIf0UdeblV0f6w0tX6jM5gIFotieoKboHSJOtwU3TdwpXRWtroxqtxKw2YbFQT4CkCdFKxqAMNU
      zMjniSt0nHVqXSw8DN74uCQBFC7wREfk/jo9ZVX3qQLLKwvd5lMSRpCqDzc3SqYS9G+XxTfwM/XL
      mfVVNXiO8vASIjcIuDvxVvn6FFztFaxrUpASEnikbFTmAqbn12zL8x2k8JxWQ1PRwYEq7URDYx5V
      EwoFHRArb2ZlX9O3MpLW87BVrDMjvaD+CYCHkg==
    </SignatureValue>
    <KeyInfo>
      <X509Data>
        <X509Certificate>
          MIIDRTCCAi2gAwIBAgIEQjgrajANBgkqhkiG9w0BAQsFADBTMQswCQYDVQQGEwJpdDELMAkGA1UE
          CBMCcm0xDTALBgNVBAcTBHJvbWUxDDAKBgNVBAoTA2RldzEMMAoGA1UECxMDZGV3MQwwCgYDVQQD
          EwNkZXcwHhcNMjAxMTEzMTQzMDI2WhcNNDAxMTE0MTQzMDI2WjBTMQswCQYDVQQGEwJpdDELMAkG
          A1UECBMCcm0xDTALBgNVBAcTBHJvbWUxDDAKBgNVBAoTA2RldzEMMAoGA1UECxMDZGV3MQwwCgYD
          VQQDEwNkZXcwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCwT1J/myUxkngvu0GR6SMn
          Bhb/CUeuCgqzSabZhW3CFBwdYwHOp9omfrJiVm81rHvD41TU5ys+EWZn3EeohdvogtlQk6kNLE7K
          BnXaGHiWmnu4JgeGimuWwg35BGwh0TrVBCnP6RnnUXjJzu6i5Lo2FuFW6z7RbZyBDC8K2+l0+wJR
          ZW8LJvmOE8vLxxUN+aLWI9TYmk55jpmcAU1Gzi+QGVkdFIkfHk5AiViJRx+Fk28NE9EGHE+MYABz
          wpUwdeXCYe79l2IeDUdPSPLZpwYdn3qhCWvOvCZ+JzancFvd8h6UlW//LhmzdoUMurSbjqMSOdYh
          9M32cjWnuLPvpftBAgMBAAGjITAfMB0GA1UdDgQWBBRRgluPbGEZ0C6ptwS/J6quOrxjrzANBgkq
          hkiG9w0BAQsFAAOCAQEAO5dEtpYFQLFvmaTmPdYR8zVznEOVlZUcmax0ttZZfKGnSqm8l316nuIW
          2gnkRFwak55DOfEh7Z7WTxf1a3yFeOC9fB9zfb1GCvL9umoUWPylU1Wac+cNGN5NHEnrZLsjd++B
          EcCetZZQHVGT9gfOZKXKt5d0NsdlB7pPNLu5BVxiNdspR08qrZaQl92m+al4bbvwXo/3vg6xFtmq
          n1jE1Dl9hj+/SSrMUj0kQ6lcW3Ju8U6kLc2Dnfygw1m+pAD8+Y2gOmMg/VNtuRRn0SVv+jKubfdl
          G5ydx/oib3D2IMgk2BUf2lDMU4fvU3LdpMPkD9kDt/dY13PDMuTIeDMOdA==
        </X509Certificate>
      </X509Data>
    </KeyInfo>
    <Object>
      <QualifyingProperties Id="QualifyingProperties_ID">
        <SignedProperties>
          <SignedSignatureProperties>
            <SigningTime>2021-01-28T13:35:58Z</SigningTime>
          </SignedSignatureProperties>
        </SignedProperties>
      </QualifyingProperties>
    </Object>
  </Signature>
</document>
```

## Build

- `git clone https://github.com/giosil/xades-bes.git`
- `mvn clean install`

## Contributors

* [Giorgio Silvestris](https://github.com/giosil)

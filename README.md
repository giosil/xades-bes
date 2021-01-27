# XAdES-BES

Utility class to sign xml width XAdES-BES (XML Advanced Electronic Signatures) digital signature.

## Example

```java
String xml = "<document><id>001</id></document>";

XAdESSigner signer = new XAdESSigner();

String signed = signer.sign(xml);
```

## Build

- `git clone https://github.com/giosil/xades-bes.git`
- `mvn clean install`

## Contributors

* [Giorgio Silvestris](https://github.com/giosil)

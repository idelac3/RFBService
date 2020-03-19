# Remote frame buffer (RFB) library for Java
Protocol <i>RFB</i> is used by VNC servers and clients to share and give control of remote desktop. This is *server-side* Java library.

## Build
To build this library, use _maven_ command: 
```
mvn install
```

Then add it to your project, into _pom.xml_ file as dependency:
```
<dependency>
	<groupId>com.scoreunit.rfb</groupId>
	<artifactId>service</artifactId>
	<version>0.0.1-SNAPSHOT</version>
</dependency>
```

Alternatively, if you don't use _maven_ to manage dependencies, just copy source code into your project.

## Usage

To start VNC service and wait for VNC viewer to connect:
```java
		final RFBService service = new RFBService();
		service.start();
```
RFB service will listen on default TCP port _5900_.

To start VNC service on another TCP port:
```java
		final RFBService service = new RFBService(1234);
		service.start();
```
RFB service will listen on TCP port _1234_.

To start VNC service and share only part of screen use _ScreenClip_ object:
```java
		final RFBService service = new RFBService();
		service.setScreenClip(0, 0, 800, 600);
		service.start();
```
RFB service will use part of screen, _800x600_ pixel, instead of full screen.

To start VNC service with configured password, use _setPassword(String)_ method:
```java
		final RFBService service = new RFBService();
		service.setPassword("mySecret123");
		service.start();
```
RFB service will ask VNC client to enter correct password.

To start VNC service and override client preferred encoding of screen:
```java
		final RFBService service = new RFBService();
		service.setPreferredEncodings(new int[]{Encodings.ZLIB, Encodings.HEXTILE});
		service.start();
```
RFB service will use preferred encodings set to ZLIB and HEXTILE.

To start VNC service and connect to VNC client which is in listening mode, use:
```java
		final RFBService service = new RFBService();
		service.connect("my-client-1", 5550);
```
RFB service will start and establish TCP connection to VNC client _my-client-1_ listening at TCP port _5500_.

## Command line use

Example class _RFBServiceExample_ provides demo code and allows RFB service library to start as stand-alone Java program.

Command line arguments:
```

RFB service

Usage:
 --port [tcp port] 
	 Bind to specific TCP port, and wait for VNC client. Default TCP port is 5900.

 --connect [hostname:port] 
	 Establish connection to VNC client. Default TCP port is 5500.

 --clip [width+height] 
	 Share only part of screen. Eg. 800+600 to share 800x600 pixels of screen, at (0,0) pixel offset.

 --password [secret] 
	 Set VNC auth. When VNC client connects, it will have to provide correct password.

Author: igor.delac@gmail.com
```
Use command line switch _--help_ to identify all available options.
 
## Tested with VNC clients

Table shows which VNC client programs have been used to test this library.

| VNC Client      | Version               | Operating System | Encodings          | Result |
| --------------- | --------------------- | ---------------- | ------------------ | ------ |
| RealVNC Viewer  | 6.20.113 (r42314) x86 | Windows XP (x86) | Raw, hextile       | OK     |
| TightVNC Viewer | 1.3.10.0 (x86)        | Windows XP (x86) | Raw, hextile, zlib | OK     |
| UltraVNC Viewer | 1.2.4.0 (x86)         | Windows XP (x86) | Raw, hextile, zlib | OK     |
| RealVNC Viewer  | 4.1.1 for X (amd86)   | Linux Mint (x64) | Raw, hextile       | OK     |
| SSVNC   Viewer  | 1.0.29      (amd86)   | Linux Mint (x64) | Zlib               | OK     |


Note that some combinations of colour depth, bits per pixel, etc. might not work well or will not work at all.
For example, with TightVNC viewer, if _bgr233_ 8-bit colour mode is selected, then use encoding _raw_ or _zlib_.
RealVNC viewer should use options _-AutoSelect=0_ with _-LowColourLevel=1_ and _-PreferredEncoding=raw_ to correctly use low colour mode.

## Securing communication with SSL

RFB service library supports SSL secure communication. To enable SSL support, generate private key and certificate.

To generate a private key and certificate (self-signed), use <i>openssl</i> utility.
```
openssl req -x509 -newkey rsa:4096 -keyout key.pem -out cert.pem -days 365 -subj '/CN=localhost' -nodes
openssl pkcs12 -export -inkey key.pem -in cert.pem -out final_result.pfx
```

First command will build _key.pem_ file, and certificate _cert.pem_ with example CN attribute set to _localhost_.

Second command will bundle key and certificate into _PKCS12_ format, in this example into _final_result.pfx_ file.

If Java _keytool_ program is available, it is possible to generate _JKS_ key store file.

```
keytool -genkeypair -keyalg rsa -keysize 4096 -keystore mystore.jks -storepass blabla123 -storetype jks -v
```

In this example chosen key store file name is _mystore.jks_ with password _blabla123_ and default validity period of 90 days.

Enabling SSL support should be easy.

```java
		final RFBService service = new RFBService();
		service.enableSSL(sslKeyFilePath, sslKeyFilePassword);
```

Note that some VNC client programs do not support SSL secure communication at all. SSL communication has been tested with _ssvnc_ VNC client program.
 
## Contribution

Please feel free to suggest new ideas, improvements, bug-fixes, etc.

## Word from author
	
The ideas presented here are not for commercial or production use. Please contact author if you need to use this library in a commercial product.

Author: igor.delac@gmail.com

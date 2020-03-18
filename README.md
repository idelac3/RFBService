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
This will listen on default TCP port _5900_.

To start VNC service on another TCP port:
```java
		final RFBService service = new RFBService(1234);
		service.start();
```
This will listen on TCP port _1234_.

To start VNC service and share only part of screen use _ScreenClip_ object:
```java
		final RFBService service = new RFBService();
		service.setScreenClip(0, 0, 800, 600);
		service.start();
```
This will tell service to use part of screen, _800x600_ pixel, instead of full screen.

To start VNC service with configured password, use _setPassword(String)_ method:
```java
		final RFBService service = new RFBService();
		service.setPassword("mySecret123");
		service.start();
```
This will ask VNC client to enter correct password.

To start VNC service and override client preferred encoding of screen:
```java
		final RFBService service = new RFBService();
		service.setPreferredEncodings(new int[]{Encodings.ZLIB, Encodings.HEXTILE});
		service.start();
```
This will start VNC service with preferred encodings set to ZLIB and HEXTILE.

To start VNC service and connect to VNC client which is in listening mode, use:
```java
		final RFBService service = new RFBService();
		service.connect("my-client-1", 5550);
```
This will start VNC service and establish TCP connection to VNC client _my-client-1_ listening at TCP port _5500_.


## Tested with VNC clients

Table shows which VNC clients have been used to test this library.

| VNC Client      | Version               | Operating System | Encodings          | Result |
| --------------- | --------------------- | ---------------- | ------------------ | ------ |
| RealVNC Viewer  | 6.20.113 (r42314) x86 | Windows XP (x86) | Raw, hextile       | OK     |
| TightVNC Viewer | 1.3.10.0 (x86)        | Windows XP (x86) | Raw, hextile, zlib | OK     |
| UltraVNC Viewer | 1.2.4.0 (x86)         | Windows XP (x86) | Raw, hextile, zlib | OK     |
| RealVNC Viewer  | 4.1.1 for X (amd86)   | Linux Mint (x64) | Raw, hextile       | OK     |

Note that some combinations of colour depth, bits per pixel, etc. might not work well or will not work at all.
For example, with TightVNC viewer, if _bgr233_ 8-bit colour mode is selected, then use encoding _raw_ or _zlib_.
RealVNC viewer should use options _-AutoSelect=0_ with _-LowColourLevel=1_ and _-PreferredEncoding=raw_ to correctly use low colour mode.

## Contribution

Please feel free to suggest new ideas, improvements, bug-fixes, etc.

## Word from author
	
The ideas presented here are not for commercial or production use. Please contact author if you need to use this library in a commercial product.

Author: igor.delac@gmail.com

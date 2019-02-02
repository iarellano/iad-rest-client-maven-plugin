# iad-rest-client-maven-plugin

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT) 
[![Maven Central](https://img.shields.io/maven-central/v/com.github.iarellano/iad-rest-client-maven-plugin.svg)](https://mvnrepository.com/artifact/com.github.iarellano/iad-rest-client-maven-plugin) 

## REST Client maven plugin
At times we need to consume external services as part of a build process, for example to verify which version of an API is deployed, verify that a service is online, get configurations from some remote storage provider, etc.
 
This plugin is aimed to provide a way to make HTTP request either to get resources, upload files, submit forms, post json, post xml, etc.

## Table of contents

- [Functional testing](#Functional-testing)
- [Integration tests](#Integration-tests)
- [Getting started](#Getting-started)
- [Configuration](#Configuration)
- [Changelog](#Changelog)
- [Artifacts used to develop this plugin](#Artifacts-used-to-develop-this-plugin)
- [References](#References)
 
## Functional testing
Test cases are in directory [src/test/resources/projects-to-test](src/test/resources/projects-to-test), they are copied during *process-resource* phase to [target/test/resources/projects-to-test](target/test/resources/projects-to-test) where the configurations are setup by using the [maven-resources-plugin:2.6](https://maven.apache.org/plugins/maven-resources-plugin/), also a directory named *config* is created where the web application properties are copied and setup again by using the maven-resources-plugin

A full set of test cases have been provided so they can be used as examples. Also a web application has been created to verify such cases, this web application resides on directory [src/test/java](src/test/java).

To execute the tests just run ```mvn clean test``` in your system console


If you need to change the web application configuration to suit your needs such as in what port you web application will listen, you can change them in the properties of the [pom.xml](pom.xml)
```xml
<properties>
    <test.server.host>localhost</test.server.host>
    <test.server.port>9088</test.server.port>
    <test.server.ssl.port>9443</test.server.ssl.port>
    <test.proxy.server.port>8089</test.proxy.server.port>
</properties>
```

## Integration tests
Besides the provided web application for functional testing, for integration testing [https://httpbin.org](https://httpbin.org) is used, to execute thees IT tests just run in your system console ```mvn clean verify -Prun-its -DskipTests=true```

## Getting started
You can look at the test so you can get a quick grasp of how to use it in directories in [src/test/resources/projects-to-test](src/test/resources/projects-to-test) and [src/it](src/it), nonetheless here is a sample usage of the plugin.

```xml
<build>
    <plugins>
        <plugin>
            <groupId>com.github.iarellano</groupId>
            <artifactId>iad-rest-client-maven-plugin</artifactId>
            <version>1.0.1</version>
            <executions>
                <execution>
                    <id>post-json</id>
                    <phase>process-resources</phase>
                    <goals>
                        <goal>http-request</goal>
                    </goals>
                    <configuration>
                        <resourceUrl>https://httpbin.org/anything</resourceUrl>
                        <method>POST</method>
                        <headers>
                            <Content-Type>application/json</Content-Type>
                        </headers>
                        <payload>{"firstName": "John", "lastName": "Connor"}</payload>
                        <responseOutput>target/response.json</responseOutput>
                    </configuration>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

### Configuration

| Parameter                                 | Description                           |
| ----------------------------------------- | ------------------------------------- |
| [method](#method)                         | HTTP method to  perform. e.g. *GET*, *POST*, *PUT*, *DELETE*, etc. Non-standard HTTP methods can be specified i.e. *SUBMIT* (as long as the server supports it) |
| [resourceUrl](#resourceUrl)               | Target URL to consume, it can be a template URL if used along with pathParams                  |
| [pathParameters](#pathParameters)         | Helps to parameterize the resourceUrl |
| [headers](#headers)                       | Key value map where the header name is the key |
| [queryParams](#queryParams)               | Query parameters to add to the query string |
| [authorization](#authorization)           | Allows to authenticate the request using Basic Authentication |
| [payload](#payload)                       | Allows to embed the request body directly in the pom.xml |
| [form](#form)                             | Allows to submit a form (MediaType = application/x-www-form-urlencoded) |
| [multipart](#multipart)                   | Allows to submit a form which includes files (MediaType = multipart/form-data) |
| [file](#file)                             | Allows to submit a file as a raw body |
| [responseOutput](#responseOutput)         | Sets the file to persist the response body |
| [headersOutput](#headersOutput)           | Sets the file to persist the response headers, headers are persisted as properties. e.g. ContentType=application/json |
| [headersPrefix](#headersPrefix)           | When headers are persisted to a property file using [headersOutput](#headersOutput), properties are prefixed with the headersPrefix value |
| [proxy](#proxy)                           | Used when the request needs to go through a proxy, also useful for debugging |
| [validateServerCert](#validateServerCert) | When false it turns off certificate validation, helpful in cases such as playing with self signed certificates. (Use with caution) |
| [extractJson](#extractJson)               | Allows to extract values from a json response using JSONPath and then assign them to system properties during the rest of the maven build lifecycle |
| [extractXml](#extractXml)                 | Allows to extract values from an xml response using XPath and then assign them to system properties during the rest of the maven build lifecycle |
| [extractPrefix](#extractPrefix)           | When either extractJson or extractXml is set, this value is prepended to names of properties |
| [successCodes](#successCodes)             | Sets which response status codes are interpreted as a successful request |
| [charset](#charset)                       | Assists the plugin by specifying the request charset to use to encode the query string and form fields |
| [followRedirect](#followRedirect)         | Instructs the plugin to follow redirects when 302 response status is received |
| [sslConfig](#sslConfig)                   | Allows to define a keystore, trustore, alias and password to performe request wich require 2-way SSL |
| [cookieConfig](#cookieConfig)             | Allows to persist coockies to file so they can survive through JVMs restart |
| [connectTimeout](#connectTimeout)         | Sets a time in milliseconds to wait for connection to be established |
| [readTimeout](#readTimeout)               | Sets a time in milliseconds to start receving data from the server |
| [loadHeadersToSysProperties](#loadHeadersToSysProperties) | When set to true it loads the response headers as system properties |
| [loadHeadersPrefix](#responseHeadersPrefix) | When response headers are loaded as system properties, the name of those properties are prefixed whith this value to avoid name clashes |

> From all the parameters above, only *resourceUrl* is required.

### method
HTTP method to  perform. e.g. *GET*, *POST*, *PUT*, *DELETE*, etc. Non-standard HTTP methods can be specified i.e. *SUBMIT* (as long as the server supports it)

|                   |             |
| ----------------- | ----------- |
| Required          | No         |
| Type              | String      |
| Default           | GET         |

Example:
```xml
<configuration>
    <method>GET</method>
    <resourceUrl>https://httpbin.org/get</resourceUrl>
</configuration>
```

Curl:
```bash
curl -X GET 'https://httpbin.org/get'
```
[Go to Configuration parameters list](#Configuration)

### resourceUrl
Target URL to consume, it can be a template URL if used along with pathParams

|                   |             |
| ----------------- | ----------- |
| Required          | Yes          |
| Type              | String      |

Example:
```xml
<configuration>
    <resourceUrl>https://httpbin.org/get</resourceUrl>
</configuration>
```

Curl:
```bash
curl -X GET 'https://httpbin.org/get'
```
[Go to Configuration parameters list](#Configuration)

### pathParameters
Helps to parameterize the resourceUrl when it is in a template form. 

|                   |                |
| ----------------- | -------------- |
| Required          | No             |
| Type              | Key Value Map  |

Example:
```xml
<configuration>
    <resourceUrl>https://httpbin.org/{basePath}/{data}</resourceUrl>
    <pathParameters>
        <basePath>base64</basePath>
        <data>${some.system.property}</data>
    </pathParameters>
</configuration>
```

Curl:
> Assuming that value of property *some.system.property* is *SFRUUEJJTiBpcyBhd2Vzb21l*
```bash
curl -X GET 'https://httpbin.org/base64/SFRUUEJJTiBpcyBhd2Vzb21l'
```
[Go to Configuration parameters list](#Configuration)

### headers
Key value map where the header name is the key

|                   |               |
| ----------------- | ------------- |
| Required          | No            |
| Type              | Key Value Map |

Example:
```xml
<configuration>
    <resourceUrl>https://httpbin.org/encoding/utf8</resourceUrl>
    <headers>
        <Accept>application/xml</Accept>
        <Cache-Control>no-cache</Cache-Control>
    </headers>
</configuration>
```
Curl:
```bash
curl -X GET https://httpbin.org/encoding/utf8 -H 'Accept: application/xml' -H 'Cache-Control: no-cache'
```
[Go to Configuration parameters list](#Configuration)

### queryParams
Query parameters to add to the query string

|                   |                                   |
| ----------------- | --------------------------------- |
| Required          | No                                |
| Type              | List of [queryParam](#QueryParam) |

#### QueryParam
| Property | Type   | Required | Description                  |
| -------- | ------ | ---------| -----------------------------|
| name     | String | Yes      | Name of the query parameter  |
| value    | String | No       | Value of que query parameter |

Example:
```xml
<configuration>
    <resourceUrl>https://httpbin.org/get</resourceUrl>
    <queryParams>
        <queryParam>
            <name>offset</name>
            <value>50</value>
        </queryParam>
        <queryParam>
            <name>limit</name>
            <value>10</value>
        </queryParam>
    </queryParams>
</configuration>
```
Curl:
```bash
curl -X GET 'https://httpbin.org/get?offset=50&limit=10'
```
[Go to Configuration parameters list](#Configuration)

### authorization
Allows to authenticate the request using Basic Authentication

|                   |               |
| ----------------- | ------------- |
| Required          | No            |
| Type              | Key Value Map (only username and password keys are accepted) |

Example:
```xml
<configuration>
    <resourceUrl>https://httpbin.org/get</resourceUrl>
    <authorization>
        <username>user</username> <!-- default is empty string -->
        <password>passwd</password> <!-- default is empty string -->
    </authorization>
</configuration>
```
Curl:
```bash
curl -X GET https://httpbin.org/get -H 'Authorization: Basic dXNlcjpwYXNzd2Q='
```
[Go to Configuration parameters list](#Configuration)

### payload
Allows to embed the request body directly in the pom.xml

|                   |               |
| ----------------- | ------------- |
| Required          | No            |
| Type              | String        |
| Excludes          | [form](#form), [multipart](#multipart), [file](#file) |

Example:
```xml
<configuration>
    <resourceUrl>https://httpbin.org/post</resourceUrl>
    <method>POST</method>
    <payload>{"firstName": "${character.firstName}", "lastName": "${character.lastName}"}</payload>
</configuration>
```
Assuming properties:
- character.firstName=John
- character.lastName=Connor
Curl:
```bash
curl -X POST https://httpbin.org/post \
  -H 'Content-Type: application/json' \
  -d '{"firstName": "John", "lastName": "Connor"}'
```
[Go to Configuration parameters list](#Configuration)

### form
Allows to submit a form (MediaType = application/x-www-form-urlencoded)

|                   |                                 |
| ----------------- | ------------------------------- |
| Required          | No                              |
| Type              | List of [FormInput](#FormInput) |
| Excludes          | [payload](#payload), [multipart](#multipart), [file](#file) |

#### FormInput

| Property | Type   | Required | Description  |
| -------- | ------ | ---------| ------------ |
| name     | String | Yes      | Field name   |
| value    | String | No       | Field value  |

Example:
```xml
<configuration>
    <resourceUrl>https://httpbin.org/post</resourceUrl>
    <method>POST</method>
    <form>
        <input>
            <name>firstName</name>
            <value>John</value>
        </input>
        <input>
            <name>lastName</name>
            <value>Connor</value>
        </input>
    </form>
</configuration>
```
Curl:
```bash
curl -X POST https://httpbin.org/post \
  -H 'Content-Type: application/x-www-form-urlencoded' \
  -d 'firstName=John&lastName=Connor'
```
[Go to Configuration parameters list](#Configuration)

### multipart
Allows to submit a form which includes files (MediaType = multipart/form-data)

|                   |                                            |
| ----------------- | ------------------------------------------ |
| Required          | No                                         |
| Type              | List of [MultipartInput](#MultipartInput)  |
| Excludes          | [payload](#payload), [form](#form), [file](#file> |

#### MultipartInput

| Property | Type               | Required | Excludes | Description  |
| -------- | ------------------ | -------- | -------- | ------------ |
| name     | String             | Yes      |          | Field name   |
| value    | String             | No       | file     | Field value  |
| file     | String (File path) | No       | value    | File to upload |


Example:
```xml
<configuration>
    <resourceUrl>https://httpbin.org/post</resourceUrl>
    <method>POST</method>
    <multipart>
        <input>
            <name>filename</name>
            <value>entrevista.txt</value>
        </input>
        <input>
            <name>filetype</name>
            <value>text</value>
        </input>
        <input>
            <name>file1</name>
            <file>file1.txt</file>
        </input>
        <input>
            <name>file2</name>
            <file>file2.txt</file>
            <contentType>text/plain</contentType> <!-- If not provided plugin will try to guess it -->
        </input>
    </multipart>
</configuration>
```
Curl:
```bash
curl -X POST 'https://httpbin.org/post' \
  -H 'content-type: multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW' \
  -F file1=@file1.txt \
  -F firstName=John \
  -F lastName=Connor \
  -F file2=@file2.txt
```
[Go to Configuration parameters list](#Configuration)

### file
It is relative to pom.xml

|                   |                                 |
| ----------------- | ------------------------------- |
| Required          | No                              |
| Type              | String (File path) |
| Excludes          | [payload](#payload), [multipart](#multipart), [form](#form) |

Example:
```xml
<configuration>
    <resourceUrl>https://httpbin.org/anything</resourceUrl>
    <file>file.json</file>
    <method>POST</method>
    <headers>
        <Content-Type>application/json</Content-Type>
    </headers>
</configuration>
```
[Go to Configuration parameters list](#Configuration)

### responseOutput
Sets the file to persist the response body. It is relative to pom.xml

|                   |                      |
| ----------------- | -------------------- |
| Required          | No                   |
| Type              | String (File path)   |

Example:
```xml
<configuration>
    <resourceUrl>https://httpbin.org/get</resourceUrl>
    <respContentFile>target/get.json</dest>
</configuration>
```
Curl:
```bash
curl -X GET 'https://httpbin.org/get' -o target/get.json 
```
[Go to Configuration parameters list](#Configuration)

### headersOutput
Sets the file to persist the response headers, headers are persisted as properties. e.g. ContentType=application/json. It is relative to pom.xml

|                   |                      |
| ----------------- | -------------------- |
| Required          | No                   |
| Type              | String (File path)   |

Example:
```xml
<configuration>
    <resourceUrl>https://httpbin.org/get</resourceUrl>
    <headersOutput>target/headers.properties</headersOutput>
</configuration>
```
File content is save as:
```properties
# Header properties from https://httpbin.org/get
Connection=keep-alive
Server=gunicorn/19.9.0
Date=Tue, 22 Jan 2019 04:14:53 GMT
Content-Type=application/json
Content-Length=217
Access-Control-Allow-Origin=*
Access-Control-Allow-Credentials=true
```
[Go to Configuration parameters list](#Configuration)

### headersPrefix
When headers are persisted to a property file using [headersOutput](#headersOutput), properties are prefixed with the headersPrefix value

|                   |         |
| ----------------- | ------- |
| Required          | No      |
| Type              | String  |

Example:
```xml
<configuration>
    <resourceUrl>https://httpbin.org/get</resourceUrl>
    <headersOutput>target/headers.properties</headersOutput>
    <headersPrefix>response.get.</headersPrefix>
</configuration>
```
File content is save as:
```properties
# Header properties from https://httpbin.org/get
response.get.Connection=keep-alive
response.get.Server=gunicorn/19.9.0
response.get.Date=Tue, 22 Jan 2019 04:14:53 GMT
response.get.Content-Type=application/json
response.get.Content-Length=217
response.get.Access-Control-Allow-Origin=*
response.get.Access-Control-Allow-Credentials=true
```
[Go to Configuration parameters list](#Configuration)

### proxy
Used when the request needs to go through a proxy, also useful for debugging

|                   |                          |
| ----------------- | ------------------------ |
| Required          | No                       |
| Type              | Object [Proxy](#Proxy)   |

#### Proxy

| Property | Type    | Required | Default | Description    |
| -------- | ------- | ---------| ------- | -------------- |
| host     | String  | Yes      |         | Proxy hostname |
| port     | Integer | Yes      |         | Proxy port     |
| type     | String  | No       | HTTP    | Proxy type, valid values are HTTP, SOCKS. See [java.net.Proxy](https://docs.oracle.com/javase/7/docs/api/java/net/Proxy.Type.html) |

Example:
```xml
<configuration>
    <resourceUrl>https://httpbin.org/get</resourceUrl>
    <proxy>
        <host>localhost</host>
        <port>3333</port>
        <type>HTTP</type>
    </proxy>
</configuration>
```
Curl:
```bash
curl -X GET 'https://httpbin.org/get' --proxy localhost:3333 -k
```
[Go to Configuration parameters list](#Configuration)

### validateServerCert
When false it turns off certificate validation, helpful in cases such as playing with self signed certificates. Use it with caution!

|                   |         |
| ----------------- | ------- |
| Required          | No      |
| Type              | Boolean |
| Default           | true    |

Example:
```xml
<configuration>
    <resourceUrl>https://httpbin.org/get</resourceUrl>
    <respHeadersFile>target/headers.properties</dest>
    <validateServerCert>false</validateServerCert>
</configuration>
```
Curl:
```bash
curl -X GET 'https://httpbin.org/get' -k
```
[Go to Configuration parameters list](#Configuration)

### extractJson
Allows to extract values from a json response using JSONPath and then assign them to system properties during the rest of the maven build lifecycle

|                   |                                            |
| ----------------- | ------------------------------------------ |
| Required          | No                                         |
| Type              | List of [JsonProperty](#JsonProperty)  |
| Excludes          | [extractXml](#extractXml)                  |

#### JsonProperty

| Property | Type               | Required | Default | Description  |
| -------- | ------------------ | -------- | ------- | ----- |
| name     | String             | Yes      |         | System property to assign the extracted value |
| jsonPath | String             | Yes      |         | JsonPath to evaluate  |
| failFast | Boolean            | No       | true    | If an error occurs while evaluating the expression or no value is retrieved then an error is thrown |

Example:
```xml
<configuration>
    <resourceUrl>https://httpbin.org/anything</resourceUrl>
    <method>GET</method>
    <extractJson>
        <property>
            <name>responseBody.method</name>
            <jsonPath>$.method</jsonPath>
        </property>
        <property>
            <name>responseBody.origin</name>
            <jsonPath>$.origin</jsonPath>
        </property>
    </extractJson>
</configuration>
```
The configuration above will setup two system properties named with the respective extracted values as:
```properties
responseBody.method=[extracted value]
responseBody.origin=[extracted value]
```
[Go to Configuration parameters list](#Configuration)

### extractXml
Allows to extract values from an xml response using XPath and then assign them to system properties during the rest of the maven build lifecycle

|                   |                                            |
| ----------------- | ------------------------------------------ |
| Required          | No                                         |
| Type              | List of [XmlProperties](#XmlProperties)       |
| Excludes          | [extractJson](#extractJson)                |

#### XmlProperties

| Property   | Type                        | Required | Default | Description  |
| ---------- | --------------------------- | -------- | ------- | ----- |
| namespaces | Key Value Map               | No      |         | Namespaces to use in xpath expresion |
| properties | [XmlProperty](#XmlProperty) | Yes             | Yes      |         | JsonPath to evaluate  |

#### XmlProperty
| Property   | Type   | Required | Default | Description  |
| ---------- | -------| -------- | ------- | ----- |
| name       | String | Yes      |         | System property to assign the extracted value |
| xpath      | String | Yes      |         | XPath to evaluate  |
| failfast   | String | Yes      | true    | If an error occurs while evaluating the expression or no value is retrieved then an error is thrown |

Example:
```xml
<configuration>
    <resourceUrl>https://httpbin.org/xml</resourceUrl>
    <method>GET</method>
    <extractXml>
        <namespaces>
            <test>http://localhost/root</test>
            <child>http://localhost/root/child</child>
        </namespaces>
        <properties>
            <property>
                <name>response.nodeValue</name>
                <xpath>/test:parent/child:innerChild/grandChild</xpath>
                <failFast>true</failFast>
            </property>
        </properties>
    </extractXml>
</configuration>
```
Assuming the response contest is:
```xml
<test:parent version="1.0" xmlns:test="http://localhost/root">
    <child:innerChild xmlns:child="http://localhost/root/child\">
        <grandChild>I am grandson of my parent's parent</grandChild>
    </child:innerChild>
</test:parent>
```
Then a system property is set up as:
```properties
response.nodeValue=I am grandson of my parent's parent
```
[Go to Configuration parameters list](#Configuration)

### extractPrefix
If used in conjunction with extractJson or extractXml, this value is prefixed to extracted property names, it can be used to avoid clashes of property names

|                   |              |
| ----------------- | ------------ |
| Required          | No           |
| Type              | String       |

Example:
```xml
<configuration>
    <resourceUrl>https://httpbin.org/anything</resourceUrl>
    <method>GET</method>
    <extractPrefix>avoid.name.clashes.</extractPrefix>
    <extractJson>
        <property>
            <name>responseBody.method</name>
            <value>$.method</value>
        </property>
        <property>
            <name>responseBody.origin</name>
            <value>$.origin</value>
        </property>
    </extractJson>
</configuration>
```
The configuration above will setup two system properties named with the respective extracted values as:
```properties
avoid.name.clashes.responseBody.method=[extracted value]
avoid.name.clashes.responseBody.origin=[extracted value]
```
[Go to Configuration parameters list](#Configuration)

### successCodes
Sets which response status codes are interpreted as a successful request. Any reponse code out from this list will thrown an error


|                   |                 |
| ----------------- | --------------- |
| Required          | No              |
| Type              | String          |
| Default           | 200,201,202,204 |

Example 1:
```xml
<configuration>
    <resourceUrl>https://httpbin.org/anything</resourceUrl>
    <method>GET</method>
    <successCodes>200,202</successCodes>
</configuration>
```
Example 2:
```xml
<configuration>
    <resourceUrl>https://httpbin.org/anything</resourceUrl>
    <method>GET</method>
    <successCodes>2XX,302,404</successCodes> <!-- Will accept all 200s along with 302 and 404 codes as successful responses -->
</configuration>
```
[Go to Configuration parameters list](#Configuration)

### charset
Assists the plugin by specifying the request charset to use to encode the query string and form fields

|                   |                 |
| ----------------- | --------------- |
| required          | No              |
| Type              | String          |
| Default           | UTF-8           |

Example:
```xml
<configuration>
    <charset>UTF-8</charset>
    <resourceUrl>https://httpbin.org/redirect-to</resourceUrl>
    <queryParams>
        <queryParam>
            <name>url</name>
            <value>https://httpbin.org/anything</value>
        </queryParam>
        <queryParam>
            <name>status_code</name>
            <value>302</value>
        </queryParam>
    </queryParams>
</configuration>
```
[Go to Configuration parameters list](#Configuration)

### followRedirect
Instructs the plugin to follow redirects when 302 response status is received. Notice that upgrade redirects (from http to https) are not followed for security reasons.

|                   |                 |
| ----------------- | --------------- |
| Required          | No              |
| Type              | Boolean         |
| Default           | false           |


Example:
```xml
<configuration>
    <resourceUrl>https://httpbin.org/redirect-to</resourceUrl>
    <queryParams>
        <queryParam>
            <name>url</name>
            <value>https://httpbin.org/anything</value>
        </queryParam>
        <queryParam>
            <name>status_code</name>
            <value>302</value>
        </queryParam>
    </queryParams>
    <followRedirect>true</followRedirect>
</configuration>
```
[Go to Configuration parameters list](#Configuration)

### sslConfig
Allows to define a keystore, trustore, alias and password to performe request wich require 2-way SSL.

|                   |                       |
| ----------------- | --------------------- |
| Required          | No                    |
| Type              | [SSLCofig](#SSLCofig) |


#### SSLCofig

| Property           | Type               | Required | Default | Description      |
| ------------------ | ------------------ | ---------| ------- | ---------------- |
| type               | String             | Yes      |         | Type of keystore |
| protocol           | String             | Yes      |         | Protocol to use  |
| keystore           | String (File path) | Ye       |         | KeyStore file    |
| keystorePassword   | String             | No       | null    | Password to open the KeyStore |
| truststore         | String (File path) | Yes      |         | trustore file, can be the same as keystore |
| truststorePassword | String             | No       | null    | Password to open the truststore |
| clientAlias        | String             | No       | null    | Key alias |

Example:
```xml
<configuration>
    <resourceUrl>https://httpbin.org/anything</resourceUrl>
    <sslConfig>
        <type>JKS</type>
        <protocol>TLSv1.2</protocol>
        <keystore>ssl-client.jks</keystore>
        <keystorePassword>password</keystorePassword>
        <truststore>ssl-client.jks</truststore>
        <truststorePassword>password</truststorePassword>
        <clientAlias>ssl-client</clientAlias>
    </sslConfig>
</configuration>
```
[Go to Configuration parameters list](#Configuration)

### connectTimeout
Sets a time in milliseconds to wait for connection to be established. More info see [URLConnection.setConnectTimeout](https://docs.oracle.com/javase/7/docs/api/java/net/URLConnection.html#setConnectTimeout(int)), a value of 0 means to wait forever.

|                   |                 |
| ----------------- | --------------- |
| Required          | No              |
| Type              | Integer         |
| Default           | 0               |

Example:
```xml
<configuration>
    <resourceUrl>https://httpbin.org/get</resourceUrl>
    <connectTimeout>10000</connectTimeout>
</configuration>
```
[Go to Configuration parameters list](#Configuration)

### readTimeout
Sets a time in milliseconds to start receving data from the server. More info see [URLConnection.setReadTimeout](https://docs.oracle.com/javase/7/docs/api/java/net/URLConnection.html#setReadTimeout(int)), a value of 0 means to wait forever.

|                   |                 |
| ----------------- | --------------- |
| Required          | No              |
| Type              | Integer         |
| Default           | 0               |

Example:
```xml
<configuration>
    <resourceUrl>https://httpbin.org/get</resourceUrl>
    <readTimeout>10000</readTimeout>
</configuration>
```
[Go to Configuration parameters list](#Configuration)
 
### cookieConfig
Allows to persist coockies to file so they can survive through JVMs restart.

|                   |                               |
| ----------------- | ----------------------------- |
| Required          | No                            |
| Type              | [CookieConfig](#CookieConfig) |

#### CookieConfig

| Property           | Type               | Required | Default                | Description      |
| ------------------ | ------------------ | ---------| ---------------------- | ---------------- |
| cookiePolicy       | String             | No       | ACCEPT_ORIGINAL_SERVER | Default redirect policy, see [java.net.CookiePolicy](https://docs.oracle.com/javase/7/docs/api/java/net/CookiePolicy.html) |
| cookieJar          | String (File path) | No       |                        | File to persist cookies so they can survive after the JVM stops |
| removeAll          | Boolean            | No       | false                  | If true, all cookies that have been preloaded from storage will be removed |
| accept             | List of String     | No       |                        | List of domains to accept in addition to the config *cookiePolicy* |
> If cookieJar is not provided then cookies are stored in memory therefore they will only exist for current JVM executing instance.

Example:
```xml
<configuration>
    <resourceUrl>https://httpbin.org/anything</resourceUrl>
    <cookieConfig>
        <cookieJar>cookies.json</cookieJar>
        <removeAll>false</removeAll>
        <accept>
            <domain>localhost.local</domain>
        </accept>
    </cookieConfig>
</configuration>
```
[Go to Configuration parameters list](#Configuration)

### loadHeadersToSysProperties
When set to true it loads the response headers to system properties

|                   |                 |
| ----------------- | --------------- |
| Required          | No              |
| Type              | Boolean         |
| Default           | false           |

Example:
```xml
<configuration>
    <resourceUrl>https://httpbin.org/get</resourceUrl>
    <loadHeadersToSysProperties>true</loadHeadersToSysProperties>
</configuration>
```
The config above will load to system properties the response headers so they are available from the execution of this plugin to the end of the maven lifecycle
```properties
Connection= keep-alive
Server= gunicorn/19.9.0
Content-Type=application/json
Access-Control-Allow-Origin=*
Access-Control-Allow-Credentials=true
Via=1.1 vegur
```
[Go to Configuration parameters list](#Configuration)

### loadHeadersPrefix
When set to true it loads the response headers to system properties

|                   |                 |
| ----------------- | --------------- |
| Required          | No             |
| Type              | String          |

Example:
```xml
<configuration>
    <resourceUrl>https://httpbin.org/get</resourceUrl>
    <loadHeadersToSysProperties>true</loadHeadersToSysProperties>
    <loadHeadersPrefix>response.get.headers.</loadHeadersPrefix>
</configuration>
```
The config above will load to system properties the response headers so they are available from the execution of this plugin to the end of the maven lifecycle
```properties
response.get.headers.Connection= keep-alive
response.get.headers.Server= gunicorn/19.9.0
response.get.headers.Content-Type=application/json
response.get.headers.Access-Control-Allow-Origin=*
response.get.headers.Access-Control-Allow-Credentials=true
response.get.headers.Via=1.1 vegur
```
[Go to Configuration parameters list](#Configuration)

## Changelog

## Artifacts used to develop this plugin
- [httpbin.org](https://httpbin.org)
- [Postman](https://www.getpostman.com/)
- [OWASP Zed Attack Proxy](https://www.owasp.org/index.php/OWASP_Zed_Attack_Proxy_Project)

## References
- [Parsing and producing JSON](http://groovy-lang.org/json.html)
- [Java URLConnection with mutual authentication](http://javasecurity.wikidot.com/example-item-1)


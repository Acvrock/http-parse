#Http-Parse

**Http-Parse** 一个HTTP流量探测解析库。 http-parse会自动识别`byte[]`中符合HTTP协议的流量

# Features

1. 自动识别编码
2. gzip自动解压

# Quick Start

探测IO流中的HTTP报文

```java
byte[] buffer = new byte[2048];
HttpMessageParser httpMessageParser = new HttpMessageParser();
int length =-1;
while((length=inputstream.read(buffer)) != -1){
  try{
    httpMessageParser.write(buffer, length); //写入数据，如果数据符合HTTP协议, 将自动缓存， 如果不符合，将会抛出HttpParseException
    if (httpMessageParser.isComplete()) {  //当捕获到一个完整的HTTP报文
      HttpMessage httpMessage = httpMessageParser.getHttpMessage();//提取捕获的HTTP报文
      if(httpMessage.isRequestMessage()){//请求报文
        ...
      }else{ //响应报文
        ...
      }
        ...
      httpMessageParser.reset();  //清空当前缓存的报文信息。
    }
  }catch(HttpParseException e){
     ...  //写入的数据不符合HTTP协议， httpMessageParser自动清空之前的数据缓存
  }
}
```#

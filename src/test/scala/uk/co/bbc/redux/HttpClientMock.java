package org.apache.commons.httpclient;

import org.apache.commons.httpclient.*;
import java.io.ByteArrayInputStream;

public class HttpClientMock extends HttpClient {
   private int expectedResponseStatus;
   private ByteArrayInputStream expectedResponseBody;

   public HttpClientMock (int responseStatus, ByteArrayInputStream responseBody) {
      this.expectedResponseStatus = responseStatus;
      this.expectedResponseBody = responseBody;
   }

   @Override
   public int executeMethod(HttpMethod method) {
      try {
        ((HttpMethodBase)method).setResponseStream(expectedResponseBody);
        return expectedResponseStatus;
      } catch (Exception e) {
        throw new RuntimeException();
      }
   }

}
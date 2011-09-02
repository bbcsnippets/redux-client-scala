package uk.co.bbc.redux

import java.io.File
import java.io.ByteArrayInputStream
import java.io.FileInputStream
import org.apache.commons.httpclient._
import org.apache.commons.io.IOUtils

trait MockableHttp {

  def mockClient(status:Int, file:FileInputStream) : HttpClientMock = {
    new HttpClientMock(status, new ByteArrayInputStream(IOUtils.toByteArray(file)))
  }

  def mockClient(status:Int, string:String) : HttpClientMock = {
    new HttpClientMock(status, new ByteArrayInputStream(string.getBytes("UTF-8")))
  }

}

trait TestFile {

  def testFile (path:String) : FileInputStream = {
    val path = Thread.currentThread().getContextClassLoader().getResource("frame_collection.jpg").getFile
    val file = new File(path)
    new FileInputStream(file)
  }

}
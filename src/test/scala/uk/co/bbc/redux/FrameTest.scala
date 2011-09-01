package uk.co.bbc.redux

import org.junit._
import Assert._
import java.io.File
import java.io.FileInputStream
import java.awt.image.BufferedImage

@Test
class FrameTest {

  @Test
  def widthCorrect  = assertEquals(Frame.WIDTH, 480)

  @Test
  def heightCorrect = assertEquals(Frame.HEIGHT, 270)

  @Test
  def sanityCheckFromInputStream {
    val dest:BufferedImage = Frame.fromInputStream(testImageStream, 20)
    assertEquals(480, dest.getWidth)
    assertEquals(270, dest.getHeight)
  }

  @Test(expected = classOf[FrameNotFoundException])
  def fromInputStreamThrows {
    Frame.fromInputStream(testImageStream, 61)
  }

  def testImageStream = {
    val path = Thread.currentThread().getContextClassLoader().getResource("frame_collection.jpg").getFile
    val file = new File(path)
    new FileInputStream(file)
  }

}



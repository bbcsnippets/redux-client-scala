package uk.co.bbc.redux

import org.junit._
import Assert._
import java.awt.image.BufferedImage

@Test
class FramesTest {

  def frames:Frames = {
    var image:BufferedImage = new BufferedImage(480 * 5, 270, BufferedImage.TYPE_3BYTE_BGR)
    new Frames(image)
  }

  @Test
  def testSeconds() {
    assertEquals(5, frames.seconds)
  }

  @Test
  def testHasFrame() {
    assertEquals(true,  frames.hasFrame(1))
    assertEquals(false, frames.hasFrame(5))
    assertEquals(false, frames.hasFrame(-1))
  }

  @Test
  def testGetFrame() {
    var image:BufferedImage = frames.getFrame(1)
    assertEquals(frames.FRAME_WIDTH,  image.getWidth())
    assertEquals(frames.FRAME_HEIGHT, image.getHeight())
  }

  @Test(expected = classOf[FrameNotFoundException])
  def testGetFrameThrows() {
    frames.getFrame(1000)
  }

}

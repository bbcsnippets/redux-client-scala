package uk.co.bbc.redux

import java.awt.image.BufferedImage

class FrameNotFoundException extends Exception

class Frames(val image:BufferedImage) {

  val FRAME_WIDTH:Int  = 480
  val FRAME_HEIGHT:Int = 270

  // Num of seconds in this frames object
  def seconds: Int = image.getWidth() / FRAME_WIDTH

  // Has the frame for second in this minute?
  def hasFrame(second:Int): Boolean = (seconds - 1) >= second && second >= 0

  // Retreive the frame for this second (e.g crop image)
  def getFrame(second:Int): BufferedImage = {
    if (hasFrame(second)) {
      image.getSubimage(FRAME_WIDTH * second, 0, FRAME_WIDTH, FRAME_HEIGHT)
    } else {
      throw new FrameNotFoundException
    }
  }

}
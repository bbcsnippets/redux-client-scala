package uk.co.bbc.redux

import java.awt.image.BufferedImage
import java.io.InputStream
import javax.imageio.ImageReader
import javax.imageio.stream.ImageInputStream
import javax.imageio.ImageReadParam
import javax.imageio.ImageIO
import java.awt.Rectangle
import java.awt.image.BufferedImage

class FrameNotFoundException extends Exception

object Frame {

  val WIDTH:Int  = 480
  val HEIGHT:Int = 270

  def fromInputStream(inputStream:InputStream, second:Int) : BufferedImage = {
    var imageReader:ImageReader      = ImageIO.getImageReadersByFormatName("JPEG").next()
    var imageStream:ImageInputStream = ImageIO.createImageInputStream(inputStream)

    // Throw up an error if the requested second is out of range
    if (second * WIDTH > imageReader.getWidth(0)) throw new FrameNotFoundException

    var param:ImageReadParam         = imageReader.getDefaultReadParam()
    imageReader.setInput(imageStream)
    param.setSourceRegion(new Rectangle(second * WIDTH, 0, WIDTH, HEIGHT))
    imageReader.read(0, param)
  }

}
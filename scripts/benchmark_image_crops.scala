import java.util.Date
import javax.imageio.ImageReader
import javax.imageio.stream.ImageInputStream
import javax.imageio.ImageReadParam
import java.awt.image.BufferedImage
import java.io.FileInputStream
import javax.imageio.ImageIO
import java.awt.Rectangle

def benchmark (description:String) (block: => Unit) {
  var start:Date = new Date
  block
  var time:Long = (new Date).getTime() - start.getTime()
  println(description+" took : "+time.toString+" ms")
}

def image = {
  new FileInputStream("/Users/matth/work/redux-client-scala/scripts/image.jpg")
}

// Warm up
ImageIO.read(image)

benchmark ("Cropping image using ImageReader") {
  for (i <- 0 until 100) {
    var reader:ImageReader   = ImageIO.getImageReadersByFormatName("JPEG").next()
    var iis:ImageInputStream = ImageIO.createImageInputStream(image)
    reader.setInput(iis)
    var param:ImageReadParam = reader.getDefaultReadParam()
    param.setSourceRegion(new Rectangle(100, 100, 100, 100))
    var detail:BufferedImage = reader.read(0, param)
  }
}

benchmark ("Cropping image using BufferedImage") {
  for (i <- 0 until 100) {
    var source:BufferedImage = ImageIO.read(image)
    var detail:BufferedImage = source.getSubimage(100, 100, 100, 100)
  }
}

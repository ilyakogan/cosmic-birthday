
var a = List[Int]()

val notableBinaryMultiples =
  (7 to 30).toStream.map(x => {
    a = x +: a
    Math.pow(2, x).toInt
  })

a

notableBinaryMultiples.take(2)
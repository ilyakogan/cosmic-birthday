package com.cosmicbirthday.calc

import com.cosmicbirthday.entities.RelativeBirthday
import org.joda.time.Period

class RelativeBirthdayGenerator {
  private implicit def convertIterable(source: Iterable[Int]): Iterable[Int] = source.map(_.toInt)

  private val yearInDays = 365.242

  private def every(x: Int) = Stream.from(0).map(_ * x)

  private def makeStream(unitStream: Iterable[Int], mnemonic: String, period: Period): Iterable[RelativeBirthday] =
    unitStream.map(x => new RelativeBirthday(period.multipliedBy(x), x, mnemonic))

  private val planetYearsInEarthDays = Map(
    "Mercury" -> 87.96,
    "Venus" -> 224.68,
    "Earth astronomic" -> 365.26,
    "Mars" -> 686.98,
    "Jupiter" -> 11.862 * yearInDays,
    "Saturn" -> 29.456 * yearInDays,
    "Uranus" -> 84.07 * yearInDays,
    "Neptune" -> 164.81 * yearInDays)

  private val planetYearStreams: List[Iterable[RelativeBirthday]] =
    for {
      (name, earthDays) <- planetYearsInEarthDays.toList
    } yield makeStream(
      every(1),
      name + " years",
      Period.minutes((earthDays * 24 * 60).toInt))

  val periodStreams: List[Iterable[RelativeBirthday]] =
    makeStream(every(1000), "days", Period.days(1)) +:
      makeStream(every(100), "weeks", Period.weeks(1)) +:
      makeStream(every(100), "months", Period.months(1)) +:
      makeStream(every(1), "years", Period.years(1)) +:
      makeStream(0 to 9999 by 1111, "days", Period.days(1)) +:
      makeStream(0 to 99999 by 11111, "days", Period.days(1)) +:
      makeStream(0 to 999 by 111, "weeks", Period.weeks(1)) +:
      makeStream(0 to 9999 by 1111, "weeks", Period.weeks(1)) +:
      makeStream(0 to 999 by 111, "months", Period.months(1)) +:
      planetYearStreams
}

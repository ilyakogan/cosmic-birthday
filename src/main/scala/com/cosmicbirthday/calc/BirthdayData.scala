package com.cosmicbirthday.calc

import com.cosmicbirthday.R
import com.cosmicbirthday.entities._
import org.joda.time.{Duration, Period}

class BirthdayData {
  private implicit def convertIterable(source: Iterable[Int]): Iterable[Int] = source.map(_.toInt)

  private val yearInDays = 365.242

  private val notableDecimalMultipleSequences =
    List(
      1 to 19 by 1,
      20 to 190 by 10,
      200 to 1900 by 100,
      2000 to 19000 by 1000,
      20000 to 190000 by 10000,
      200000 to 1900000 by 100000,
      111 to 999 by 111,
      1111 to 9999 by 1111,
      11111 to 99999 by 11111).map(_.toStream).map(_.map(x => new Multiple(x)))

  private val notableBinaryMultiples =
    (7 to 30).toStream.map(x => {
      val number = Math.pow(2, x).toInt
      new Multiple(number, number + " (1" + "0" * x + " binary)")
    })

  private val notableMultipleSequences =
    notableBinaryMultiples +: notableDecimalMultipleSequences

  private val detailedSequence = List((1 to 1000 by 1).toStream.map(x => new Multiple(x)))

  private def makeStreamsInDays(mnemonic: String, image: Int, days: Double): List[Stream[RelativeBirthday]] =
    for (sequence <- notableMultipleSequences)
    yield
      sequence.map(multiple =>
        new DurationBasedRelativeBirthday(
          new Duration((days * 24 * 3600 * 1000).toLong).multipliedBy(multiple.number),
          new BirthdayDescription(multiple.alias + " " + mnemonic, image)))
        .takeWhile(x => x.duration.getStandardDays < 200 * 365)

  private def makeStreams(mnemonic: String, image: Int, period: Period, isExtraDetailed: Boolean = false): List[Stream[RelativeBirthday]] =
    for (sequence <- if (isExtraDetailed) detailedSequence else notableMultipleSequences)
    yield
      sequence.map(multiple =>
        new PeriodBasedRelativeBirthday(
          period.multipliedBy(multiple.number),
          new BirthdayDescription(multiple.alias + " " + mnemonic, image)))
        .takeWhile(x => x.period.getYears < 200)

  class Planet(val name: String, val yearInEarthDays: Double, val image: Int)

  private val planets = List(
    new Planet("Mercury", 87.96, R.drawable.mercury),
    new Planet("Venus", 224.68, R.drawable.venus),
    new Planet("Mars", 686.98, R.drawable.mars),
    new Planet("Jupiter", 11.862 * yearInDays, R.drawable.jupiter),
    new Planet("Saturn", 29.456 * yearInDays, R.drawable.saturn),
    new Planet("Uranus", 84.07 * yearInDays, R.drawable.uranus),
    new Planet("Neptune", 164.81 * yearInDays, R.drawable.neptune))

  private val planetYearStreams: List[Stream[RelativeBirthday]] =
    planets.flatMap(planet =>
      makeStreamsInDays(planet.name + " years", planet.image, planet.yearInEarthDays))

  val periodStreams: List[Stream[RelativeBirthday]] =
    makeStreams("Earth days", R.drawable.earth, Period.days(1)) ++
      makeStreams("weeks", R.drawable.calendar, Period.weeks(1)) ++
      makeStreams("months", R.drawable.calendar, Period.months(1)) ++
      makeStreams("Earth years", R.drawable.earth, Period.years(1), isExtraDetailed = true) ++
      makeStreamsInDays("lunar months", R.drawable.moon, 29.530588853) ++
      makeStreamsInDays("moon phases", R.drawable.moon, 29.530588853 / 4) ++
      makeStreams("Venus days", R.drawable.venus, Period.days(116) plus Period.hours(18)) ++
      makeStreams("Mercury days", R.drawable.mercury, Period.days(58) plus Period.hours(15) plus Period.minutes(30)) ++
      planetYearStreams
}

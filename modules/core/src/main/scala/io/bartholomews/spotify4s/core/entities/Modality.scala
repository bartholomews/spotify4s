package io.bartholomews.spotify4s.core.entities

import enumeratum.values.{IntEnum, IntEnumEntry}

/**
  * Indicates the modality (major or minor) of a track,
  * the type of scale from which its melodic content is derived.
  * This field will contain a 0 for “minor”, a 1 for “major”, or a -1 for no result.
  * Note that the major key (e.g. C major) could more likely be confused
  * with the minor key at 3 semitones lower (e.g. A minor) as both keys carry the same pitches.
  */
sealed abstract class Modality(val value: Int) extends IntEnumEntry
object Modality extends IntEnum[Modality] {
  case object Minor extends Modality(value = 0)
  case object Major extends Modality(value = 1)
  case object NoResult extends Modality(value = -1)

  override val values: IndexedSeq[Modality] = findValues
}

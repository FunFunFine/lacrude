package io.funfunfine.lacrude.announcement

import io.circe.testing.golden.GoldenCodecTests
import io.circe.testing.instances._

import munit.DisciplineSuite

class AnnouncementGoldenSpec extends DisciplineSuite {
  checkAll("Announcement Codec is correct", GoldenCodecTests[Announcement].goldenCodec)
}

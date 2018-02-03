package it.turingtest.spotify.scala.client.entities

import org.scalatest.{FunSpec, Matchers}
import play.api.libs.json.{JsError, JsString, JsSuccess}

class ItemTypeSpec extends FunSpec with Matchers {

  describe("ItemType") {

    it("should properly read an album ItemType") {
      ItemType.reader.reads(JsString("album")) shouldBe
        JsSuccess(ItemType.Album)
    }

    it("should properly read an artist ItemType") {
      ItemType.reader.reads(JsString("artist")) shouldBe
        JsSuccess(ItemType.Artist)
    }

    it("should properly read an playlist ItemType") {
      ItemType.reader.reads(JsString("playlist")) shouldBe
        JsSuccess(ItemType.Playlist)
    }

    it("should properly read an track ItemType") {
      ItemType.reader.reads(JsString("track")) shouldBe
        JsSuccess(ItemType.Track)
    }

    it("should reject an unknown ItemType") {
      val unknownItem = JsString("unknown")
      ItemType.reader.reads(unknownItem) shouldBe
        JsError(s"Cannot parse ItemType from json '$unknownItem'")
    }

  }

}

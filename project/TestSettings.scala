import sbt.{addCommandAlias, Def}

object TestSettings {
  def apply(): Def.SettingsDefinition =
    addCommandAlias("test-all", ";scalafmtCheckAll ;coverage ;test ;coverageReport") ++
      addCommandAlias("test-fast", "testOnly * -l org.scalatest.tags.Slow")
}

import sbt.{addCommandAlias, Def}

object TestSettings {
  def apply(): Def.SettingsDefinition =
    addCommandAlias("test-coverage", ";coverage ;test ;coverageReport") ++
      addCommandAlias("test-fast", "testOnly * -l org.scalatest.tags.Slow")
}

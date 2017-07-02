package utils

import java.io.File

import loader.Loader
import org.scalatestplus.play._
import play.api.inject.DefaultApplicationLifecycle
import play.api.{Application, ApplicationLoader, Configuration, Environment}
import play.core.DefaultWebCommands

trait AppFactory extends FakeApplicationFactory {

  override def fakeApplication(): Application = {
    val environment = Environment.simple(new File("."))
    val context = ApplicationLoader.Context(
      environment,
      sourceMapper = None,
      webCommands = new DefaultWebCommands(),
      initialConfiguration = Configuration.load(environment) ++ TestConfig(),
      lifecycle = new DefaultApplicationLifecycle()
    )
    new Loader().load(context)
  }

}

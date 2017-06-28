package spring.scala.controller

import org.springframework.web.bind.annotation.{RequestMapping, RequestParam, RestController}

import scala.beans.BeanProperty

/**
  * Created by vamsp7 on 6/26/17.
  */
case class Greeting(@BeanProperty id: Long,
                    @BeanProperty content: String)

@RestController
class GreetingController {

  @RequestMapping(path = Array("/greeting"))
  def greeting(@RequestParam(value = "name", defaultValue = "World") name: String): Greeting = {
    new Greeting(1, s"Hello, $name")
  }
}

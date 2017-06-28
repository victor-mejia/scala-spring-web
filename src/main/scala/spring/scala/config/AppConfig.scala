package spring.scala.config

import org.springframework.context.annotation.{ComponentScan, Configuration}

/**
  * Created by vamsp7 on 6/26/17.
  */
@Configuration
@ComponentScan(Array("spring/scala/controller"))
class AppConfig {

}

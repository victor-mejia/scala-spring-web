package spring.scala.model

import scala.beans.BeanProperty

class User {
  @BeanProperty
  var name: String = null

  @BeanProperty
  var username: String = null

  @BeanProperty
  var password: String = null
}

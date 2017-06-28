package spring.scala.model

import scala.beans.BeanProperty

/**
  * Created by vamsp7 on 6/26/17.
  */
case class Exam(@BeanProperty name: String,
                @BeanProperty description: String,
                @BeanProperty passScore: Double,
                @BeanProperty totalScore: Double,
                @BeanProperty durationSec: Long)

case class Question(@BeanProperty title: String,
                    @BeanProperty problemDesc: String,
                    @BeanProperty multipleAns: Boolean,
                    @BeanProperty order: Int)

case class ChoiseAswer(@BeanProperty text: String,
                       @BeanProperty order: Int,
                       @BeanProperty correct: Boolean)


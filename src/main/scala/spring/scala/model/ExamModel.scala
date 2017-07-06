package spring.scala.model

import java.util.Date
import javax.persistence.{Entity, GeneratedValue, Id}
import java.lang.{Long,Double}


import scala.annotation.meta.field
import scala.beans.BeanProperty

/**
  * Created by vamsp7 on 6/26/17
  */

@Entity
case class ExamDescription(@(Id @field) @(GeneratedValue @field) @BeanProperty id: Long,
               @BeanProperty name: String,
               @BeanProperty description: String,
               @BeanProperty passScore: Double,
               @BeanProperty totalScore: Double,
               @BeanProperty durationSec: Long) {

  def this() = this(null,null,null,null,null,null)
}

@Entity
case class Question(@(Id @field) @BeanProperty id: Long,
                    @BeanProperty examDesc: Long,
                    @BeanProperty title: String,
                    @BeanProperty problemDesc: String,
                    @BeanProperty multipleAns: Boolean,
                    @BeanProperty ord: Int)

@Entity
case class ChoiseAswer(@(Id @field) @BeanProperty id: Long,
                       @BeanProperty question: Long,
                       @BeanProperty text: String,
                       @BeanProperty ord: Int,
                       @BeanProperty correct: Boolean)

@Entity
case class Exam(@(Id @field) @(GeneratedValue @field) @BeanProperty id: Long,
                @BeanProperty examDesc: ExamDescription,
                @BeanProperty user: String,
                @BeanProperty startTime: Date,
                @BeanProperty endTime: Date)

@Entity
case class Answer(@(Id @field) @(GeneratedValue @field) @BeanProperty id: Long,
                  @BeanProperty exam: Long,
                  @BeanProperty question: Long,
                  @BeanProperty choiseAswer: Long)

@Entity
case class ExamGrade(@(Id @field) @(GeneratedValue @field) @BeanProperty id: Long,
                     @BeanProperty exam: Long,
                     @BeanProperty score: Double,
                     @BeanProperty passed: Boolean)

case class ExamStatus(@BeanProperty pendindQuestions: Int,
                      @BeanProperty remainingTime: Long,
                      @BeanProperty startTime: Date,
                      @BeanProperty finished: Boolean)

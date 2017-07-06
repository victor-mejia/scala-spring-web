package spring.scala.services

import java.lang

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import spring.scala.model._
import spring.scala.repository.ExamDescriptionRepository

import scala.collection.JavaConverters._
import scala.util.Try

/**
  * Created by vamsp7 on 6/27/17.
  */
trait ExamService {


  def getExamDescription(): Option[ExamDescription]
  def createExam(user: User): Try[Long]
  def getExam(examId: Long): Option[Exam]
  def questionsByExamSort(examId: Long):  Seq[Question]
  def getExamStatus(examId: Long): Option[ExamStatus]
  def validQuestion(exam: Exam, questionId: Long): Boolean
  def saveAnswers(examId: Long, questionId: Long, answers: Seq[Answer]): Try[Unit]
  def endExam(examId: Long): Try[ExamGrade]
}

@Service
class ExamServiceImpl @Autowired() (val examDescriptionRepository: ExamDescriptionRepository) extends ExamService {

  override def getExamDescription(): Option[ExamDescription] = {

    examDescriptionRepository.findAll()
      .asScala
      .toStream
      .collectFirst { case e@_ => e }
  }
}



package spring.scala.services

import java.lang
import java.util.Date

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import spring.scala.exception.{ExamDescriptionNotFoundException, ExamNotFoundException}
import spring.scala.model._
import spring.scala.repository.{AnswerRepository, ExamDescriptionRepository, ExamRepository, QuestionRespository}

import scala.collection.JavaConverters._
import scala.util.{Failure, Try}

/**
  * Created by vamsp7 on 6/27/17.
  */
trait ExamService {


  def getExamDescription(): Option[ExamDescription]
  def createExam(user: User): Try[Long]
  def getExam(examId: Long): Option[Exam]
  def questionsByExamSort(examId: Long):  Try[Seq[Question]]
  def getExamStatus(examId: Long): Option[ExamStatus]
  def validQuestion(exam: Exam, questionId: Long): Boolean
  def saveAnswers(examId: Long, questionId: Long, answers: Seq[Answer]): Try[Unit]
  def endExam(examId: Long): Try[ExamGrade]
}

@Service
class ExamServiceImpl @Autowired() (val examDescriptionRepository: ExamDescriptionRepository,
                                    val examRepository: ExamRepository,
                                    val questionRepository: QuestionRespository,
                                    val answerRepository: AnswerRepository) extends ExamService {

  override def getExamDescription(): Option[ExamDescription] = {
    examDescriptionRepository.findAll()
      .asScala
      .toStream
      .collectFirst { case e@_ => e }
  }

  override def createExam(user: User): Try[Long] = {

    def saveExam(examDesc: ExamDescription ) = Try(examRepository.save(Exam(null,examDesc.id,user.username)))

    getExamDescription() match {
      case Some(examDesc) => saveExam(examDesc).map(_.id)
      case None => Failure(ExamDescriptionNotFoundException)
    }
  }

  override def getExam(examId: Long): Option[Exam] = Option(examRepository.findOne(examId))

  override def questionsByExamSort(examId: Long): Try[Seq[Question]] = {
    val questionsOp = getExam(examId)
      .map(_.examDescId)
      .map(examDescId => Try(questionRepository.findByExamDesc(examDescId)))

    questionsOp match {
      case Some(questions) => questions.map(_.asScala.toStream)
      case None => Failure(ExamNotFoundException)
    }
  }

  override def getExamStatus(examId: Long): Option[ExamStatus] = {

    def remaningTime(exam:Exam, examDesc: ExamDescription): Long = ???
    def examStatus(totalQuest: Int, answeredQuestions:Int, exam: Exam, examDesc: ExamDescription ): ExamStatus = {
      ExamStatus(totalQuest-answeredQuestions,remaningTime(exam,examDesc),exam.startTime,exam.endTime != null)
    }

    for {
      examDesc <- getExamDescription()
      exam <- Option(examRepository.findOne(examId))
      questions <- Option(questionRepository.findByExamDesc(exam.examDescId)).map(_.asScala.toSet)
      answers <- Option(answerRepository.findByExam(exam.id)).map(_.asScala.toStream)
      answeredQuestions <- Option(answers.map(_.question).toSet)
    } yield examStatus(questions.size,answeredQuestions.size,exam,examDesc)

  }

  override def validQuestion(exam: Exam, questionId: Long): Boolean = ???

  override def saveAnswers(examId: Long, questionId: Long, answers: Seq[Answer]): Try[Unit] = ???

  override def endExam(examId: Long): Try[ExamGrade] = ???
}



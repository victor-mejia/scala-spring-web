package spring.scala.services

import java.util.Date

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import spring.scala.exception.{ExamDescriptionNotFoundException, ExamNotFoundException}
import spring.scala.model._
import spring.scala.repository._

import scala.collection.JavaConverters._
import scala.collection.immutable.Stream.Empty
import scala.util.{Failure, Success, Try}
import java.lang.Long
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
  def saveAnswers(examId: Long, questionId: Long, choiceAnswers: Seq[Long]): Try[Unit]
  def endExam(examId: Long): Try[ExamGrade]
}

@Service
class ExamServiceImpl @Autowired() (val examDescriptionRepository: ExamDescriptionRepository,
                                    val examRepository: ExamRepository,
                                    val questionRepository: QuestionRespository,
                                    val answerRepository: AnswerRepository,
                                    val examGradeRepository: ExamGradeRepository,
                                    val choiceAswerRepository: ChoiseAswerRepository) extends ExamService {

  override def getExamDescription(): Option[ExamDescription] = {
    examDescriptionRepository.findAll()
      .asScala
      .toStream
      .collectFirst { case e@_ => e }
  }

  override def createExam(user: User): Try[Long] = {

    def saveExam(examDesc: ExamDescription) = Try(examRepository.save(Exam(null, examDesc.id, user.username)))

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

    def remaningTime(startDate: Date, durationSec: Long): Long = {
      (durationSec - (new Date().getTime - startDate.getTime) / 1000) match {
        case remaning if remaning > 0 => remaning
        case _ => 0
      }
    }

    def examStatus(totalQuest: Int, answeredQuestions: Int, exam: Exam, examDesc: ExamDescription): ExamStatus = {
      ExamStatus(totalQuest - answeredQuestions, remaningTime(exam.startTime, examDesc.durationSec), exam.startTime, exam.endTime != null)
    }

    for {
      examDesc <- getExamDescription()
      exam <- Option(examRepository.findOne(examId))
      questions <- Option(questionRepository.findByExamDesc(exam.examDescId)).map(_.asScala.toSet)
      answers <- Option(answerRepository.findByExam(exam.id)).map(_.asScala.toStream)
      answeredQuestions <- Option(answers.map(_.question).toSet)
    } yield examStatus(questions.size, answeredQuestions.size, exam, examDesc)
  }

  override def validQuestion(exam: Exam, questionId: Long): Boolean = questionRepository.findOne(questionId) != null

  override def saveAnswers(examId: Long, questionId: Long, choiceAnswers: Seq[Long]): Try[Unit] = {

    def answer(choiceAnswer: Long): Answer = Answer(null, examId, questionId, choiceAnswer)

    Try(answerRepository.save(choiceAnswers.map(answer(_)).asJava))
  }

  override def endExam(examId: Long): Try[ExamGrade] = {

    lazy val answers = answerRepository.findByExam(examId).asScala.toStream

    def presentInAnswers(question: Long, choice: Long): Double = answers.collectFirst {
      case Answer(_, `examId`, `question`, `choice`) => 1d
    }.getOrElse(0d)

    def questionScore(correctChoices: Stream[ChoiceAnswer], scoreAcc: Double): Double = correctChoices match {
      case Empty => 0
      case correct #:: rest => questionScore(rest, scoreAcc + presentInAnswers(correct.question, correct.id))
    }

    def calculateScore(exam: Exam): Double = {
      questionRepository.findByExamDesc(exam.examDescId).asScala.toStream
        .map(question => choiceAswerRepository.findByQuestion(question.id).asScala.toStream.filter(_.correct))
        .map(questionScore(_, 0))
        .foldLeft(0d)(_ + _)
    }

    lazy val examGradeOpt =
      for {
        exam <- getExam(examId).map(_.copy(endTime = new Date())).map(examRepository.save(_))
        examDesc <- getExamDescription()
        score <- Some(calculateScore(exam))
      } yield examGradeRepository.save(ExamGrade(null, exam.id, score, score >= examDesc.passScore))

    Try(examGradeOpt) flatMap {
      _ match {
        case Some(examGrade) => Success(examGrade)
        case None => Failure(ExamNotFoundException)
      }
    }
  }
}

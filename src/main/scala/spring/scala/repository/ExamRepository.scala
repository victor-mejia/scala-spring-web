package spring.scala.repository

import org.springframework.data.repository.CrudRepository
import spring.scala.model._
import java.lang.{Long,Iterable}

/**
  * Created by vamsp7 on 7/3/17.
  */
trait ExamDescriptionRepository extends CrudRepository[ExamDescription,Long]

trait QuestionRespository extends CrudRepository[Question,Long] {
  def findByExamDesc(examDesc: Long): Iterable[Question]
}

trait ChoiseAswerRepository extends CrudRepository[ChoiseAswer,Long]

trait ExamRepository extends CrudRepository[Exam,Long]

trait AnswerRepository extends CrudRepository[Answer,Long] {
  def findByExam(exam: Long): Iterable[Answer]
}

trait ExamGradeRepository extends CrudRepository[ExamGrade,Long]

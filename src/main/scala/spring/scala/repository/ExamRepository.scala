package spring.scala.repository

import org.springframework.data.repository.CrudRepository
import spring.scala.model._
import java.lang.Long

/**
  * Created by vamsp7 on 7/3/17.
  */
trait ExamDescriptionRepository extends CrudRepository[ExamDescription,Long]

trait QuestionRespository extends CrudRepository[Question,Long]

trait ChoiseAswerRepository extends CrudRepository[ChoiseAswer,Long]

trait ExamRepository extends CrudRepository[Exam,Long]

trait AnswerRepository extends CrudRepository[Answer,Long]

trait ExamGradeRepository extends CrudRepository[ExamGrade,Long]

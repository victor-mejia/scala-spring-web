package spring.scala.controller

import com.google.gson.Gson
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation._
import spring.scala.exception.{ExamDescriptionNotFoundException, ExamNotFoundException, ExamOrQuestionNotFoundExeption}
import spring.scala.model._
import spring.scala.services.ExamService

import scala.util.{Failure, Success}

@Controller
@RequestMapping(path = Array("/exam"))
class ExamController @Autowired() (val examService: ExamService) {

  @GetMapping(produces = Array("text/html"))
  def getExamDetails(model: Model): String = {

    model.addAttribute("user", new User) //TODO Remove this replace it with security principal

    examService.getExamDescription() match {
      case Some(examDetails) => model.addAttribute("examDetails", examDetails)
      case None => throw ExamDescriptionNotFoundException
    }
    "home"
  }

  @PostMapping()
  def createExam(@ModelAttribute user: User, model: Model): String = {

    examService.createExam(user) match {
      case Success(examId) => s"redirect:/exam/${examId}"
      case Failure(error) => throw error
    }
  }

  @GetMapping(path = Array("/{examId}"), produces = Array("text/html"))
  def getExam(@PathVariable examId: Long, model: Model): String = {

    examService.getExam(examId) match {
      case Some(exam) =>  setExamToModel(exam, 1, model)
      case None => throw ExamNotFoundException
    }
    "exam"
  }

  @GetMapping(path = Array("{examId}/status"), produces = Array("text/html"))
  def getStatus(@PathVariable examId: Long, model: Model) : String = {

    lazy val lastQuestionId = examService.questionsByExamSort(examId).map(_.last.id)

    def setModel(status: ExamStatus) = {
      model.addAttribute("lastQuestionId", lastQuestionId.get)
      model.addAttribute("status", status)
      model.addAttribute("examId", examId)
    }

    examService.getExamStatus(examId) match {
      case Some(status) => setModel(status)
      case None => throw ExamNotFoundException
    }

    "endExam"
  }

  @GetMapping(path = Array("{examId}/status.json"), produces = Array("text/html"))
  def getStatus(@PathVariable examId: Long) : ResponseEntity[ExamStatus] = {

    examService.getExamStatus(examId) match {
      case Some(status) => ResponseEntity.ok(status)
      case None => throw ExamNotFoundException
    }
  }

  @GetMapping(path = Array("/{examId}/question/{questionId}"), produces = Array("text/html"))
  def getExam(@PathVariable examId: Long, @PathVariable questionId: Long, model: Model): String = {

    examService.getExam(examId) match {
      case Some(exam) if (examService.validQuestion(exam,questionId)) => setExamToModel(exam,questionId,model)
      case None => throw ExamOrQuestionNotFoundExeption
    }
    "exam"
  }

  @PostMapping(path = Array("/{examId}/question/{questionId}/answer"), consumes = Array("application/json"))
  def saveAnswer(@PathVariable examId: Long, @PathVariable questionId: Long, @RequestBody answersJson : String):ResponseEntity[Boolean] = {

    val answers: Seq[Answer] = ??? // TODO answersJson map to array of answers

    examService.saveAnswers(examId,questionId,answers) match {
      case Success(_) => ResponseEntity.ok(true)
      case Failure(error) => throw error
    }
  }

  @PostMapping(path = Array("/{examId}/finish"), produces = Array("application/json"))
  def endExam(examId: Long): ResponseEntity[ExamGrade] = {

    examService.endExam(examId) match {
      case Success(examGrade) => ResponseEntity.ok(examGrade)
      case Failure(error) => throw error
    }
  }

  private def setExamToModel(exam: Exam, currentQuestion: Long, model: Model): Unit = {
    println("Getting questions for exam " + exam.id)

    val examDesc = examService.getExamDescription().getOrElse(null)
    val questionsList = examService.questionsByExamSort(exam.id)
    val gson = new Gson()
    val questions: String = gson.toJson(questionsList)

    model.addAttribute("examDesc", examDesc)
    model.addAttribute("exam", exam)
    model.addAttribute("userFullName", "Victor Mejia")
    model.addAttribute("questions", questions)
    model.addAttribute("currentQuestion", currentQuestion)
  }
}

package spring.scala.controller

import java.util.Date

import com.google.gson.Gson
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation._
import spring.scala.model._
import spring.scala.services.ExamService

@Controller
@RequestMapping(path = Array("/exam"))
class ExamController @Autowired() (val examService: ExamService) {

  @GetMapping(produces = Array("text/html"))
  def getExamDetails(model: Model): String = {

    model.addAttribute("user", new User)

    examService.getExamDescription()
      .map(examDetails => model.addAttribute("examDetails", examDetails))
      .map(_ => "home")
      .getOrElse("notFound")
  }

  @PostMapping()
  def createExam(@ModelAttribute user: User, model: Model): String = {
    val examId = 123;
    s"redirect:/exam/${examId}"
  }

  @GetMapping(path = Array("/{id}"), produces = Array("text/html"))
  def getExam(@PathVariable id: String, model: Model): String = {

    setExamToModel(id, "1", model)

    "exam"
  }

  @GetMapping(path = Array("{id}/status"), produces = Array("text/html"))
  def getStatus(@PathVariable id: String, model: Model) : String = {
    val status = ExamStatus(3,1600,new Date, false)

    model.addAttribute("status", status)
    model.addAttribute("examId", id)
    model.addAttribute("lastQuestionId", 5)

    "endExam"
  }

  @GetMapping(path = Array("{id}/status.json"), produces = Array("text/html"))
  def getStatus(@PathVariable id: String) : ResponseEntity[ExamStatus] = {
    val status = ExamStatus(3,1600,new Date, false)

    ResponseEntity.ok(status)
  }

  @GetMapping(path = Array("/{id}/question/{questionId}"), produces = Array("text/html"))
  def getExam(@PathVariable id: String, @PathVariable questionId: String, model: Model): String = {
    setExamToModel(id,questionId,model)
    "exam"
  }

  @PostMapping(path = Array("/{id}/question/{questionId}/answer"), consumes = Array("application/json"))
  def saveAnswer(@PathVariable id: Int, @PathVariable questionId: Int, @RequestBody answersJson : String):ResponseEntity[Boolean] = {
    ResponseEntity.ok(true)
  }

  @PostMapping(path = Array("/{id}/finish"), produces = Array("application/json"))
  def endExam(): ResponseEntity[ExamGrade] = {
    val examGrade = ExamGrade(1,1,7, true)
    ResponseEntity.ok(examGrade)
  }


  private def setExamToModel(examId: String, currentQuestion: String, model: Model) = {
    println("Getting questions for exam " + examId)
    val exam = new ExamDescription(1,"Exam1", " This is the exam basic level", 6, 10, 3600)

    val questionsList = List(
      Question(1,1,"Question 1", "Problem 1", false, 1),
      Question(2,1,"Question 1", "Problem 1", false, 2),
      Question(3,1,"Question 1", "Problem 1", false, 3),
      Question(4,1,"Question 1", "Problem 1", false, 4),
      Question(5,1,"Question 1", "Problem 1", false, 5));

    val gson = new Gson()

    val questions: String = gson.toJson(questionsList)

    model.addAttribute("exam", exam)
    model.addAttribute("userFullName", "Victor Mejia")
    model.addAttribute("questions", questions)
    model.addAttribute("currentQuestion", currentQuestion)

  }
}

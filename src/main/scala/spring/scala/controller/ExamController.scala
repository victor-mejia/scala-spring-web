package spring.scala.controller

import java.net.URI

import spring.scala.model.Exam
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation._

@RestController
@RequestMapping(path = Array("/api/exam"))
class ExamController {

  @GetMapping()
  def healthCheck():Boolean = true

  @PostMapping()
  def startExam():ResponseEntity[Exam] = {
    val exam = new Exam("Exam1"," This is the exam basic level",6,10,3600)
    ResponseEntity.created(new URI("/exam/1")).body(exam)
  }

  @PutMapping(path = Array("/{id}/question/{questionId}"))
  def saveAnswer(@PathVariable id: Int, @PathVariable questionId: Int):ResponseEntity[Boolean] = {
    ResponseEntity.ok(true)
  }

  @PostMapping(path = Array("/{id}/end"))
  def endExam(): ResponseEntity[String] = {
    val score = 7
    ResponseEntity.ok(s"The exam is over your final score is $score. Congratulations you have passed.")
  }
}

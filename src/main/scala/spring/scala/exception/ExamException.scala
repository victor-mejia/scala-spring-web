package spring.scala.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

/**
  * Created by vamsp7 on 7/5/17.
  */
trait ExamException extends RuntimeException

@ResponseStatus(value=HttpStatus.NOT_FOUND, reason="No exam deatails were found")
object ExamDescriptionNotFoundException extends ExamException

@ResponseStatus(value=HttpStatus.NOT_FOUND, reason="No such exam")
object ExamNotFoundException extends ExamException

@ResponseStatus(value=HttpStatus.NOT_FOUND, reason="No such exam or invalid question")
object ExamOrQuestionNotFoundExeption extends ExamException

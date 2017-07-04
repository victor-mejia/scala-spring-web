package spring.scala.services

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import spring.scala.model.ExamDescription
import spring.scala.repository.ExamDescriptionRepository

import scala.collection.JavaConverters._

/**
  * Created by vamsp7 on 6/27/17.
  */
trait ExamService {

  def getExamDescription(): Option[ExamDescription]
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



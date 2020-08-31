package services

import javax.inject._
import model._
import scala.concurrent.Future

@Singleton
class UserService @Inject() (protected val userRepository: UserRepository) {

  def addUser(user: User): Future[String] = {
    userRepository.add(user)
  }

  def deleteUser(id: Long): Future[Int] = {
    userRepository.delete(id)
  }

  def getUser(id: Long): Future[Option[User]] = {
    userRepository.get(id)
  }

  def listAllUsers: Future[Seq[User]] = {
    userRepository.listAll
  }
}
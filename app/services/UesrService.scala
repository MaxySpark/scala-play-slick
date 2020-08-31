package services

import javax.inject._
import model._
import scala.concurrent.Future

@Singleton
class UserService @Inject() (protected val userRepository: UserRepository) {

  def addUser(user: UserSchema.User): Future[String] = {
    userRepository.add(user)
  }

  def deleteUser(id: Long): Future[Int] = {
    userRepository.delete(id)
  }

  def getUser(id: Long): Future[Option[UserSchema.User]] = {
    userRepository.get(id)
  }

  def listAllUsers: Future[Seq[UserSchema.User]] = {
    userRepository.listAll
  }
}
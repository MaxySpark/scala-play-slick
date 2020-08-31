package model

import javax.inject._
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.{JsValue, Json, OFormat}

import scala.concurrent.{ExecutionContext, Future}
import slick.jdbc.JdbcProfile
import slick.jdbc.MySQLProfile.api._

object UserSchema {
  case class User(id: Long, firstName: String, lastName: String, mobile: Long, email: String) {
    def toPublic = UserPublic(id, firstName, lastName, mobile, email)
  }

  object UserObject {
    implicit val format: OFormat[User] = Json.format[User]
  }

  class UserTable(tag: Tag) extends Table[User](tag, "user") {

    def id = column[Long]("id", O.PrimaryKey,O.AutoInc)
    def firstName = column[String]("first_name")
    def lastName = column[String]("last_name")
    def mobile = column[Long]("mobile")
    def email = column[String]("email")

    override def * = (id, firstName, lastName, mobile, email).mapTo[User]
  }

  val users = TableQuery[UserTable]

}

@Singleton
class UserRepository @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

  val dbConfig = dbConfigProvider.get[JdbcProfile]

  val users = TableQuery[UserSchema.UserTable]

  def add(user: UserSchema.User): Future[String] = {
    dbConfig.db.run(users += user).map(res => "User successfully added").recover {
      case ex: Exception => ex.getCause.getMessage
    }
  }

  def delete(id: Long): Future[Int] = {
    dbConfig.db.run(users.filter(_.id === id).delete)
  }

  def get(id: Long): Future[Option[UserSchema.User]] = {
    dbConfig.db.run(users.filter(_.id === id).result.headOption)
  }

  def listAll: Future[Seq[UserSchema.User]] = {
    dbConfig.db.run(users.result)
  }

}

case class UserPublic(
                       id: Long,
                       firstName: String,
                       lastName: String,
                       mobile: Long,
                       email: String
                     ) {
  def toJsValue: JsValue = Json.toJson(this)
  override def toString: String = toJsValue.toString()
}

object UserPublic {
  implicit val format: OFormat[UserPublic] = Json.format[UserPublic]
}
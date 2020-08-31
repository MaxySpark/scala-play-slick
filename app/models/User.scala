package model

import javax.inject._
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.{JsValue, Json, OFormat, Format, JsString, JsSuccess, JsValue, Json, OFormat}

import scala.concurrent.{ExecutionContext, Future}
import slick.jdbc.JdbcProfile
import slick.jdbc.MySQLProfile.api._
import java.sql.Timestamp
import java.text.SimpleDateFormat
import slick.sql.SqlProfile.ColumnOption.SqlType

object UserSchema {

  case class User(id: Long, first_name: String, last_name: String, mobile: Long, email: String, createdAt: Timestamp, updatedAt: Timestamp) {
    implicit object timestampFormat extends Format[Timestamp] {
      val format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS'Z'")
      def reads(json: JsValue) = {
        val str = json.as[String]
        JsSuccess(new Timestamp(format.parse(str).getTime))
      }
      def writes(ts: Timestamp) = JsString(format.format(ts))
    }
    def toPublic = UserPublic(id, first_name, last_name, mobile, email, createdAt, updatedAt)
  }
  object UserObject {
    implicit val format: OFormat[User] = Json.format[User]
  }

  class UserTable(tag: Tag) extends Table[User](tag, "user") {

    def id = column[Long]("id", O.PrimaryKey)

    def first_name = column[String]("first_name")

    def last_name = column[String]("last_name")

    def mobile = column[Long]("mobile")

    def email = column[String]("email")

    def createdAt = column[Timestamp]("createdAt", SqlType("timestamp not null default CURRENT_TIMESTAMP"))

    def updatedAt = column[Timestamp]("updatedAt", SqlType("timestamp not null default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP"))

    def * = (id, first_name, last_name, mobile, email, createdAt, updatedAt).mapTo[User]


  }

  val users = TableQuery[User]
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
                       first_name: String,
                       last_name: String,
                       mobile: Long,
                       email: String,
                       createdAt: Timestamp,
                       updatedAt: Timestamp
                     ) {
  def toJsValue: JsValue = Json.toJson(this)
  override def toString: String = toJsValue.toString()
}

object UserPublic {
  implicit object timestampFormat extends Format[Timestamp] {
    val format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS'Z'")
    def reads(json: JsValue) = {
      val str = json.as[String]
      JsSuccess(new Timestamp(format.parse(str).getTime))
    }
    def writes(ts: Timestamp) = JsString(format.format(ts))
  }
  implicit val format: OFormat[UserPublic] = Json.format[UserPublic]
}


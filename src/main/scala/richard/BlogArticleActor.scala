package richard

import akka.cluster.sharding.ShardRegion
import akka.persistence.{PersistentActor, SnapshotOffer}

case class BlogArticleState(title: String, body: String)

class BlogArticleActor extends PersistentActor {
  import richard.BlogArticleActor._

  var state: BlogArticleState = BlogArticleState("", "")

  override def persistenceId: String = self.path.name


  override def preStart(): Unit = {
    super.preStart()
    println("#################################")
    println("start start start prestart!!!!1")
    println(self.path)
  }

  val receiveRecover: Receive = {
    case CreatedArticleEvt(_, title, body) =>
      state = BlogArticleState(title, body)
    case UpdatedArticleEvt(_, title, body) =>
      state = BlogArticleState(title, body)
    case a =>
      println("$$$$$$$$$$$$$$$$$$$$$ unexpected message in receiveRecover " + a)
      println(a)
  }

  val receiveCommand: Receive = {
    case CreateArticleCmd(id, title, body) =>
      println(s"CreateArticleCmd(${id}, ${title}, ${body}) received")
      persist(CreatedArticleEvt(id, title, body)) { event =>
        state = BlogArticleState(event.title, event.body)
        println(s"CreateArticleEvt(${id}, ${title}, ${body}) processed")
      }
    case UpdateArticleCmd(id, title, body) =>
      println(s"CreateArticleCmd(${title}, ${body}) received")
      persist(UpdatedArticleEvt(id, title, body)) { event =>
        state = BlogArticleState(event.title, event.body)
        println(s"CreateArticleEvt(${title}, ${body}) processed")
      }
    case a =>
      println("$$$$$$$$$$$$$$$$$$$$$ unexpected message in receiveCommand " + a)
      println(a)
  }
}

object BlogArticleActor {
  sealed trait Command
  case class CreateArticleCmd(id: String, title: String, body: String) extends Command
  case class UpdateArticleCmd(id: String, title: String, body: String) extends Command

  sealed trait Event
  case class CreatedArticleEvt(id: String, title: String, body: String) extends Event
  case class UpdatedArticleEvt(id: String, title: String, body: String) extends Event

  val numberOfShards = 100
  val extractEntityId: ShardRegion.ExtractEntityId = {
    case msg @ CreateArticleCmd(id, _, _) => (id, msg)
    case msg @ UpdateArticleCmd(id, _, _) => (id, msg)
  }
  val extractShardId: ShardRegion.ExtractShardId = {
    case CreateArticleCmd(id, _, _) => (id.hashCode % numberOfShards).toString
    case UpdateArticleCmd(id, _, _) => (id.hashCode % numberOfShards).toString
  }
}
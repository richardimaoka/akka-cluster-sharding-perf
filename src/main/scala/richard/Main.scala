package richard

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.cluster.sharding.{ClusterSharding, ClusterShardingSettings}
import com.typesafe.config.ConfigFactory

object Main {

  def main(args: Array[String]): Unit = {
    val port = if(args.length == 1) {
      try {
        args(0).toInt
      } catch {
        case _: NumberFormatException => 2551
      }
    } else {
      2551
    }

    val config = ConfigFactory
      .parseString("akka.remote.netty.tcp.port=" + port)
      .withFallback(ConfigFactory.load())

    val system = ActorSystem("richard", config)
    //val cluster = Cluster(system)

    try {
      val deviceRegion: ActorRef = ClusterSharding(system).start(
        typeName = "BlogArticleActor",
        entityProps = Props[BlogArticleActor],
        settings = ClusterShardingSettings(system),
        extractEntityId = BlogArticleActor.extractEntityId,
        extractShardId = BlogArticleActor.extractShardId
      )

      val ref = system.actorOf(Props[BlogArticleActor])

      ref ! BlogArticleActor.UpdateArticleCmd("bbb", "title", "string")

      if(port == 2560) {
        deviceRegion ! BlogArticleActor.CreateArticleCmd("aaa", "title", "string")
        deviceRegion ! BlogArticleActor.CreateArticleCmd("bbb", "title", "string")
        deviceRegion ! BlogArticleActor.CreateArticleCmd("ccc", "title", "string")
        deviceRegion ! BlogArticleActor.CreateArticleCmd("ddd", "title", "string")

        deviceRegion ! BlogArticleActor.UpdateArticleCmd("bbb", "title", "string")
        deviceRegion ! BlogArticleActor.UpdateArticleCmd("ccc", "title", "string")
        deviceRegion ! BlogArticleActor.UpdateArticleCmd("ddd", "title", "string")
      }
    } catch {
      case e: Throwable => {
        println("----------------------")
        println(e)
        system.terminate()
      }
    }
  }
}

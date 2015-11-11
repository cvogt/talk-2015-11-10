package ai.x.example
import ai.x.lens._

object Example{
  def main(args: Array[String]): Unit = {  
    println("Starting")
    println("-"*80)
    println()

    trait Indirect[T]{
      def map[R](f: T => R): Indirect[R]
      def compose[R](other: Indirect[R]): Indirect[(T,R)] = ???
      //def flatMap( f: T => Indirect[R] ): Indirect[R]
    }

    case class IndirectValue[T](i : T) extends Indirect[T]{
      def map[R](f: T => R): IndirectValue[R] = IndirectValue(f(i))
    }

    case class FakeIndirect[T]() extends Indirect[T]{
     def map[R](f: T => R): FakeIndirect[R] = FakeIndirect[R]()
    }

    /*
    printIndirect( IndirectValue( 5 ) )
    
    printIndirect( FakeIndirect[Int] )

    def printIndirect(i: Indirect[Int]) = {
      println( i.map(_ + 10) )    
    }
    */

    //IndirectValue(5).compose( IndirectValue("Test") ): Indirect[(Int, String)]
    

    // Scalaz after here

    import scalaz._
    import Scalaz._
    import scala.concurrent._
    import scala.concurrent.duration._
    import scala.concurrent.ExecutionContext.Implicits.global

    def delayedValue[T](v: T) = Future{
      Thread.sleep(2000)
      v
    }

    val iCached = delayedValue(5)
    val sCached = delayedValue("test")
    //println(new java.util.Date)
    val f1 = for{
      v1 <- iCached
      v2 <- sCached
    } yield (v1, v2)

    /*
    printFuture(f1)
    println(new java.util.Date)

    printFuture(
      ( delayedValue(5) |@| delayedValue("Test") )( (l,r) => (l,r) )
    )

    println(new java.util.Date)

    def printFuture[T](f: Future[T] ) = 
      println(
        Await.result(
          f,
          Duration.Inf
        )
      )
    */

    trait MySerialize[T]{
      def serialize(v: T): String
    }
    implicit object IntSerialize extends MySerialize[Int]{
      def serialize(v: Int) = s"(Int:$v)"
    }
    implicit def OptionSerialize[T](implicit s: MySerialize[T]) = new MySerialize[Option[T]]{
      def serialize(v: Option[T]) = "(Option:"+v.map(
        value => s.serialize(value)
      ).getOrElse("None") +")"
    }




    def serializeAll[T](v: T)(implicit s: MySerialize[T]) = s.serialize(v)

    /*println(
      serializeAll(Option(5))
    )*/

    case class Address(street: String)
    case class Person(name: String, address: Address)    

    val p = Person("Chris", Address("Baker Street"))

    val p2 = p.lens(_.address.street).set("Park Lane")
              .lens(_.name).modify(_ + " Vogt")



    //println(p2)

    for{
      v1 <- Option(5)
      _ = println(v1)
      v2 <- (None: Option[Int])
      v3 <- Option(10)
    } yield v1 + v2 + v3


    /*
    object OptionSerialize[T] extends MySerialize[Option[T]]{
      def serialize(v: Int) = s"(Int:$v)"
    }*/

    println()
    println("-"*80)
    println("Done!")
  }
}



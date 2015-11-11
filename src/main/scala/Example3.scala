package ai.x.example
import ai.x.lens._

object Example3{
  def _main(args: Array[String]): Unit = {  
    println("Starting")
    println("-"*80)
    println()

    abstract class Indirect[T]{
      def map[R]( f: T => R ): Indirect[R]
      def flatMap[R]( f: T => Indirect[R] ): Indirect[R] = ???
      def compose[R]( other: Indirect[R] ): Indirect[(T,R)]
    }
    case class IndirectValue[T]( i: T ) extends Indirect[T]{
      def map[R]( f: T => R ): IndirectValue[R] = IndirectValue(f(i)) 
      def compose[R]( other: Indirect[R] ): Indirect[(T,R)] = ???
    }
    case class FakeIndirect[T]() extends Indirect[T]{
      def map[R]( f: T => R ): FakeIndirect[R] = FakeIndirect[R]
      def compose[R]( other: Indirect[R] ): FakeIndirect[(T,R)] = FakeIndirect[(T,R)]
    }

    val i = IndirectValue(5)
    val i2 = FakeIndirect[Int]()

    def doIndirect(d: Indirect[Int]) = println( d.map(_ + 10) )

    IndirectValue(IndirectValue(5))

    //doIndirect(i)
    //doIndirect(i2)

    // Scalaz after here

    import scalaz._
    import Scalaz._
    import scala.concurrent._
    import scala.concurrent.duration._
    import scala.concurrent.ExecutionContext.Implicits.global

    Option(Option(5)).flatten === Option(5)

    def futurePrint[T](f: Future[T]) = {
      println(
        Await.result(
          f,
          Duration.Inf
        )
      )
    }

    def delayedFuture[T](v: T) = Future{ Thread.sleep(2000); v }

    //println(new java.util.Date)
    val delayedInt = delayedFuture(5)
    val delayedString = delayedFuture("Test")
    val f1 = for{
      i <- delayedInt
      s <- delayedString
    } yield (i,s)
    //futurePrint(f1)
    
    //println(new java.util.Date)

    //val f2 = ( delayedFuture(5) |@| delayedFuture("Test") )( (l,r) => (l,r) )
    val f2 = delayedFuture(5).flatMap( i => delayedFuture("Test").map( s => (i,s) ) )
    //futurePrint(f2)
    //println(new java.util.Date)


    val ff1 = (i: Int) => i*i
    val ff2 = (i: Int) => s"The square of $i is: "

    val square: (Int => String) = for{
      v1 <- ff1
      v2 <- ff2
    } yield v2 + v1.toString

    //println( square(1) )
    //println( square(2) )
    //println( square(3) )


    trait MySerializable[T]{
      def serialize(t: T): String
    }

    implicit object IntSerializable extends MySerializable[Int]{
      def serialize(t: Int): String = "(Int:"+t.toString+")"
    }
    implicit object DoubleSerializable extends MySerializable[Double]{
      def serialize(t: Double): String = "(Double:"+t.toString+")"
    }
    implicit def OptionSerializable[T](implicit s: MySerializable[T]) = new MySerializable[Option[T]]{
      def serialize(t: Option[T]): String = s"(Option:"+t.map(
        value => serializeAll(value)
      ).getOrElse("None")+")"
    }

    def serializeAll[T](v: T)( implicit s: MySerializable[T] ) = s.serialize(v)

    //println( serializeAll(Option(6)) )


    /*
    println(
      Option(5) |+| (None:Option[Int])
    )
    */

    val res = for{
      v1 <- Option(5)
      v2 <- (None:Option[Int])
    } yield v1 + v2


    //println(res)


    case class Address(street: String)
    case class Person(name: String, address: Address)
    val p = Person("Chris", Address("Wall Street"))
    
    println(p)

    println(p.lens(_.address.street).set("Broadway"))

    println()
    println("-"*80)
    println("Done!")
  }
}



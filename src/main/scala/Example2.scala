package ai.x.example
import ai.x.lens._

case class Person(age: Int, name: String)
case class Address(street: String, owner: Person)

trait Transformation[T]{  
  def map[R](f: T => R): Indirect[R]
}
trait Flattenable[T]{
  //def flatten[O](implicit ev: T =:= Indirect[O]): Flattenable[O]
}
trait Composable[T]{
  def compose[O](other: Indirect[O]): Indirect[(T,O)]
}
trait Indirect[T] extends Transformation[T] with Flattenable[T] with Composable[T]{
  def doit(f: T => Unit): Unit
}

case class IndirectValue[T](i: T) extends Indirect[T]{
  def doit( f: T => Unit ): Unit = f(i)
  def map[R](f: T => R) = new IndirectValue[R]( f(i) )
  def compose[O](other: Indirect[O]): Indirect[(T,O)] = ???
}

object Indirect{
  def print3times[T](i: Indirect[T]) = {
    i.doit(println)
    i.doit(println)
    i.doit(println)
  }  
  def square3times(i: Indirect[Int]) = {
    i.map(x => x * x).map(x => x * x).map(x => x * x)
  }  
}

class FakeIndirectValue[T] extends Indirect[T]{
  def doit( f: T => Unit ): Unit = ()
  def map[R](f: T => R ) = new FakeIndirectValue[R]
  //def flatten[O](implicit ev: T =:= FakeIndirectValue[O]): FakeIndirectValue[O] = new FakeIndirectValue[O]
  def compose[O](other: Indirect[O]): Indirect[(T,O)] = ???
}

object Example2{
  def _main(args: Array[String]): Unit = {  
    println("Starting")
    
    val i = new IndirectValue(5)
    //i.doit( println )

    //Indirect.print3times(i)
    //Indirect.print3times(FakeIndirectInt)

    //Indirect.square3times(i).doit(println)

    /*(
      IndirectValue(5) compose IndirectValue(5)
    ).map{
      case (left, right) => left * right
    }.doit(println)*/

    //FakeIndirectInt.doit( println )

    /*

    val a: Address = Address("123 Bane", Person(99, "Chris"))
    val a2: Address = a.lens(_.owner.name).set("Jan Chris")
    println(a2)
    assert(a2.owner.name == "Jan Chris")
    */

    implicit def IntPlus(l: Int, r: Int) = l + r

    println(
      implicitly[(Int,Int) => Int].apply(6,5)
    )

    // Scalaz after here

    import scalaz._
    import Scalaz._
    import scala.concurrent._
    import scala.concurrent.duration._
    import scala.concurrent.ExecutionContext.Implicits.global

    case class Stats(i: Int, s: String)

    implicit val statsMonoid = new Semigroup[Stats]{
      def append(l: Stats, r: => Stats): Stats = Stats(l.i+r.i, l.s+";"+r.s )
    }

    println(
      Option(Stats(1,"A")) |+| Option(Stats(2,"B")) |+| None
    )

    println(
      List( Option(Stats(1,"A")), Option(Stats(2,"B")), None )
    )

    
    println( new java.util.Date )
    val res = Await.result(
      (Future{Thread.sleep(5000);3} |@| Future{Thread.sleep(5000);5})( _ * _ ),
      Duration.Inf
    )
    println( new java.util.Date )

    println(res)

    val f = for{
      result <- ((i: Int) => i*i)
      label <- ((i: Int) => s"The suqare of $i is: ")
    } yield label + result

    println(f(1))
    println(f(2))
    println(f(3))

    println("Done!")
  }
}



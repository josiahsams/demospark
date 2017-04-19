

// Scala is a pure Object oriented language
val x = "hello"     // Implicit Conversion
// x = 1            // Compilation Error: Static Typed language
var xyz = 1234
xyz = 3456

// Method Definition
def sqr1(x: Int): Int = {
  x * x
}


// Assign Function as variable
val sqr2 = (in: Int) => {
  in * in
}

// Scala as a Functional Programming language
// Functions as first class citizens
// Takes Function as argument
def mathOperation(f: (Int) => Int, x: Int) = {
  f(x)
}

mathOperation(sqr2, 20)


// Syntactic Sugaring

1.+(2)    // same as  1 + 2

object test {
  val listObj = scala.collection.mutable.Map[Int,String]()
  def apply(x: Int) = { listObj.get(x).head  }
  def update(x: Int, value: String) = { listObj.update(x, value) }
}

test(1) = "Joe"     // same as test.update(1, "Joe")
println(test(1))    // same as test.apply(1)

var y = 1
y += 3              // same as y = y.+(3)







// Function returning Function
def addPrefix(prefix: String) =  (s: String) => {
  prefix + "_" + s
}
val withPrefix = addPrefix("Joe")
println(withPrefix("Sam"))




// Function Currying
def addPrefix1(prefix: String)(s: String) = {
  prefix + "_" + s
}
val withPrefix1 = addPrefix1("Joe1")_

println(withPrefix1("Sam1"))





// Case Class
case class Person(name: String, desc: String)

val p1 = Person("joe", "one")
val p2 = Person("sam", "two")

// Pattern Matching & Decomposing
def catchFunc(obj: Any) = obj match {
  case Person(x, _) => println("Caught " + x)
  case _ => println("unknown type")
}

catchFunc(p1)

// Singleton Objects
object IdFactory {
  private var counter = 0
  def create(): Int = {
    counter += 1
    counter
  }
}

println(IdFactory.create())
println(IdFactory.create())


// Class with Constructors & default values
class Point(var x: Int = 0, var y: Int = 0)


// Traits
trait Greeter {
  def greet(name: String): Unit
}

class CustomizableGreeter(prefix: String) extends Greeter {
  override def greet(name: String): Unit = {
    println(prefix + " " + name)
  }
}

val obj1 = new CustomizableGreeter("Hello")
obj1.greet("Joe")




// Looping

val listOfItems = List.range(1, 10, 2)

val modList = listOfItems.map(x => x * 2)

modList.foreach(println)

def foo(n: Int, v: Int) =
  for (i <- 0 until n;
       j <- 0 until v; if j % 2 == 0 ) yield
    (i, j)

foo(3, 3) foreach {
  case (i, j) =>
    println(s"($i, $j)")
}


// Implicit parameters passing
case class Source(sourceName : String){
  def sendText(t:String) = {
    println(s"Text is coming from $sourceName and its value is $t")
  }
}

def communicateFrom1(t:String)(implicit s:Source) = s.sendText(t)

implicit val defaultSource = Source("default source")

communicateFrom1("1st message")

class Class1 {
  def func1 = {
    println("Called Function1")
  }
}

class Class2 {

}

implicit def converter(obj: Class2): Class1 = {
  new Class1
}
val obj3 = new Class2
obj3.func1



// Collection APIs

// Arrays
val arr = Array(1, 2, 3, 4,5)
arr(2)
arr(2) = 30
arr(2)

// List
val list1 = List(1,2 ,3, 4, 5)
list1(2)
// list1(2) = 30

// Set
val set1 = Set(1,1,1,2,3,3,3,4,5)
set1.foreach(print)

// Maps
val maps = Map(1 -> "joe", 2 -> "sam")
maps.get(1)

// Option
maps.get(1) match {
  case Some(x) => println(s"Value is $x")
  case None => println("No value found")
}

println(maps.getOrElse(10, "Not found"))



// Tuples

val tup = ("Something", 1234)
println(s"Values are ${tup._1} and ${tup._2}")

tup match {
  case (x, y) => println (s"Values are $x and $y")
}


// Collection APIs
val numbers = List(1, 2, 3, 4, 5, 6, 7, 8)

// map
numbers.map((i: Int) => i * 2)

// foreach
numbers.foreach((i: Int) => i * 2)

// filter
numbers.filter((i: Int) => i % 2 == 0)


List(1, 2, 3).zip(List("a", "b", "c"))
// partition
numbers.partition(_ % 2 == 0)

// drop
numbers.drop(5)


// foldleft - accumulators
numbers.foldLeft(0)((m: Int, n: Int) => m + n)


// flatten
List(List(1, 2), List(3, 4)).flatten

val nestedNumbers = List(List(1, 2), List(3, 4))

// flatmap = map + flatten
nestedNumbers.flatMap(x => x.map(_ * 2))


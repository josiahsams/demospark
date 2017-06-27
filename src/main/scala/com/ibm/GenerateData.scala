package com.ibm

import java.io.{File, PrintWriter}

import scala.util.Random

/**
  * Created by joe on 5/15/17.
  */
object GenerateData {

  def generateRand(seed: Long, D: Int, scale: Double) = {
    val r = new Random(seed)

    val y = if (seed % 2 == 0) 0 else 1
    val x = Array.fill(D){r.nextGaussian() + (y * scale)}
    // val x = Array.fill(D){ (r.nextGaussian() *y) + scale}

    (x, y)
  }

  def generateSeq(seed: Long, D: Int, scale: Double): (Array[Double],Int) = {
    val r = new Random(seed)

    val y = if (seed < 50) 0 else 1
    val x = Array.fill(D){  seed*scale}
    // val x = Array.fill(D){ (r.nextGaussian() *y) + scale}

    (x, y)
  }

  def mean(arr: Array[Double]) = {
    val sum = arr.foldLeft[Double](0)( (acc: Double, value: Double) => acc + value )

    sum / arr.length
  }

  def variance(arr: Array[Double]) = {
    val m = mean(arr)

    val tmp = arr.foldLeft[Double](0)(
      (acc: Double, value: Double) => acc + (value - m)*(value -m))

    tmp / arr.length
  }

  def stdev(arr: Array[Double]) = {
    Math.sqrt(variance(arr))
  }

  def main(args: Array[String]) = {

    val pw = new PrintWriter(new File("/Users/josiahsams/Desktop/rawdata02"))
    (1 to 1000).map(i => generateSeq(i, 2, 0.7))
      .foreach{
        case (x: Array[Double], y: Int) => {
          x.foreach(x1 => {
            pw.write(x1.toString)
            pw.write(" ")
          })
          pw.println(y)
        }
      }

      pw.close()

//    val r = new Random(42)
//    val input = Array.fill[Double](100){ r.nextGaussian() }
//
//    val pw = new PrintWriter(new File("/Users/josiahsams/Desktop/rawinputdata"))
//    input.foreach(row => pw.println(row))
//    pw.close
//    println (mean(input))
//    println (variance(input))
//    println(stdev(input))



//    val numElements = 10
//    val hostInputA = Array.tabulate[Float](numElements){i:Int => i }
//    val hostInputB = Array.tabulate[Float](numElements){i:Int => i }
//
//    val output = for (i <- 0 to numElements - 1)
//        yield { hostInputA(i) + hostInputB(i) }
//
//    output.foreach(println)
  }

}

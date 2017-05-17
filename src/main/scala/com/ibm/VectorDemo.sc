import breeze.linalg._


// Dense Vector - 5 x 1
val X = new DenseVector(Array(1.1, 1.2, 1.3, 1.4, 1.5))

// Dense Matrix - 5 x 5
val Y = DenseMatrix.eye[Double](5)

// Dot Product - 5 x 1
val Z = Y * X

// 1 x 5
val ZZ = X.t * Y

// Sparse Vector
val sX = new SparseVector[Double](Array(1,3,5), Array(1.1,1.3,1.5), 5)

val const: Double = 10

val cZ = Y * const

// in place multiplication
X :*= const




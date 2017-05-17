package com.ibm

import jcuda.driver.JCudaDriver._
import jcuda.driver._
import org.apache.commons.io.IOUtils
import jcuda.Pointer
import jcuda.Sizeof
import jcuda.driver.CUdeviceptr

/**
  * Created by joe on 5/15/17.
  */
object JCudaSample {

  def main(args: Array[String]): Unit = {
    val ptxURL = getClass.getResource("JCudaVectorAddKernel.ptx")

    JCudaDriver.setExceptionsEnabled(true)

    // Initialization
    JCudaDriver.cuInit(0)

    // Context Creation
    val device: CUdevice = new CUdevice
    cuDeviceGet(device, 0)
    val context: CUcontext = new CUcontext
    cuCtxCreate(context, 0, device)

    // Load Kernel Modules
    val inputStream = ptxURL.openStream()
    val moduleBinaryData = IOUtils.toByteArray(inputStream)
    inputStream.close()
    val moduleBinaryData0 = new Array[Byte](moduleBinaryData.length + 1)
    System.arraycopy(moduleBinaryData, 0, moduleBinaryData0, 0, moduleBinaryData.length)
    moduleBinaryData0(moduleBinaryData.length) = 0
    val module = new CUmodule
    JCudaDriver.cuModuleLoadData(module, moduleBinaryData0)

    // Get reference to kernel
    val funcName="add"
    val function = new CUfunction
    cuModuleGetFunction(function, module, funcName)

    // Input Data Creation
    val numElements = 100000

    // Allocate and fill the host input data
    val hostInputA = Array.tabulate[Float](numElements){i:Int => i }
    val hostInputB = Array.tabulate[Float](numElements){i:Int => i }

    // Allocate the device input data, and copy the
    // host input data to the device
    val deviceInputA = new CUdeviceptr
    cuMemAlloc(deviceInputA, numElements * Sizeof.FLOAT)
    cuMemcpyHtoD(deviceInputA, Pointer.to(hostInputA), numElements * Sizeof.FLOAT)

    val deviceInputB = new CUdeviceptr
    cuMemAlloc(deviceInputB, numElements * Sizeof.FLOAT)
    cuMemcpyHtoD(deviceInputB, Pointer.to(hostInputB), numElements * Sizeof.FLOAT)

    // Allocate device output memory
    val deviceOutput = new CUdeviceptr
    cuMemAlloc(deviceOutput, numElements * Sizeof.FLOAT)

    // Set up the kernel parameters: A pointer to an array
    // of pointers which point to the actual values.
    val kernelParameters = Pointer.to(
      Pointer.to(Array[Int](numElements)),
      Pointer.to(deviceInputA),
      Pointer.to(deviceInputB),
      Pointer.to(deviceOutput))

    // Call the kernel function.
    val blockSizeX = 256
    val gridSizeX = Math.ceil(numElements.toDouble / blockSizeX).toInt
    cuLaunchKernel(function,
      gridSizeX, 1, 1, // Grid dimension
      blockSizeX, 1, 1, // Block dimension
      0, null, // Shared memory size and stream
      kernelParameters, null) // Kernel- and extra parameters

    // Allocate host output memory and copy the device output
    // to the host.
    val hostOutput = new Array[Float](numElements)
    cuMemcpyDtoH(Pointer.to(hostOutput), deviceOutput, numElements * Sizeof.FLOAT)


    val output = for (i <- 0 to numElements -1 )
      yield { hostInputA(i) + hostInputB(i) }

    assert(hostOutput.sameElements(output))


    // Clean up.
    cuMemFree(deviceInputA)
    cuMemFree(deviceInputB)
    cuMemFree(deviceOutput)

  }
}

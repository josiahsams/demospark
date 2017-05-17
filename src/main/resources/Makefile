CUDA_PATH ?= /usr/local/cuda
CXX ?= g++
NVCC ?= $(CUDA_PATH)/bin/nvcc
COMPUTE_CAPABILITY ?= 35
CXXFLAGS ?= -m64 -O3 -Xcompiler -Wall --std=c++11 -g
NVCCFLAGS ?= -arch=sm_$(COMPUTE_CAPABILITY) -Xptxas="-v"


all : JCudaVectorAddKernel.ptx

JCudaVectorAddKernel.ptx: JCudaVectorAddKernel.cu
	$(NVCC) -ccbin $(CXX) $(CXXFLAGS) $(NVCCFLAGS) -ptx -o $@ -c $^

clean:
	rm -f JCudaVectorAddKernel.ptx

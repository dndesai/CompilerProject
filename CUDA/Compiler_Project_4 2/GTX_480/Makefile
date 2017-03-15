PROGS= norm opt1 opt2 opt3
SM=
norm: norm.cu
	nvcc -o $@ $^ $(SM)
opt1: opt1.cu
	nvcc -o $@ $^ $(SM)
opt2: opt2.cu
	nvcc -o $@ $^ $(SM)
opt3: opt3.cu
	nvcc -o $@ $^ $(SM)
optAll: optAll.cu
	nvcc -o $@ $^ $(SM)

clean:
	rm $(PROGS)
# Reference commandsfor compiling and running (i.e. with 4 processes) MPI programs
mpicc ./src/filter.c -o filter.out
mpirun -np 4 filter.out
#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <mpi.h>

// Creates an array of random numbers
int *create_random_array(int num_elements, int max_value)
{
  int *arr = (int *)malloc(sizeof(int) * num_elements);
  for (int i = 0; i < num_elements; i++)
  {
    arr[i] = (rand() % max_value);
  }
  return arr;
}

// Computes the average value
float compute_average(int *array, int num_elements)
{
  int sum = 0;
  for (int i = 0; i < num_elements; i++)
  {
    sum += array[i];
  }
  return ((float)sum) / num_elements;
}

// Computes final average value
float compute_final_average(float *array, int num_elements)
{
  float sum = 0.0f;
  for (int i = 0; i < num_elements; i++)
  {
    sum += array[i];
  }
  return sum / num_elements;
}

int main(int argc, char **argv)
{
  // Number of elements for each processor
  int num_elements_per_proc = 1000;
  if (argc > 1)
  {
    num_elements_per_proc = atoi(argv[1]);
  }

  // Init random number generator
  srand(time(NULL));

  MPI_Init(NULL, NULL);

  int my_rank, world_size;
  MPI_Comm_rank(MPI_COMM_WORLD, &my_rank);
  MPI_Comm_size(MPI_COMM_WORLD, &world_size);

  // Process 0 creates the array
  int *global_arr = NULL;
  float *gather_buffer = NULL;

  if (my_rank == 0)
  {
    global_arr = create_random_array(num_elements_per_proc * world_size, 100);
    gather_buffer = (float *)malloc(sizeof(float) * world_size);
  }

  int *recv_buffer = (int *)malloc(sizeof(int) * num_elements_per_proc);
  MPI_Scatter(global_arr, num_elements_per_proc, MPI_INT, recv_buffer, num_elements_per_proc, MPI_INT, 0, MPI_COMM_WORLD);

  float avg = compute_average(recv_buffer, num_elements_per_proc);

  MPI_Gather(&avg, 1, MPI_FLOAT, gather_buffer, 1, MPI_FLOAT, 0, MPI_COMM_WORLD);

  if (my_rank == 0)
  {
    float result = compute_final_average(gather_buffer, world_size);
    printf("The average is %f\n", result);
  }

  if (my_rank == 0)
  {
    free(global_arr);
    free(gather_buffer);
  }
  free(recv_buffer);

  MPI_Barrier(MPI_COMM_WORLD);
  MPI_Finalize();
}

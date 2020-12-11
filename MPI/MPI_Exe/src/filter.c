#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <mpi.h>

// Creates an array of random numbers.
int *create_random_array(int num_elements, int max_value)
{
  int *arr = (int *)malloc(sizeof(int) * num_elements);
  for (int i = 0; i < num_elements; i++)
  {
    arr[i] = (rand() % max_value);
  }
  return arr;
}

int count_multiple(int *array, int size, int filter_num)
{
  int count = 0;
  for (int i = 0; i < size; i++)
  {
    if ((array[i] % filter_num) == 0)
      count++;
  }

  return count;
}

int *filter_array(int *array, int size, int filter_num, int out_size)
{
  int *out_array = (int *)malloc(sizeof(int) * out_size);
  int j = 0;
  for (int i = 0; i < size; i++)
  {
    if ((array[i] % filter_num) == 0)
      out_array[j++] = array[i];
  }

  return out_array;
}

void send_results(int *array, int size, int filter_num, int my_rank)
{
  int multiple_count = count_multiple(array, size, filter_num);
  MPI_Send(&multiple_count, 1, MPI_INT, 0, 0, MPI_COMM_WORLD);
  printf("Node %d count result %d\n", my_rank, multiple_count);
  if (multiple_count > 0)
  {
    int *result_array = filter_array(array, size, filter_num, multiple_count);
    MPI_Send(result_array, multiple_count, MPI_INT, 0, 0, MPI_COMM_WORLD);
    free(result_array);
  }
}

int *receive_result(int world_size, int *out_size)
{

  int num_procc_with_result = 0;
  *out_size = 0;
  int *result = NULL;

  for (int procID = 1; procID < world_size; procID++)
  {
    int count_result;
    MPI_Recv(&count_result, 1, MPI_INT, procID, MPI_ANY_TAG, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
    (*out_size) += count_result;
    if (count_result > 0)
      num_procc_with_result++;
  }

  if (*out_size == 0)
    return result;

  result = (int *)malloc(sizeof(int) * (*out_size));
  printf("Master out size %d results\n", *out_size);

  int fill_index = 0;
  for (int i = 0; i < num_procc_with_result; i++)
  {
    MPI_Status status;
    MPI_Probe(MPI_ANY_SOURCE, MPI_ANY_TAG, MPI_COMM_WORLD, &status);
    int count;
    MPI_Get_count(&status, MPI_INT, &count);
    MPI_Recv(&result[fill_index], count, MPI_INT, status.MPI_SOURCE, status.MPI_TAG, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
    fill_index += count;
  }

  return result;
}

// Process 0 selects a number num.
// All other processes have an array that they filter to only keep the elements
// that are multiples of num.
// Process 0 collects the filtered arrays and print them.
int main(int argc, char **argv)
{
  // Maximum value for each element in the arrays
  const int max_val = 100;
  // Number of elements for each processor
  int num_elements_per_proc = 50;
  // Number to filter by
  int num_to_filter_by = 2;
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

  // Process 0 selects the num
  int num;
  int *local_array;
  if (my_rank == 0)
  {
    num = num_to_filter_by;
  }
  else
  {
    local_array = create_random_array(num_elements_per_proc, max_val);
  }

  MPI_Bcast(&num, 1, MPI_INT, 0, MPI_COMM_WORLD);

  if (my_rank > 0)
  {
    send_results(local_array, num_elements_per_proc, num, my_rank);
  }
  else
  {
    int result_size;
    int *results = receive_result(world_size, &result_size);
    printf("Received %d results\n", result_size);
    printf("Results: \n");
    for (int i = 0; i < result_size; i++)
    {
      printf("%d\t", results[i]);
    }

    if (result_size > 0)
    {
      printf("\n");
      free(results);
    }

    if (my_rank > 0)
      free(local_array);
  }

  MPI_Barrier(MPI_COMM_WORLD);
  MPI_Finalize();
}

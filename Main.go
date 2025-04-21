package main

import (
    "fmt"
    "math/rand"
    "sync"
    "time"
)

const numWorkers = 4

func main() {
    taskQueue := make(chan string, 10)
    results := make([]string, 0)
    var resultMutex sync.Mutex
    var wg sync.WaitGroup

    // Populate task queue
    for i := 1; i <= 10; i++ {
        taskQueue <- fmt.Sprintf("Task %d", i)
    }
    close(taskQueue)

    // Start workers
    for i := 0; i < numWorkers; i++ {
        wg.Add(1)
        go func(workerId int) {
            defer wg.Done()
            for task := range taskQueue {
                fmt.Printf("Worker %d started: %s\n", workerId, task)
                time.Sleep(time.Duration(rand.Intn(1000)) * time.Millisecond) // Simulate work
                result := fmt.Sprintf("%s -> processed by Worker %d", task, workerId)

                resultMutex.Lock()
                results = append(results, result)
                resultMutex.Unlock()

                fmt.Printf("Worker %d completed: %s\n", workerId, task)
            }
        }(i)
    }

    wg.Wait()

	fmt.Println("\nProcessed Results:")
	for _, r := range results {
		fmt.Println(r)
	}

	// Write results to file
	file, err := os.Create("results.txt")
	if err != nil {
		fmt.Println("Error creating file:", err)
		return
	}
	defer file.Close()

	for _, r := range results {
		_, err := file.WriteString(r + "\n")
		if err != nil {
			fmt.Println("Error writing to file:", err)
			return
		}
	}
	fmt.Println("Results saved in file successfully -> File Name: results.txt")

}

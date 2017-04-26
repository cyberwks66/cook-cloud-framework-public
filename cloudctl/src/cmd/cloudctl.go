package main

import (
	"fmt"
	"log"
	"net/http"
	"os"

	"github.com/cooksystemsinc/cook-cloud-framework/cloudctl"
)

type Configuration struct {
	Name           string
	Infrastructure []string
	Services       []string
	GitRepo        string
}

var DEV, PROD *Configuration

func init() {
	DEV = &Configuration{
		Name:           "dev",
		Infrastructure: []string{},
		Services:       []string{"configuration:latest", "discovery:latest", "router:latest"},
		GitRepo:        "http://localhost:2222/git-server/repos/config",
	}
	PROD = &Configuration{
		Name:           "prod",
		Infrastructure: []string{},
		Services:       []string{"configuration:latest", "discovery:latest", "router:latest"},
		GitRepo:        "http://localhost:2222/git-server/repos/config",
	}
}

func main() {
	machine, err := cloudctl.GetMachine()

	if err != nil {
		fmt.Printf("Couldn't find Docker Machine:\n %s\n", err)
		os.Exit(2)
	}

	config := DEV

	machine.VerifyServiceImages(config.Services)

	http.HandleFunc("/start", func(w http.ResponseWriter, r *http.Request) {
		startServiceWrapper(machine, config, w, r)
	})
	http.HandleFunc("/stop", func(w http.ResponseWriter, r *http.Request) {
		stopServiceWrapper(machine, config, w, r)
	})

	listen := ":8080"
	fmt.Println("Listening on ", listen)
	log.Fatal(http.ListenAndServe(listen, nil))
}

func startServiceWrapper(machine *cloudctl.Machine, config *Configuration, w http.ResponseWriter, r *http.Request) {
	if len(config.Infrastructure) > 0 {
		fmt.Printf("Starting infrastructure : %s\n", config.Infrastructure)
		for _, inf := range config.Infrastructure {
			err := machine.StartInfrastructure(inf)
			if err != nil {
				fmt.Printf("Error starting infrastructure (%s) : %v\n", inf, err)
			}
			fmt.Printf("+Inf : %s\n", inf)
		}
	}

	if len(config.Services) > 0 {
		fmt.Printf("Starting services : %s\n", config.Services)
		for _, service := range config.Services {
			err := machine.StartContainer(service)
			if err != nil {
				fmt.Printf("Error starting service (%s) : %v\n", service, err)
			}
			fmt.Printf("+Service : %s\n", service)
		}
		fmt.Print("I'm finished!\n")
	}
}

func stopServiceWrapper(machine *cloudctl.Machine, config *Configuration, w http.ResponseWriter, r *http.Request) {
	if len(config.Services) > 0 {
		fmt.Printf("Stopping services : %s\n", config.Services)
		for _, service := range config.Services {
			err := machine.StopContainer(service)
			if err != nil {
				fmt.Printf("Error stopping service (%s) : %v\n", service, err)
				return
			}
			fmt.Printf("-Service : %s\n", service)
		}
	}
}

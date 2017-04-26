package cloudctl

import (
	"fmt"

	"github.com/docker/docker/client"
)

//GetMachine gets a local Docker Machine instance
func GetMachine() (*Machine, error) {
	// dockerClient, err := client.NewClient("unix:///var/run/docker.sock", "1.13.1", &http.Client{}, make(map[string]string))
	dockerClient, err := client.NewEnvClient()
	if err != nil {
		return nil, fmt.Errorf("Error creating Docker API Client :\n%s", err)
	}
	newMachine := &Machine{dockerClient}

	return newMachine, nil
}

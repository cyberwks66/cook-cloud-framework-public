package cloudctl

import (
	"context"
	"fmt"
	"strings"
	"time"

	"github.com/docker/docker/api/types"
	"github.com/docker/docker/api/types/container"
	"github.com/docker/docker/client"
)

//Machine holds the details of a Docker install
type Machine struct {
	DockerClient *client.Client
}

//ListContainers does what it says
func (machine *Machine) ListContainers() ([]types.Container, error) {
	return machine.DockerClient.ContainerList(context.Background(), types.ContainerListOptions{})
}

//ListImages does what it says
func (machine *Machine) ListImages() ([]types.ImageSummary, error) {
	return machine.DockerClient.ImageList(context.Background(), types.ImageListOptions{})
}

//VerifyServiceImages checks the existing "service" images
func (machine *Machine) VerifyServiceImages(services []string) error {
	images, err := machine.DockerClient.ImageList(context.Background(), types.ImageListOptions{})
	if err != nil {
		return err
	}

	for _, s := range services {
		found := false
		for _, i := range images {
			if mapContainsServiceTags(s, i.Labels, i.RepoTags) {
				found = true
				break
			}
		}
		if !found {
			return fmt.Errorf("Couldn't find service : %s", s)
		}
		fmt.Printf("Verified image for Service : %s\n", s)
	}
	return nil
}

func mapContainsServiceTags(service string, labels map[string]string, tags []string) bool {
	//See if there's a label for vendor (TODO : Check that it's cooksys.com)
	if _, ok := labels["vendor"]; ok {
		for _, repoTag := range tags {
			//If so, check the repotag for the service name
			if strings.Contains(repoTag, service) {
				return true
			}
		}
	}
	return false
}

//StartContainer starts a new docker container in the default VM
func (machine *Machine) StartContainer(name string) error {
	ctx := context.Background()

	//We look for these labels later
	config := addManagementLabels(&container.Config{Image: name})
	resp, err := machine.DockerClient.ContainerCreate(ctx, config, nil, nil, "")
	if err != nil {
		return err
	}

	if err := machine.DockerClient.ContainerStart(ctx, resp.ID, types.ContainerStartOptions{}); err != nil {
		return err
	}

	return nil
}

//StopContainer starts a new docker container in the default VM
func (machine *Machine) StopContainer(name string) (err error) {
	ctx := context.Background()
	list, err := machine.DockerClient.ContainerList(ctx, types.ContainerListOptions{})
	if err != nil {
		return err
	}
	for _, container := range list {
		fmt.Printf("Container : %s / %+v\n", container.ID, container.Labels)
		if findManagementLabels(container.Labels) {
			fmt.Printf("Matched : %s\n", name)
			duration := time.Duration(20) * time.Second
			err := machine.DockerClient.ContainerStop(ctx, container.ID, &duration)
			if err != nil {
				return err
			}
		}
	}

	return nil
}

//StartInfrastructure starts a new docker container in the default VM
func (machine *Machine) StartInfrastructure(name string) error {
	ctx := context.Background()
	_, err := machine.DockerClient.ImagePull(ctx, name, types.ImagePullOptions{})
	if err != nil {
		return err
	}
	//We look for these labels later
	config := addManagementLabels(&container.Config{Image: name})
	resp, err := machine.DockerClient.ContainerCreate(ctx, config, nil, nil, name)
	if err != nil {
		return err
	}
	fmt.Printf("Errors : %+v\n", resp.Warnings)

	if err := machine.DockerClient.ContainerStart(ctx, resp.ID, types.ContainerStartOptions{}); err != nil {
		return err
	}

	return nil
}

func addManagementLabels(config *container.Config) *container.Config {
	config.Labels = make(map[string]string)
	config.Labels["managed"] = "byGo"
	config.Cmd = append(config.Cmd, "-Dstuff")
	return config
}

func findManagementLabels(config map[string]string) bool {
	_, ok := config["managed"]
	return ok
}

package cloudctl

import (
	"bytes"
	"fmt"
	"net/http"
	"os"
	"os/exec"
	"path/filepath"
	"strings"
	"syscall"

	"github.com/docker/docker/client"
	"github.com/docker/docker/pkg/tlsconfig"
)

//GetMachine gets a local Docker Machine instance
func GetMachine() (*Machine, error) {
	newMachine := &Machine{"/usr/local/bin/docker-machine", "/Applications/VirtualBox.app/Contents/MacOS/VBoxManage", nil}
	err := newMachine.verifyInstallation()
	if err != nil {
		return nil, err
	}

	dockerCertPath := newMachine.getCertificatePath("default")

	options := tlsconfig.Options{
		CAFile:             filepath.Join(dockerCertPath, "ca.pem"),
		CertFile:           filepath.Join(dockerCertPath, "cert.pem"),
		KeyFile:            filepath.Join(dockerCertPath, "key.pem"),
		InsecureSkipVerify: os.Getenv("DOCKER_TLS_VERIFY") == "",
	}
	tlsc, err := tlsconfig.Client(options)
	if err != nil {
		return nil, err
	}

	httpClient := &http.Client{
		Transport: &http.Transport{
			TLSClientConfig: tlsc,
		},
	}

	client, err := client.NewClient(newMachine.getHostURL("default"), "", httpClient, nil)
	if err != nil {
		return nil, fmt.Errorf("Error creating Docker API Client :\n%s", err)
	}
	newMachine.Docker = client

	return newMachine, nil
}

//UpStart ensures that our docker instance is up and running as needed
func (machine *Machine) UpStart() error {
	if err := machine.testVM("default"); err != nil {
		return err
	}

	if err := machine.startHost("default"); err != nil {
		return err
	}
	return nil
}

func (machine *Machine) verifyInstallation() error {
	dockerMachineExec, err := os.Open(machine.DockerMachine)
	defer dockerMachineExec.Close()

	if err != nil {
		return fmt.Errorf("Couldn't find the Docker Machine binary (%s).", machine.DockerMachine)
	}
	vboxManageExec, err := os.Open(machine.VBoxManage)
	defer vboxManageExec.Close()
	if err != nil {
		return fmt.Errorf("Couldn't find the VBoxManage binary (%s).", machine.VBoxManage)
	}

	if mode, _ := dockerMachineExec.Stat(); 0111&mode.Mode() == 0 {
		return fmt.Errorf("Docker Machine (%s) is not executeable (%s / %s)", machine.DockerMachine, mode.Mode(), 0111&mode.Mode())
	}

	if mode, _ := vboxManageExec.Stat(); 0111&mode.Mode() == 0 {
		return fmt.Errorf("VirtualBox Manage (%s) is not executeable (%s / %s)", machine.VBoxManage, mode.Mode(), 0111&mode.Mode())
	}

	return nil
}

func (machine *Machine) testVM(containerName string) error {
	cmd := exec.Command(machine.VBoxManage, "showvminfo", containerName)
	// var out bytes.Buffer
	// cmd.Stdout = &out
	err := cmd.Run()

	if err != nil {
		return fmt.Errorf("Couldn't run command : %s", err.Error())
	}

	return nil
}

//StartContainer starts a new docker container in the default VM
func (machine *Machine) runMachineCommand(commands ...string) (out bytes.Buffer, status int, err error) {
	cmd := exec.Command(machine.DockerMachine, commands...)
	cmd.Stdout = &out
	err = cmd.Run()
	// Did the command fail because of an unsuccessful exit code
	if exitError, ok := err.(*exec.ExitError); ok {
		waitStatus := exitError.Sys().(syscall.WaitStatus)
		status = waitStatus.ExitStatus()
	}
	if err != nil {
		err = fmt.Errorf("Couldn't run command (%s) with error :\n%s", strings.Join(commands, " % "), err.Error())
		return
	}

	return
}

func (machine *Machine) startHost(host string) error {
	_, status, err := machine.runMachineCommand("start", host)
	if status == 0 || status == 1 {
		return nil
	}

	return fmt.Errorf("Received an unexpected status code (%v) while starting new host ; expected 1 or 0 : \n%s", status, err)
}

func (machine *Machine) getHostURL(host string) string {
	out, _, err := machine.runMachineCommand("inspect", "--format={{.Driver.IPAddress}}", host)

	if err != nil {
		return ""
	}

	return strings.Join([]string{"tcp://", strings.TrimSpace(out.String()), ":2376"}, "")
}

func (machine *Machine) getCertificatePath(host string) string {
	out, _, err := machine.runMachineCommand("inspect", "--format={{.HostOptions.AuthOptions.CertDir}}", host)

	if err != nil {
		return ""
	}

	return strings.TrimSpace(out.String())
}

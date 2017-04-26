package com.cooksys.cloud.util.verifyselftest;

import com.cooksys.cloud.commons.model.SelfTestUuidResponse;
import org.springframework.web.client.RestTemplate;

public class VerifySelfTest {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";

    private String routerHost = "localhost";
    private String routerPort = "8765";
    private int frameworkBootstrapTimeoutSeconds = 300;
    private int memLeakTimeoutSeconds = 180;
    private int cpuHogTimeoutSeconds = 180;
    private int scaleUpTimeoutSeconds = 180;
    private int scaleDownTimeoutSeconds = 180;

    private RestTemplate restTemplate = new RestTemplate();

    public int execute() {
        final String prefix = "http://" + routerHost + ":" + routerPort;
        final String discoveryUrl = prefix + "/discovery/self-test/uuids";
        final String memLeakUrl = prefix + "/self-test/selftest/memoryleak";
        final String cpuHogUrl = prefix + "/self-test/selftest/cpuhog";
        final String autoScaleUrl = prefix + "/self-test/selftest/simulate-load";
        final String selfTestRestUrl = prefix + "/self-test/selftest/rest";

        System.out.println("Starting Platform Self Test");
        System.out.println();

        String initialUuid = "";
        boolean passedTest = false;
        String testFailMessage = "";

        for (int i = frameworkBootstrapTimeoutSeconds; i >= 0; i--) {
            System.out.print("\r");
            System.out.print("Waiting for platform to start .............................. " + prettyFormatSeconds(i));

            try {
                restTemplate.getForObject(selfTestRestUrl, String.class, (String) null);
                passedTest = true;
            } catch (Exception e) {
                // ignore
            }

            if (passedTest)
                break;

            sleep();
            if (i == 0) {
                testFailMessage = "Test Failed: framework did not start within threshold.";
            }
        }

        if (passedTest) {
            System.out.print("\r");
            System.out.print("Waiting for platform to start .............................. " + ANSI_GREEN
                    + "TEST PASSED" + ANSI_RESET);
            System.out.println();

        } else {
            System.out.print("\r");
            System.out.print("Waiting for platform to start .............................. " + ANSI_RED + "TEST FAILED"
                    + ANSI_RESET);
            System.out.println();
            System.out.println(testFailMessage);
            System.out.println();
            return 1;
        }

        SelfTestUuidResponse uuidResponse = restTemplate.getForObject(discoveryUrl, SelfTestUuidResponse.class);
        initialUuid = uuidResponse.getUuids().get(0);

        System.out.println("Starting Memory-leak self test");
        passedTest = false;

        restTemplate.postForObject(memLeakUrl, null, String.class);

        for (int i = memLeakTimeoutSeconds; i >= 0; i--) {
            System.out.print("\r");
            System.out.print("Memory leak test ........................................... " + prettyFormatSeconds(i));

            SelfTestUuidResponse response = restTemplate.getForObject(discoveryUrl, SelfTestUuidResponse.class,
                    (String) null);

            if (response != null && response.getUuids().size() > 0) {
                if (response.getUuids().size() > 1) {
                    testFailMessage = "Test failed: multiple self-test APIs are running - check your configuration to ensure only one instance is configured for bootstrap.";
                } else {
                    if (!response.getUuids().get(0).equals(initialUuid)) {
                        passedTest = true;
                        initialUuid = response.getUuids().get(0);
                        break;
                    }
                }
            }

            sleep();
            if (i == 0) {
                testFailMessage = "Test Failed: self-test was not restarted within threshold.";
            }
        }

        if (passedTest) {
            System.out.print("\r");
            System.out.print("Memory leak test ........................................... " + ANSI_GREEN
                    + "TEST PASSED" + ANSI_RESET);
            System.out.println();

        } else {
            System.out.print("\r");
            System.out.print("Memory leak test ........................................... " + ANSI_RED + "TEST FAILED"
                    + ANSI_RESET);
            System.out.println();
            System.out.println(testFailMessage);
            System.out.println();
            return 1;
        }

        passedTest = false;
        for (int i = frameworkBootstrapTimeoutSeconds; i >= 0; i--) {
            System.out.print("\r");
            System.out.print("Waiting for route to new self-test instance ................ " + prettyFormatSeconds(i));

            try {
                restTemplate.getForObject(selfTestRestUrl, String.class, (String) null);
                passedTest = true;
            } catch (Exception e) {
                // ignore
            }

            if (passedTest)
                break;

            sleep();
            if (i == 0) {
                testFailMessage = "Test Failed: route was not created to new self-test insance.";
            }
        }

        if (passedTest) {
            System.out.print("\r");
            System.out.print("Waiting for route to new self-test instance ................ " + ANSI_GREEN
                    + "TEST PASSED" + ANSI_RESET);
            System.out.println();

        } else {
            System.out.print("\r");
            System.out.print("Waiting for route to new self-test instance ................ " + ANSI_RED + "TEST FAILED"
                    + ANSI_RESET);
            System.out.println();
            System.out.println(testFailMessage);
            System.out.println();
            return 1;
        }
        passedTest = false;

        System.out.println("Starting CPU Hog self test");

        boolean cpuHogPostSuccess = false;
        while (!cpuHogPostSuccess) {
            try {
                restTemplate.postForObject(cpuHogUrl, null, String.class);
            } catch (Exception e) {
                System.out.println("error sending POST request to start CPU hog test: " + e.getMessage());
                System.out.println("Retrying...");
                try {
                    Thread.sleep(1000);
                    continue;
                } catch (InterruptedException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
            cpuHogPostSuccess = true;
        }

        for (int i = cpuHogTimeoutSeconds; i >= 0; i--) {
            System.out.print("\r");
            System.out.print("CPU Hog test ............................................... " + prettyFormatSeconds(i));

            SelfTestUuidResponse response = restTemplate.getForObject(discoveryUrl, SelfTestUuidResponse.class,
                    (String) null);

            if (response != null && response.getUuids().size() > 0) {
                if (response.getUuids().size() > 1) {
                    testFailMessage = "Test failed: multiple self-test APIs are running - check your configuration to ensure only one instance is configured for bootstrap.";
                } else {
                    if (!response.getUuids().get(0).equals(initialUuid)) {
                        passedTest = true;
                        initialUuid = response.getUuids().get(0);
                        break;
                    }
                }
            }

            sleep();
            if (i == 0) {
                testFailMessage = "Test Failed: self-test was not restarted within threshold.";
            }
        }

        if (passedTest) {
            System.out.print("\r");
            System.out.print("CPU Hog test ............................................... " + ANSI_GREEN
                    + "TEST PASSED" + ANSI_RESET);
            System.out.println();

        } else {
            System.out.print("\r");
            System.out.print("CPU Hog test ............................................... " + ANSI_RED + "TEST FAILED"
                    + ANSI_RESET);
            System.out.println();
            System.out.println(testFailMessage);
            System.out.println();
            return 1;
        }

        passedTest = false;
        for (int i = frameworkBootstrapTimeoutSeconds; i >= 0; i--) {
            System.out.print("\r");
            System.out.print("Waiting for route to new self-test instance ................ " + prettyFormatSeconds(i));

            try {
                restTemplate.getForObject(selfTestRestUrl, String.class, (String) null);
                passedTest = true;
            } catch (Exception e) {
                // ignore
            }

            if (passedTest)
                break;

            sleep();
            if (i == 0) {
                testFailMessage = "Test Failed: route was not created to new self-test insance.";
            }
        }

        if (passedTest) {
            System.out.print("\r");
            System.out.print("Waiting for route to new self-test instance ................ " + ANSI_GREEN
                    + "TEST PASSED" + ANSI_RESET);
            System.out.println();

        } else {
            System.out.print("\r");
            System.out.print("Waiting for route to new self-test instance ................ " + ANSI_RED + "TEST FAILED"
                    + ANSI_RESET);
            System.out.println();
            System.out.println(testFailMessage);
            System.out.println();
            return 1;
        }

        passedTest = false;

		/*
         * Auto-scale Test
		 */
        System.out.println("Starting Scale-up self test");

        for (int i = 0; i < 5; i++) {
            try {
                restTemplate.postForObject(autoScaleUrl, null, String.class);
            } catch (Exception e) {
                continue;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            break;
        }

        for (int i = scaleUpTimeoutSeconds; i >= 0; i--) {
            System.out.print("\r");
            System.out.print("Scale-up test .............................................. " + prettyFormatSeconds(i));

            SelfTestUuidResponse response = restTemplate.getForObject(discoveryUrl, SelfTestUuidResponse.class,
                    (String) null);

            if (response != null && response.getUuids().size() == 2) {
                passedTest = true;
                initialUuid = response.getUuids().get(0);
                break;
            }

            sleep();
            if (i == 0) {
                testFailMessage = "Test Failed: self-test did not scale to 2 instances.";
            }
        }

        if (passedTest) {
            System.out.print("\r");
            System.out.print("Scale-up test .............................................. " + ANSI_GREEN
                    + "TEST PASSED" + ANSI_RESET);
            System.out.println();

        } else {
            System.out.print("\r");
            System.out.print("Scale-up test .............................................. " + ANSI_RED + "TEST FAILED"
                    + ANSI_RESET);
            System.out.println();
            System.out.println(testFailMessage);
            System.out.println();
            return 1;
        }

        System.out.println("Starting Scale-up self test");
        try {
            restTemplate.delete(autoScaleUrl);
            restTemplate.delete(autoScaleUrl);
            restTemplate.delete(autoScaleUrl);
        } catch (Exception e) {
            // ignore
        }

        for (int i = scaleDownTimeoutSeconds; i >= 0; i--) {
            System.out.print("\r");
            System.out.print("Scale-down test ............................................ " + prettyFormatSeconds(i));

            SelfTestUuidResponse response = restTemplate.getForObject(discoveryUrl, SelfTestUuidResponse.class,
                    (String) null);

            if (response != null && response.getUuids().size() == 1) {
                passedTest = true;
                initialUuid = response.getUuids().get(0);
                break;
            }

            sleep();
            if (i == 0) {
                testFailMessage = "Test Failed: self-test did not scale down to 1 instance.";
            }
        }

        if (passedTest) {
            System.out.print("\r");
            System.out.print("Scale-down test ............................................ " + ANSI_GREEN
                    + "TEST PASSED" + ANSI_RESET);
            System.out.println();

        } else {
            System.out.print("\r");
            System.out.print("Scale-down test ............................................ " + ANSI_RED + "TEST FAILED"
                    + ANSI_RESET);
            System.out.println();
            System.out.println(testFailMessage);
            System.out.println();
            return 1;
        }

        return 0;
    }

    private String prettyFormatSeconds(int seconds) {
        int minutes = (seconds % 3600) / 60;
        int secondsRemaining = seconds % 60;

        return String.format("%02d:%02d", minutes, secondsRemaining);
    }

    private void sleep() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void printUsage() {
        String usage = "verify-self-test [ -routerHost <host> ] [ -routerPort <port> ] [ -frameworkBootstrapTimeoutSeconds <seconds>] [ -memLeakTimeoutSeconds <seconds> ] [-cpuHogTimeoutSeconds <seconds> ] [ -scaleUpTimeoutSeconds <seconds> ] [ -scaleDownTimeoutSeconds <seconds> ] [ -help | --help ]";
        System.out.println(usage);
        System.exit(1);
    }

    public static void main(String[] args) {
        final VerifySelfTest selfTest = new VerifySelfTest();

        for (int i = 0; i < args.length; i++) {
            if ("-routerHost".equals(args[i])) {
                selfTest.routerHost = args[++i];
            } else if ("-routerPort".equals(args[i])) {
                selfTest.routerPort = args[++i];
            } else if ("-frameworkBootstrapTimeoutSeconds".equals(args[i])) {
                selfTest.frameworkBootstrapTimeoutSeconds = new Integer(args[++i]);
            } else if ("-memLeakTimeoutSeconds".equals(args[i])) {
                selfTest.memLeakTimeoutSeconds = new Integer(args[++i]);
            } else if ("-cpuHogTimeoutSeconds".equals(args[i])) {
                selfTest.cpuHogTimeoutSeconds = new Integer(args[++i]);
            } else if ("-scaleUpTimeoutSeconds".equals(args[i])) {
                selfTest.scaleUpTimeoutSeconds = new Integer(args[++i]);
            } else if ("-scaleDownTimeoutSeconds".equals(args[i])) {
                selfTest.scaleDownTimeoutSeconds = new Integer(args[++i]);
            } else if ("-help".equals(args[i]) || "--help".equals(args[i])) {
                printUsage();
            } else {
                System.out.println("Illegal option: " + args[i]);
                printUsage();
            }
        }
        System.exit(selfTest.execute());
    }

}

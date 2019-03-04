```
px uaa login uaa_rubhge admin
px cs predix-event-hub Tiered eh_shared uaa_rubhge
```

# Environment
It's possible to execute tests on dev, int and qa environment.
Use env variable in command line for that. (Example: -Denv=dev) 

It's possible to specify group of tests in the RunCucumberTest.java file.  
	Use tags attribute for that. 
	Example: tag = {"@group"}
Or better way is to use cucumber.options in command line. (Example: "-Dcucumber.options=--tags @mapped")

# Running tests

```
-Dtest=RunCucumberTest test -Denv=dev "-Dcucumber.options=--tags @mapped"
or
-Dtest=RunCucumberTest test -Denv=int "-Dcucumber.options=--tags @mapped,@unmapped,sdc,asset"
```
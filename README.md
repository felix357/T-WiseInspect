# T-Wise-Sampling
The **T-Wise-Sampling** tool performs sampling analysis on feature models, supporting different sampling algorithms (YASA, UNIFORM, INCLING). It outputs statistics about the resulting configurations, making it useful for combinatorial testing and configuration analysis.

## Introduction
The **T-Wise-Sampling** tool analyzes feature models to generate representative configurations for software systems. It supports multiple sampling algorithms (YASA, UNIFORM, INCLING) to create configurations with t-wise feature interactions. Additionally, the tool enables a comparative analysis of these algorithms based on key metrics, including the number of configurations, feature interaction coverage, and the frequency of interaction coverage for t=2. This enables a deeper understanding of the trade-offs between different coverage algorithms.

## Dependencies
- Java 21  or later
- Gradle 8 or later

## Setup Instructions
1. Clone this repository: ```git clone https://github.com/felix357/t-wise-sampling.git```
2. After cloning navigate to the project directory: ```cd t-wise-sampling```
3. Build the project using gradle: ``` ./gradlew clean shadowJar```
4. The JAR file will be located in the ```app/build/libs``` folder and will be named ```app.jar```
5. Once the project is built, you can run the executable JAR file from the command line using Java. ```cd build/libs``` ```java -jar t-wise-sampling-1.0.jar --input-file /path/to/input.xml --sampling-algorithm YASA --output /path/to/output/statistics.txt```
6. Ensure that you have Java 21 or later installed.
7. Ensure that you have Gradle 8 or later installed. 

## Features
- Reads and processes a Feature Model from an XML file.
- Supports various sampling algorithms: YASA, UNIFORM, INCLING.
- Configurable t-wise feature interaction.
- Outputs statistics for resulting configurations.

## How to Use
You can run the tool via the command-line interface (CLI). The tool requires several parameters for operation, and you can provide optional configurations as needed.

### Required Parameters
#### Input File:
```--input-file``` / ```-i```<br>
The path to the input XML file containing the feature model. This file should define the feature model that you wish to analyze.

**Example**:<br>```--input-file /path/to/your/input.xml```


#### Sampling Algorithm:
```--sampling-algorithm``` / ```-s```<br>
The sampling algorithm to be used. The supported algorithms are:
- YASA
- UNIFORM
- INCLING

**Example**:<br>```--sampling-algorithm YASA```

#### Output Path:
```--output``` / ```-o```<br>
The path to the output file where the statistics of the resulting configurations will be saved.

**Example**:<br>```--output /path/to/output/statistics.txt```

### Optional Parameters

#### T Value:
```--t-value``` / ```-t```<br>
An optional parameter that specifies the number of variables for t-wise feature interactions.
If not provided, the default value is 2.

**Example**:<br> ```--t-value 3```

#### Number of Configurations:
```--configurations``` / ```-c```<br>
This parameter defines the maximum number of configurations for all samplers.
If not provided the default value is 10000.

**Example**:<br> ```--configurations 5```

#### CSV Summary Report:
```--csv```<br>
If this flag is provided, the tool will generate a results.csv file containing t-wise coverage statistics and exactly-once interaction coverage counts across multiple sample sizes.
This CSV report can help in analyzing how coverage evolves with increasing sample sizes.

**Example**:<br> ```--csv```

Full Command Example
Here is an example of how to run the tool with all required parameters:
```java -jar sampling-analyzer.jar --input-file /path/to/your/input.xml --sampling-algorithm YASA --output /path/to/output/statistics.txt```

## Output
The output will be a file containing the statistics of the resulting configurations, including various metrics relevant to the chosen sampling algorithm.

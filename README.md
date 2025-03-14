# T-Wise-Sampling
The **T-Wise-Sampling** tool performs sampling analysis on feature models, supporting different sampling algorithms (YASA, UNIFORM, INCLING). It outputs statistics about the resulting configurations, making it useful for combinatorial testing and configuration analysis.


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


Full Command Example
Here is an example of how to run the tool with all required parameters:
```java -jar sampling-analyzer.jar --input-file /path/to/your/input.xml --sampling-algorithm YASA --output /path/to/output/statistics.txt```

## Output
The output will be a file containing the statistics of the resulting configurations, including various metrics relevant to the chosen sampling algorithm.
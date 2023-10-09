package data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class ConvertMushroom {

    public static void main(String[] args) throws IOException {

        // input file path and output file path
        String inputFile = "datasets/agaricus-lepiota.data";
        String outputFile = "datasets/agaricus-lepiota_deep.txt";

        // read input file
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
                //to check if lines print correctly
                System.out.println(line);
            }

        }

        // extract unique values for each feature
        Set<String>[] featureValues = new Set[lines.get(0).split(",").length];
        for (int i = 0; i < featureValues.length; i++) {
            featureValues[i] = new HashSet<>();
        }
        for (String line : lines) {
            String[] values = line.split(",");
            for (int i = 0; i < values.length; i++) {
                featureValues[i].add(values[i]);
                //System.out.println(Arrays.toString(values));
            }
        }

        // write one-hot encoded data to output file
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile)))
        {
            for (String line : lines) {
                int j = 0;
                String[] values = line.split(",");
                for (int i = 0; i < values.length; i++)
                {

                    Set<String> uniqueValues = featureValues[i];
                    for (String value : uniqueValues)
                    {
                        if(j==0){
                            if (value.equals(values[i]))
                            {
                                bw.write("1:");
                            } else
                            {
                                bw.write("0:");
                            }
                        }else if (j == 1) {
                            if (value.equals(values[i]))
                            {
                                bw.write("");
                            } else
                            {
                                bw.write("");
                            }
                        } else if (j == 118) {
                            if (value.equals(values[i]))
                            {
                                bw.write("1");
                            } else
                            {
                                bw.write("0");
                            }
                        }else {
                            if (value.equals(values[i]))
                            {
                                bw.write("1,");
                            } else
                            {
                                bw.write("0,");
                            }
                        }
                        j++;
                    }
                }
                bw.write("\n");
            }
        }
    }
}


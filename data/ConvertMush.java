package data;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import util.Log;

public class ConvertMush {
    public static void main(String[] arguments) {
        try {
            //create a buffered reader given the filename (which requires creating a File and FileReader object beforehand)
            BufferedReader bufferedReader = new BufferedReader(new FileReader(new File("./datasets/agaricus-lepiota.data")));
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File("./datasets/agaricus-lepiota2.txt")));

            String readLine = "";
            //read the file line by line
            while ((readLine = bufferedReader.readLine()) != null) {
                Log.info(readLine); //print out each line of the file if the -DLOG_LEVEL=DEBUG system property is set on the command line

                if (readLine.length() == 0 || readLine.charAt(0) == '#') {
                    //empty lines are skipped, as well as lines beginning
                    //with the '#' character, these are comments and also skipped
                    continue;
                }

                String[] values = readLine.split(",");
                for(int j = 0; j<23;j++) {
                    String sampleClass = values[j]; //the class of the dataset is the last column

                    //put everything in a stringbuffer before writing to the file
                    StringBuffer sb = new StringBuffer();

                    //This dataset has 2 classes: 'p' and 'e'
                    if (j == 0) {
                        if (sampleClass.equals("e")) {
                            sb.append("0"); //this will be the third class
                        } else if (sampleClass.equals("p")) {
                            sb.append("1"); //this will be the second class
                        } else {
                            System.err.println("ERROR: unknown class in agaricus-lepiota.data file: '" + sampleClass + "'");
                            System.err.println("This should not happen.");
                            System.exit(1);
                        }
                        sb.append(":");
                    }
                    if (j == 1) {
                        switch (sampleClass) {
                            case "b" -> sb.append("1,0,0,0,0,0");
                            case "c" -> sb.append("0,1,0,0,0,0");
                            case "x" -> sb.append("0,0,1,0,0,0");
                            case "f" -> sb.append("0,0,0,1,0,0");
                            case "k" -> sb.append("0,0,0,0,1,0");
                            case "s" -> sb.append("0,0,0,0,0,1");
                        }
                        sb.append(":");
                    }
                    if (j == 2) {
                        switch (sampleClass) {
                            case "f" -> sb.append("1,0,0,0");
                            case "g" -> sb.append("0,1,0,0");
                            case "y" -> sb.append("0,0,1,0");
                            case "s" -> sb.append("0,0,0,1");
                        }
                        sb.append(":");
                    }
                    if (j == 3) {
                        switch (sampleClass) {
                            case "n" -> sb.append("1,0,0,0,0,0,0,0,0,0");
                            case "b" -> sb.append("0,1,0,0,0,0,0,0,0,0");
                            case "c" -> sb.append("0,0,1,0,0,0,0,0,0,0");
                            case "g" -> sb.append("0,0,0,1,0,0,0,0,0,0");
                            case "r" -> sb.append("0,0,0,0,1,0,0,0,0,0");
                            case "p" -> sb.append("0,0,0,0,0,1,0,0,0,0");
                            case "u" -> sb.append("0,0,0,0,0,0,1,0,0,0");
                            case "e" -> sb.append("0,0,0,0,0,0,0,1,0,0");
                            case "w" -> sb.append("0,0,0,0,0,0,0,0,1,0");
                            case "y" -> sb.append("0,0,0,0,0,0,0,0,0,1");
                        }
                        sb.append(":");
                    }

                    //the other values are the different input values for the neural network
                    for (int i = 0; i < values.length - 1; i++) {
                        if (i > 0) sb.append(",");
                        sb.append(values[i]);
                    }
                    sb.append("\n");
                    Log.info(sb.toString());
                    bufferedWriter.write(sb.toString());
                }
            }
            bufferedWriter.close();
            bufferedReader.close();

        } catch (IOException e) {
            Log.fatal("ERROR converting agaricus-lepiota data file");
            e.printStackTrace();
            System.exit(1);
        }
    }
}

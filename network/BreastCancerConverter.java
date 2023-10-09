package network;

public class BreastCancerConverter {
    public static void main(String[] args) {
        String[][] data = {
                {"no-recurrence-events", "20-29" ,"ge40" ,"15-19" ,"12-14", "yes" ,"3", "right" ,"left-low", "no"},
                {"recurrence-events", "60-69", "premeno", "20-24", "9-11", "yes", "3", "right", "left-up", "yes"},
                {"recurrence-events", "10-19", "lt40" ,"0-4" ,"21-23", "no" ,"3" ,"left" ,"central", "yes"},
                {"recurrence-events", "40-49", "premeno", "30-34", "36-39", "yes" ,"3", "left", "right-low" ,"no"}
        };
        int[][] encodedData = encodeData(data);
        printEncodedData(encodedData);
    }

    private static int[][] encodeData(String[][] data) {
        int numRows = data.length;
        int numCols = 52;
        int[][] encodedData = new int[numRows][numCols];
        for (int i = 0; i < numRows; i++) {
            // encode class attribute
            switch (data[i][0]) {
                case "no-recurrence-events" -> encodedData[i][0] = 0;
                default -> encodedData[i][0] = 1;
            }
            // encode age attribute
            switch (data[i][1]) {
                case "10-19" -> encodedData[i][1] = 1;
                case "20-29" -> encodedData[i][2] = 1;
                case "30-39" -> encodedData[i][3] = 1;
                case "40-49" -> encodedData[i][4] = 1;
                case "50-59" -> encodedData[i][5] = 1;
                case "60-69" -> encodedData[i][6] = 1;
                case "70-79" -> encodedData[i][7] = 1;
                case "80-89" -> encodedData[i][8] = 1;
                case "90-99" -> encodedData[i][9] = 1;
            }
            // encode menopause attribute
            switch (data[i][2]) {
                case "lt40" -> encodedData[i][10] = 1;
                case "ge40" -> encodedData[i][11] = 1;
                default -> encodedData[i][12] = 1;
            }
            // encode tumor-size attribute
            switch (data[i][3]) {
                case "0-4" -> encodedData[i][13] = 1;
                case "5-9" -> encodedData[i][14] = 1;
                case "10-14" -> encodedData[i][15] = 1;
                case "15-19" -> encodedData[i][16] = 1;
                case "20-24" -> encodedData[i][17] = 1;
                case "25-29" -> encodedData[i][18] = 1;
                case "30-34" -> encodedData[i][19] = 1;
                case "35-39" -> encodedData[i][20] = 1;
                case "40-44" -> encodedData[i][21] = 1;
                case "45-49" -> encodedData[i][22] = 1;
                case "50-54" -> encodedData[i][23] = 1;
                case "55-59" -> encodedData[i][24] = 1;
            }
            // encode inv-nodes attribute
            switch (data[i][4]) {
                case "0-2" -> encodedData[i][25] = 1;
                case "3-5" -> encodedData[i][26] = 1;
                case "6-8" -> encodedData[i][27] = 1;
                case "9-11" -> encodedData[i][28] = 1;
                case "12-14" -> encodedData[i][29] = 1;
                case "15-17" -> encodedData[i][30] = 1;
                case "18-20" -> encodedData[i][31] = 1;
                case "21-23" -> encodedData[i][32] = 1;
                case "24-26" -> encodedData[i][33] = 1;
                case "27-29" -> encodedData[i][34] = 1;
                case "30-32" -> encodedData[i][35] = 1;
                case "33-35" -> encodedData[i][36] = 1;
                case " 36-39" -> encodedData[i][37] = 1;
            }
            // encode node-caps attribute
            if ("yes".equals(data[i][5])) {
                encodedData[i][38] = 1;
            } else {
                encodedData[i][39] = 1;
            }
            // encode deg-malig attribute
            switch (data[i][6]) {
                case "1" -> encodedData[i][40] = 1;
                case "2" -> encodedData[i][41] = 1;
                case "3" -> encodedData[i][42] = 1;
            }
            // encode breast attribute
            if ("right".equals(data[i][7])) {
                encodedData[i][43] = 1;
            } else {
                encodedData[i][44] = 1;
            }
            // encode breast-quad attribute
            switch (data[i][8]) {
                case "left-up" -> encodedData[i][45] = 1;
                case "left-low" -> encodedData[i][46] = 1;
                case "right-up" -> encodedData[i][47] = 1;
                case "right-low" -> encodedData[i][48] = 1;
                case "central" -> encodedData[i][49] = 1;
            }
            // encode irradiat attribute
            if (data[i][9].equals("yes")) {
                encodedData[i][50] = 1;
            } else {
                encodedData[i][51] = 1;
            }
        }
        return encodedData;
    }

    private static void printEncodedData(int[][] encodedData) {
        for (int i = 0; i < encodedData.length; i++) {
            for (int j= 0; j < encodedData[i].length; j++) {
                if(j==0){
                    System.out.print(encodedData[i][j] + ":");
                }else if(j == 51){
                    System.out.print(encodedData[i][j]);
                }else {
                    System.out.print(encodedData[i][j] + "," );
                }
            }
            System.out.println();
        }
    }
}


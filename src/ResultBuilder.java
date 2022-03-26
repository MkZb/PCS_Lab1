import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class ResultBuilder {
    static int P = 4;
    static int N = 400;

    public static void main(String[] args) {
        float[][] MA = new float[N][N];
        float[] E = new float[N];

        try {
            File resultMA = new File("results/resultMA.txt");
            File resultE = new File("results/resultE.txt");
            for (int i = 0; i < P; i++) {
                File fileE = new File("results/resultE" + i + ".txt");
                Scanner scanner = new Scanner(fileE);
                for (int j = 0; j < N / P; j++) {
                    E[j + (N / P) * i] = Float.parseFloat(scanner.nextLine());
                }
                scanner.close();
            }

            for (int i = 0; i < P; i++) {
                File fileMA = new File("results/resultMA" + i + ".txt");
                Scanner scanner = new Scanner(fileMA);
                for (int j = 0; j < N; j++) {
                    for (int k = (N / P) * i; k < (N / P) * (i + 1); k++) {
                        MA[j][k] = Float.parseFloat(scanner.nextLine());
                    }
                }
                scanner.close();
            }
            FileWriter writer1 = new FileWriter("results/resultE.txt");
            for (int i = 0; i < N; i++) {
                writer1.write(E[i] + "\n");
            }
            writer1.close();

            FileWriter writer2 = new FileWriter("results/resultMA.txt");
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    writer2.write(MA[i][j] + "\n");
                }
            }
            writer2.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

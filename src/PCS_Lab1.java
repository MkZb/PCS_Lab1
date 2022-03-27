import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public class PCS_Lab1 {
    static long timeExec = 0;
    static long timeStart = 0;

    public static void main(String[] args) {
        System.out.println("Program started");

        int P = 4; //Threads count. Set it so N is multiple of P.
        int N = 1500; //Matrix and array size.

        Data data = new Data(N);
        data.loadData("test6.txt");

        for (int i = 0; i < P; i++) {
            int pNum = i;
            new Thread(new Runnable() {
                public void run() {
                    System.out.println("Thread " + pNum + " started");
                    int P = 4; //Threads count. Set it so N is multiple of P. (local)
                    int N = 1500; //Matrix and array size. (local)
                    float[][] MD;
                    float[][] MT;
                    float[][] MZ;
                    float[] B;
                    float[] D;

                    MD = data.parseMatrix(N, 0);
                    MT = data.parseMatrix(N, N * N);
                    MZ = data.parseMatrix(N, 2 * (N * N));
                    B = data.parseVector(N, 3 * (N * N));
                    D = data.parseVector(N, 3 * (N * N) + N);
                    System.out.println("Data successfully parsed");

                    long start = System.currentTimeMillis();

                    float a = 0;
                    float[][] MTZpart = new float[N][N / P];
                    float[][] MTDpart = new float[N][N / P];
                    float[][] MApart = new float[N][N / P];
                    float[] Epart = new float[N / P];

                    //Calc max(MD)
                    for (int j = 0; j < N; j++) {
                        for (int k = 0; k < N; k++) {
                            if (MD[j][k] > a) a = MD[j][k];
                        }
                    }

                    //Calc B*MD+D*MT
                    for (int j = (N / P) * pNum; j < (N / P) * (pNum + 1); j++) {
                        float[] arrayToAdd = new float[2 * N];
                        for (int k = 0; k < N; k++) {
                            arrayToAdd[k] += B[k] * MD[j][k];
                            arrayToAdd[k + N] += D[k] * MT[j][k];
                        }
                        Arrays.sort(arrayToAdd);
                        for (int k = 0; k < 2 * N; k++) {
                            Epart[j - (N / P) * pNum] += arrayToAdd[k];
                        }
                    }

                    //Calc max(MD)*(MT+MZ)
                    for (int j = 0; j < N; j++) {
                        for (int k = (N / P) * pNum; k < (N / P) * (pNum + 1); k++) {
                            MTZpart[j][k - (N / P) * pNum] = a * (MT[j][k] + MZ[j][k]);
                        }
                    }

                    //Calc max(MD)*(MT+MZ)-MT*MD
                    for (int j = 0; j < N; j++) {
                        for (int k = (N / P) * (pNum); k < (N / P) * (pNum + 1); k++) {
                            float[] arrayToAdd = new float[N];
                            for (int l = 0; l < N; l++) {
                                arrayToAdd[l] = MT[j][l] * MD[l][k];
                            }
                            Arrays.sort(arrayToAdd);
                            MTDpart[j][k - (N / P) * (pNum)] = 0;
                            for (int l = 0; l < N; l++) {
                                MTDpart[j][k - (N / P) * (pNum)] += arrayToAdd[l];
                            }
                            MApart[j][k - (N / P) * (pNum)] = MTZpart[j][k - (N / P) * (pNum)] - MTDpart[j][k - (N / P) * (pNum)];
                        }
                    }

                    try {
                        long finish = System.currentTimeMillis();
                        File resultMA = new File("results/resultMA" + pNum + ".txt");
                        File resultE = new File("results/resultE" + pNum + ".txt");
                        FileWriter writer1 = new FileWriter("results/resultMA" + pNum + ".txt");
                        FileWriter writer2 = new FileWriter("results/resultE" + pNum + ".txt");
                        for (int j = 0; j < N; j++) {
                            for (int k = (N / P) * (pNum); k < (N / P) * (pNum + 1); k++) {
                                //System.out.println("MA[" + j + "][" + k + "] = " + MApart[j][k - (N / P) * (pNum)]);
                                writer1.write(MApart[j][k - (N / P) * (pNum)] + "\n");
                            }
                        }

                        for (int j = 0; j < N / P; j++) {
                            //System.out.println("E[" + j + (N / P) * (pNum) + "] = " + Epart[j]);
                            writer2.write(Epart[j] + "\n");
                        }
                        writer1.close();
                        writer2.close();
                        System.out.println("Data successfully saved on disk");
                        setTime(start, finish);
                    } catch (IOException e) {
                        System.out.println("An error occurred.");
                        e.printStackTrace();
                    }

                }

                public synchronized void setTime(long startTime, long endTime) {
                    if (timeStart == 0) timeStart = startTime;
                    if (timeStart > startTime) timeStart = startTime;
                    if ((endTime - timeStart) > timeExec) timeExec = endTime - timeStart;
                    System.out.println("Current time executed = " + timeExec);
                }
            }).start();
        }
    }
}



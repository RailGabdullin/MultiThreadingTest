import java.util.Arrays;

public class BigArrayTest {

    /**
     * Эта программа тестирует многопоточность.
     * Мы создаем массив размерностью 10 млн. и заполняем его единицами. Потом по формуле с sin и cos делаем расчеты
     * новых значений массива.
     * Метод sampleArrTest - показыает как просиходит расчет новых значений массива в одном потоке.
     * Метод towThreadsArrayTest - делит массив на два новых полу-массива и расчитывает их значения в дух разных потоках.
     * Метод optionThreadArrayTest - позволяет задать некоторое количесто потоков, которые будут параллельно расчитывать
     * значения для своих частей массивов.
     *
     * Ресультаты выводятся в консоль в виде времени в миллисекундах затраченных программой на рассчеты. В случае многопоточности
     * в рассчет времени включается и время на разделение изначального массива, создание потоков и склейку массива.
     *
     * На моей машине на момент создания лучшие результаты получаются в районе 85 потоков.
     */
    static final int SIZE = 10000000;
    static final int HALF = SIZE / 2;
    static float[] arr = new float[SIZE];

    static void sampleArrTest(float [] arr){
        long a = System.currentTimeMillis();
        for (int i = 0; i < SIZE; i++) {
            arr[i] = (float)(arr[i] * Math.sin(0.2f + i / 5) * Math.cos(0.2f + i / 5) * Math.cos(0.4f + i / 2));
        }
        System.out.println(System.currentTimeMillis() - a);
    }

    static void towThreadsArrayTest(float [] arr) throws InterruptedException {
        long a = System.currentTimeMillis();
        float [] arr1 = new float[HALF];
        float [] arr2 = new float[HALF];
        System.arraycopy(arr, 0, arr1, 0, HALF);
        System.arraycopy(arr, HALF, arr2, 0, HALF);
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < HALF; i++) {
                    arr1[i] = (float)(arr1[i] * Math.sin(0.2f + i / 5) * Math.cos(0.2f + i / 5) * Math.cos(0.4f + i / 2));
                }
            }
        });
        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < HALF; i++) {
                    arr2[i] = (float)(arr2[i] * Math.sin(0.2f + i / 5) * Math.cos(0.2f + i / 5) * Math.cos(0.4f + i / 2));
                }
            }
        });

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        float [] resultArr = new float[SIZE];
        System.arraycopy(arr1, 0, resultArr, 0, HALF);
        System.arraycopy(arr2,0, resultArr, HALF, HALF);

        System.out.println(System.currentTimeMillis()-a);
    }

    static void optionThreadArrayTest(float [] arr, int numberOfThreads) throws InterruptedException {
        long a = System.currentTimeMillis();
        Thread [] threads = new Thread[numberOfThreads];
        float [][] arrOfArr = new float[numberOfThreads][SIZE/numberOfThreads];
        int k = 0;
        for (int i = 0; i < numberOfThreads; i++) {
            System.arraycopy(arr, k, arrOfArr[i], 0,SIZE/numberOfThreads);
            k = k + SIZE/numberOfThreads;
            int finalI = i;
            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int j = 0; j < SIZE/numberOfThreads; j++) {
                        arrOfArr[finalI][j] = (float)(arrOfArr[finalI][j] * Math.sin(0.2f + j / 5) * Math.cos(0.2f + j / 5) * Math.cos(0.4f + j / 2));
                    }
                }
            });
            threads[i].start();
        }
        for (int i = 0; i < numberOfThreads; i++) {
            threads[i].start();
        }
        for (int i = 0; i < numberOfThreads; i++) {
            threads[i].join();
        }
        float [] resultArr = new float[SIZE];
        int t = 0;
        for (int i = 0; i < numberOfThreads; i++) {
            System.arraycopy(arrOfArr[i], 0, resultArr, 0, SIZE/numberOfThreads);
            t = t + numberOfThreads;
        }
        System.out.println(numberOfThreads + " : " + (System.currentTimeMillis() - a));
    }

    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < SIZE; i++) {arr[i] = 1;}
//        sampleArrTest(arr);
//        towThreadsArrayTest(arr);

        for (int i = 1; i < 150; i++) {
            optionThreadArrayTest(arr, i);
        }
    }
}

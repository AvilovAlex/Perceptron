import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class AvilovRecognize {

    private static final int NUMBER_OF_INPUTS = 28;
    private static final int NUMBER_OF_ITERATIONS = 10;

    public static void main(String[] args) {
        AvilovRecognize AvilovRecognize = new AvilovRecognize();
        if (args.length == 0) {
            System.out.println("Слишком мало аргументов командной строки");
            System.exit(0);
        }
        else if (args[0].equals("-обучение")) {
            ArrayList<String> imagesOne = new ArrayList<>();
            ArrayList<String> imagesTwo = new ArrayList<>();

            int i = 2;
            for (; args[i+2].charAt(0) != '-'; i++)
                imagesOne.add(args[i + 2]);

            i += 2;
            String objOne = args[1];
            String objTwo = args[i++];

            for (; i + 2 < args.length; i++)
                imagesTwo.add(args[i + 2]);

            AvilovRecognize.learn(objOne, objTwo, imagesOne, imagesTwo, NUMBER_OF_ITERATIONS);
            for (int j = 0; j<args.length; j++)
              System.out.println(args[j]);
        }
        else if (args[0].equals("-reset")) {
            Perceptron.getInit(NUMBER_OF_INPUTS).reset();
            System.out.println("Обучение выполнено");
        }
        else {
            AvilovRecognize.feedPicture(args[0]);
        }

    }

    /* Берем только одно изображение что-бы определить что за объект на картинке*/
    private void feedPicture(String imagePath) {
        double[] inputs = openImage(imagePath);
        int assumedOutput = calculate(inputs, NUMBER_OF_INPUTS);
        Scanner reader = new Scanner(System.in);

        if (assumedOutput == -1) {
            System.out.println("Это " + Perceptron.getInit(NUMBER_OF_INPUTS).getObjOne() + "? Y\\N");
            String answer = reader.nextLine();
            if (answer.equals("N") || answer.equals("n"))
                relearn(1, assumedOutput, inputs, NUMBER_OF_INPUTS);
        }
        else {
            System.out.println("Это " + Perceptron.getInit(NUMBER_OF_INPUTS).getObjTwo() + "? Y\\N");
            String answer = reader.nextLine();
            if (answer.equals("N") || answer.equals("n"))
                relearn(-1, assumedOutput, inputs, NUMBER_OF_INPUTS);
        }
    }

     /* Сеть учится на объектах, которые показаны в наборе пройденных изображений, представленых через -1 или 1*/
    private void learn(String objOne, String objTwo, ArrayList<String> imagesOne, ArrayList<String> imagesTwo, int numberOfIterations) {
        Perceptron.getInit(NUMBER_OF_INPUTS).setObjOne(objOne);
        Perceptron.getInit(NUMBER_OF_INPUTS).setObjTwo(objTwo);
        System.out.println("\n!!!!!!" + objOne);
        for (int i = 0; i < imagesOne.size(); i++) {
            double[] inputs = openImage(imagesOne.get(i));
            calculate(inputs, NUMBER_OF_INPUTS, -1);
        }
        System.out.println("\n!!!!!!" + objTwo);
        for (int i = 0; i < imagesTwo.size(); i++) {
            double[] inputs = openImage(imagesTwo.get(i));
            calculate(inputs, NUMBER_OF_INPUTS, 1);
        }
        if (numberOfIterations > 0)
            learn(objOne, objTwo, imagesOne, imagesTwo, numberOfIterations - 1);
    }

    /* Открываем изображение и преобразуем в нормализованный массив Double*/
    private double[] openImage(String image_path) {
        try {
            //System.out.println(image_path);
            BufferedImage img = ImageIO.read(new File(image_path));
            int width = img.getWidth();
            int height = img.getHeight();
            int[][] grayImg = new int[width][height];
            convertToGrayscale(img, width, height, grayImg);
            double[] normalized = new double[NUMBER_OF_INPUTS];
            normalize(width, height, grayImg, normalized);
            return normalized;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /* Нормализуем изображение */
    private void normalize(int width, int height, int[][] grayImg, double[] normalized) {
        int[] bins = new int[NUMBER_OF_INPUTS];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (0 != getBin(grayImg[x][y])) {
                    int bin = getBin(grayImg[x][y]);
                    bins[bin-1] += 1;
                }
            }
        }

        double all = 0;
        for (int i = 0; i < NUMBER_OF_INPUTS; i++) {
            all += bins[i];
        }

        for (int i = 0; i < NUMBER_OF_INPUTS; i++) {
            normalized[i] = bins[i] / all;
        }
    }

    /* Преобзуем в градации серого */
    private void convertToGrayscale(BufferedImage img, int width, int height, int[][] grayImage) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int p = img.getRGB(x, y);
                int r = (p>>16)&0xff;
                int g = (p>>8)&0xff;
                int b = p&0xff;
                int avg = (r + g + b) / 3;
                grayImage[x][y] = avg;
            }
        }
    }

    public void calculate(double[] inputs, int capacity, int answer) {
        int assumedOutput = Perceptron.getInit(capacity).determine(Perceptron.getInit(capacity).sumUp(inputs));
        if (answer != assumedOutput) {
            Perceptron.getInit(capacity).relearn(answer, inputs, capacity);
        }
    }

    /* Обработка одного изображения */
    public int calculate(double[] inputs, int capacity) {
        return Perceptron.getInit(capacity).determine(Perceptron.getInit(capacity).sumUp(inputs));
    }

    /* Процедура переучивания */
    public void relearn(int answer, int assumedOutput, double[] inputs, int capacity) {
        Perceptron.getInit(capacity).relearn(answer, inputs, capacity);
    }

    private int getBin(int value){
      int bin = 1;
      if (value <= 225) {bin = (value/8) + 1;}
      if ((value >= 224)&&(value <= 225))
          bin = 28;
      return bin;
    }
}

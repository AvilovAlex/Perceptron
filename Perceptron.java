import java.io.*;
import java.util.concurrent.ThreadLocalRandom;

public class Perceptron implements Serializable {

    private static final String FILE_NAME = "perceptron.ser";
    private double[] weights;
    private double theta;
    private String objOne;
    private String objTwo;
    private static Perceptron perceptron;
    private double learningRate;

    private Perceptron() {
    }

    public static Perceptron getInit(int capacity) {
        perceptron = open();
        if (null == perceptron) {
            perceptron = new Perceptron();
            perceptron.generate(capacity);
            save();
        }
        return perceptron;
    }

    /* Генерируем новый ГЛУПЫЙ Перцептрон*/
    public void generate(int capacity) {
        weights = new double[capacity];
        for (int i = 0; i < capacity; i++)
            weights[i] = ThreadLocalRandom.current().nextDouble(-0.9, 0.9 + 1);
        theta = ThreadLocalRandom.current().nextDouble(-0.9, 0.9 + 1);
        learningRate = ThreadLocalRandom.current().nextDouble(0.7, 1 + 1);
        if (learningRate > 1) {
            learningRate -= (learningRate - 1);
        }
        else if (learningRate < 0) {
            learningRate = 0.7;
        }
    }

    public double sumUp(double[] inputs) {
        double sum = 0;
        for (int i = 0; i < weights.length; i++)
            sum += weights[i] * inputs[i];
        sum += theta;
        return sum;
    }

    public int determine(double sum) {
        if (sum <= theta) {
            return -1;
        }
        return 1;
    }

    public void relearn(int answer, double[] inputs, int capacity) {
        theta += answer * learningRate;
        System.out.print("Theta: " + theta + "\t");
        for (int i = 0; i < capacity; i++) {
            weights[i] += answer * inputs[i] * learningRate;
            System.out.print("w" + i + " " + weights[i] + " ");
        }
        if (learningRate > 0.1)
            learningRate -= 0.05;
        System.out.println("Relearned");
        save();
    }

     /* Открываем сохранненый Перцептрон с весами => TRUE. Если ]айла Перцептрона не найдено => FALSE*/
    private static Perceptron open() {
        try {
            ObjectInputStream os = new ObjectInputStream(new FileInputStream(FILE_NAME));
            Perceptron tmp = (Perceptron) os.readObject();
            os.close();
            return tmp;
        } catch (IOException | ClassNotFoundException e) {
            return null;
        }
    }

    public static void save() {
        try {
            ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(FILE_NAME));
            os.writeObject(perceptron);
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setObjOne(String name) {
        objOne = name;
        save();
    }

    public void setObjTwo(String name) {
        objTwo = name;
        save();
    }

    public String getObjOne() {
        return objOne;
    }

    public String getObjTwo() {
        return objTwo;
    }

    public void reset() {
        try{
            File file = new File(FILE_NAME);
            file.delete();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}

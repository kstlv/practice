import java.io.*;
import java.util.*;

public class Main {
    private static final String inputFile = "абоненты.csv", outputFileUsers = "Начисления_абоненты.csv", outputFileHouses = "Начисления_дома.csv";
    private static final String splitChar = ";"; // Символ, используемый для разделения столбцов в csv-файлах
    private static StringBuilder fileText;

    // Для расчетов использован тип double для быстрой производительности, но лучше использовать тип decimal для получения более точных результатов

    public static void main(String[] args) {
        System.out.println("\nНачисления для абонентов:\n");

        // Передаем файл в метод, чтобы подсчитать начисления для абонентов
        calculateUsers(new File(inputFile));

        System.out.println("\n---\n\nНачисления по домам:");

        // Используем данные, полученные от предыдущего метода для подсчета начислений по домам
        calculateHouses(fileText.toString());
    }

    // Подсчет начислений для абонентов
    private static void calculateUsers(File file){
        if (!fileExist(file)) return; // Выходим из метода, если файл не существует

        final double normative = 301.26; // Норматив
        final double valuePerUnit = 1.52; // Цена за единицу начисления по счетчику
        boolean currentLineIsFirst = true;
        fileText = new StringBuilder();

        BufferedReader reader;

        try {
            reader = new BufferedReader(new FileReader(inputFile));
            String line = reader.readLine();

            while (line != null) {
                String[] columns = line.split(splitChar); // Разделяем строку по столбцам

                Type type = Type.UNKNOWN;
                switch (columns[5]) {
                    case "1": // Начисления по нормативу
                        type = Type.NORMATIVE;
                        break;
                    case "2": // Начисления по счетчику
                        type = Type.COUNTER;
                        break;
                }

                double calculation = 0;

                switch (type) {
                    case NORMATIVE:
                        calculation = normative;
                        break;
                    case COUNTER:
                        double current = Double.parseDouble(columns[7]); // Текущее
                        double previous = Double.parseDouble(columns[6]); // Предыдущее
                        double difference = current - previous; // Разница
                        calculation = difference * valuePerUnit; // Итог
                        break;
                }

                // Если текущая строка первая, то добавляем новый заголовок для нового столбца таблицы.
                // В случае, если строка не первая, то заполняем содержимое таблицы данными.
                String currentLine = line + splitChar + (currentLineIsFirst ? "Начислено" : calculation);
                System.out.println(currentLine);
                fileText.append(currentLine).append("\n");

                line = reader.readLine(); // Читаем след. строку
                currentLineIsFirst = false; // След. строка уже не будет являться первой
            }

            reader.close();

            saveFile(outputFileUsers, fileText.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Подсчет начислений по домам
    private static void calculateHouses(String inputData){
        if (inputData.length() == 0) return; // Выходим из метода, если в него переданы пустые данные

        List<House> houses = new ArrayList<>(); // Список объектов типа Дом

        Scanner scanner = new Scanner(inputData);
        scanner.nextLine(); // Пропускаем строку с заголовками
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] columns = line.split(splitChar); // Разделяем строку по столбцам

            // Добавляем дом в список
            houses.add(new House(columns[2] + splitChar + columns[3], Double.parseDouble(columns[8])));
        }
        scanner.close();

        // Объединяем дома с одинаковыми адресами, считаем сумму
        Map<String, Double> housesTotal = new HashMap<>();
        for (House house : houses) {
            double sum = housesTotal.containsKey(house.address) ? housesTotal.get(house.address) : 0;
            sum += house.value;
            housesTotal.put(house.address, sum);
        }

        final String header = "№ строки;Улица;№ дома;Начислено";
        System.out.println("\n" + header);
        fileText = new StringBuilder(header + "\n");

        int number = 1;
        for (String key : housesTotal.keySet()) {
            String currentLine = number + splitChar + key + splitChar + housesTotal.get(key);
            number++;

            System.out.println(currentLine);
            fileText.append(currentLine).append("\n");
        }

        saveFile(outputFileHouses, fileText.toString());
    }

    // Метод для проверки наличия файла
    private static boolean fileExist(File file){
        if (file.exists() && !file.isDirectory()){
            return true;
        } else {
            System.out.println("Файл \"" + file.getName() + "\" не найден.");
            return false;
        }
    }

    // Метод, реализующий сохранение текста в файл
    private static void saveFile(String fileName, String text){
        if(fileName.length() != 0 && text.length() != 0){
            try (PrintWriter printWriter = new PrintWriter(fileName)) {
                printWriter.println(text);
                System.out.println("\nСохранен файл: \"" + fileName + "\".");
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
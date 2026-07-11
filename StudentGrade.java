import java.util.Scanner;
import java.util.ArrayList;

public class StudentGrade {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        ArrayList<String> studentNames = new ArrayList<>();
        ArrayList<Double> grades= new ArrayList<>();

        System.out.println("========================================");
        System.out.println("    WELCOME TO STUDENT GRADE TRACKER    ");
        System.out.println("========================================");

        System.out.print("Enter the total number of students: ");
        int totalStudents = sc.nextInt();
        sc.nextLine();

        for(int i = 0; i < totalStudents; i++) {
            System.out.println("\n--- Entering Details for Student "+ (i + 1) + " ---");

            System.out.print("Enter Student Name: ");
            String name = sc.nextLine();
            studentNames.add(name);

            System.out.print("Enter grade/mark for " + name + ": ");
            double mark = sc.nextDouble();
            grades.add(mark);
            sc.nextLine();
        }

        double sum = 0;
        double highest = grades.get(0);
        double lowest = grades.get(0);

        String highestStudent = studentNames.get(0);
        String lowestStudent = studentNames.get(0);

        for(int i = 0; i < totalStudents; i++) {
            double currentMark = grades.get(i);
            sum += currentMark;

            if(currentMark > highest) {
                highest = currentMark;
                highestStudent = studentNames.get(i);
            }  

            if(currentMark < lowest) {
                lowest = currentMark;
                lowestStudent = studentNames.get(i);
            }     

        }

        double average = sum / totalStudents;

        System.out.println("\n=======================================");
        System.out.println("              SUMMARY REPORT           ");
        System.out.println("=======================================");
        System.out.println("Total Students Processed : " + totalStudents);
        System.out.println("Average Class Score      : " + String.format("%.2f", average));
        System.out.println("Highest Score            : " + highest + " (By: " + highestStudent + ")");
        System.out.println("Lowest Score             : " + lowest + " (By: " + lowestStudent + ")");
        System.out.println("=======================================");

        sc.close();
        
    }
}
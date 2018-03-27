package cs1635.gradebuddy.activities;

import java.util.HashMap;
import java.util.List;

import cs1635.gradebuddy.models.Course;

/**
 * Created by akj26 on 3/20/18.
 * This works with the Junit Test and shows grade calculations for both the overall grade in one class and the current gpa for all classes
 * This does not work with the database, as that seemed more dependent on the activity pages and how those are set up,
 * but these do work for grade calculations and can be integrated with the database, based on how everything will be set up
 * and ow the individual pages are created
 */

public class Calculations {

    /**Calculates the Current GPA
     * Each grade a student has for their classes for the semester are stored as letter grade in the database
     * Here, a hash map stores grades in general and their associated weight
     * As the current grades are continued to be read in from the database, they are matched to their associated weight,
     * the weight of the grade is added to the total for the GPA and then
     */


    final double sectionMark;
    final double weight;

    public Calculations(double sectionMark, double weight) {
        this.sectionMark = sectionMark;
        this.weight = weight;
    }

    public double getTotal() {
        return sectionMark * weight / 100;
    }

    public static double getAverage(Calculations[] arr) {
        double sum = 0;
        for (int i=0; i < arr.length; i++) sum += arr[i].getTotal();
        return sum / arr.length;
    }

    /** The Calculations method works with the following code, given below as an EXAMPLE
     * Whoever, does the cacluclate, can do the firehouse portion:
     * public static void main(String[] args) {
     //in these brief basic example, user input for the mark and grade: where mark represents the grade in a particular secition nad weight is the percentage
     //ie overall quizzes mark = 80, and weight = worth 10%
     Scanner input = new Scanner(System.in);
     int counter = input.nextInt();
     Calcuations[] arr = new Calcuations[counter];
     for (int i = 0 ; i < counter ; i++) {
     double sectionMark = input.nextDouble();
     double weight = input.nextDouble();
     arr[i] = new Exam(mark, weight);
     }
     System.out.println("The average mark for your assignments/exams is "+ Calculations.getAverage(arr));
     }
     * */


    /**CurrentGPA Caclucations calcuates the gpa for a current term
     * The method does not work with the database, I figured that could be implemented by whomever is doing that page because it depends on how the data is set up
     * and how the page will read in the data
     * */
    public double currentGPACalculatons (List<String> courses){
        HashMap<String, Double> grades = new HashMap<>();
        String[] letterGrades = new String[courses.size()]; //stores the letter grades for the current semster
        int index = 0;
        for(String grade : courses) {
            letterGrades[index] = grade;
            index++;
        }
        //put the GPA grades that are possible in the HashMap
        grades.put("A",4.00);
        grades.put("A-",3.67);
        grades.put("B+",3.33);
        grades.put("B",3.00);
        grades.put("B-",2.67);
        grades.put("C+",2.33);
        grades.put("C",2.00);
        grades.put("C-",1.67);
        grades.put("D",1.00);
        grades.put("F",0.00);

        // read grades from an array; store the current grades as letter Grades in an array  and then use the values to calculate the  gpa
        double totalGPA = 0.0; //used to store the total current GPA
        for(int i = 0; i <letterGrades.length; i++)
        {
            String grade =letterGrades[i];

            //gets the associated grade value from the hashmap and adds it to the totalGPA
            double value = grades.get(grade);
            totalGPA += value;
        }

        //calculates the overall GPA by dividing the gpa, by the amount of letter Grades
        double gpa = totalGPA / letterGrades.length;
        return gpa;
    }

}
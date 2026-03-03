import java.sql.*;
import java.util.Scanner;

public class Main {

    static String url = "jdbc:mysql://localhost:3306/studentdb";
    static String username = "root";
    static String password = "password";   // change if needed

    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {

        try (Connection conn = DriverManager.getConnection(url, username, password)) {

            while (true) {

                System.out.println("\n===== STUDENT MANAGEMENT SYSTEM =====");
                System.out.println("1. Add Student");
                System.out.println("2. View Students");
                System.out.println("3. Update Student");
                System.out.println("4. Delete Student");
                System.out.println("5. Show Average Marks Per Course");
                System.out.println("6. Exit");
                System.out.print("Enter choice: ");

                int choice = sc.nextInt();
                sc.nextLine();

                switch (choice) {

                    case 1:
                        addStudent(conn);
                        break;

                    case 2:
                        viewStudents(conn);
                        break;

                    case 3:
                        updateStudent(conn);
                        break;

                    case 4:
                        deleteStudent(conn);
                        break;

                    case 5:
                        showAverageMarks(conn);
                        break;

                    case 6:
                        System.out.println("Exiting...");
                        return;

                    default:
                        System.out.println("Invalid choice!");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= ADD =================
    public static void addStudent(Connection conn) throws Exception {

        System.out.print("Enter Name: ");
        String name = sc.nextLine();

        System.out.print("Enter Email: ");
        String email = sc.nextLine();

        System.out.print("Enter Marks: ");
        int marks = sc.nextInt();

        System.out.print("Enter Course ID (1-Java, 2-Python, 3-Data Science): ");
        int courseId = sc.nextInt();

        String query = "INSERT INTO students(name,email,marks,course_id) VALUES(?,?,?,?)";
        PreparedStatement ps = conn.prepareStatement(query);

        ps.setString(1, name);
        ps.setString(2, email);
        ps.setInt(3, marks);
        ps.setInt(4, courseId);

        ps.executeUpdate();
        System.out.println("Student Added Successfully!");
    }

    // ================= VIEW (JOIN) =================
    public static void viewStudents(Connection conn) throws Exception {

        String query = "SELECT s.id, s.name, s.email, s.marks, c.course_name " +
                       "FROM students s " +
                       "JOIN courses c ON s.course_id = c.course_id";

        PreparedStatement ps = conn.prepareStatement(query);
        ResultSet rs = ps.executeQuery();

        System.out.println("\nID | Name | Email | Marks | Course");
        System.out.println("---------------------------------------------------");

        while (rs.next()) {
            System.out.println(
                    rs.getInt("id") + " | " +
                    rs.getString("name") + " | " +
                    rs.getString("email") + " | " +
                    rs.getInt("marks") + " | " +
                    rs.getString("course_name"));
        }
    }

    // ================= UPDATE =================
    public static void updateStudent(Connection conn) throws Exception {

        System.out.print("Enter Student ID: ");
        int id = sc.nextInt();
        sc.nextLine();

        System.out.print("Enter New Email: ");
        String email = sc.nextLine();

        System.out.print("Enter New Marks: ");
        int marks = sc.nextInt();

        String query = "UPDATE students SET email=?, marks=? WHERE id=?";
        PreparedStatement ps = conn.prepareStatement(query);

        ps.setString(1, email);
        ps.setInt(2, marks);
        ps.setInt(3, id);

        int rows = ps.executeUpdate();

        if (rows > 0)
            System.out.println("Student Updated Successfully!");
        else
            System.out.println("Student Not Found!");
    }

    // ================= DELETE =================
    public static void deleteStudent(Connection conn) throws Exception {

        System.out.print("Enter Student ID: ");
        int id = sc.nextInt();

        String query = "DELETE FROM students WHERE id=?";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setInt(1, id);

        int rows = ps.executeUpdate();

        if (rows > 0)
            System.out.println("Student Deleted Successfully!");
        else
            System.out.println("Student Not Found!");
    }

    // ================= AGGREGATE (GROUP BY + AVG) =================
    public static void showAverageMarks(Connection conn) throws Exception {

        String query = "SELECT c.course_name, AVG(s.marks) AS avg_marks " +
                       "FROM students s " +
                       "JOIN courses c ON s.course_id = c.course_id " +
                       "GROUP BY c.course_name";

        PreparedStatement ps = conn.prepareStatement(query);
        ResultSet rs = ps.executeQuery();

        System.out.println("\nCourse | Average Marks");
        System.out.println("----------------------------");

        while (rs.next()) {
            System.out.println(
                    rs.getString("course_name") + " | " +
                    rs.getDouble("avg_marks"));
        }
    }
}